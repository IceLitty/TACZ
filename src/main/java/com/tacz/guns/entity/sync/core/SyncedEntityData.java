package com.tacz.guns.entity.sync.core;

import com.google.common.collect.ImmutableSet;
import com.mrcrayfish.framework.api.event.EntityEvents;
import com.mrcrayfish.framework.api.event.PlayerEvents;
import com.mrcrayfish.framework.api.event.TickEvents;
import com.tacz.guns.GunMod;
import com.tacz.guns.init.CommonRegistry;
import com.tacz.guns.init.ModAttachment;
import com.tacz.guns.network.message.ServerMessageUpdateEntityData;
import com.tacz.guns.network.message.handshake.ServerMessageSyncedEntityDataMapping;
import it.unimi.dsi.fastutil.ints.Int2ReferenceMap;
import it.unimi.dsi.fastutil.ints.Int2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * <p>Basically a clone of DataParameter system. It's not good to init custom data parameters to
 * other entities that aren't your own. It can cause mismatched ids and crash the game. This synced
 * data system attempts to solve the problem (at least for player entities) and allows data to be
 * easily synced to clients. The data can only be controlled on the logical server. Changing the
 * data on the logical client will have no affect on the server.</p>
 * <p></p>
 * <p>To use this system you first need to create a synced data key instance. This should be a public
 * static final field. You will need to specify an key id (based on your modid), the serializer, and
 * a default value supplier.</p>
 * <code>public static final SyncedDataKey&lt;Double&gt; CURRENT_SPEED = SyncedDataKey.create(new ResourceLocation("examplemod:speed"), Serializers.DOUBLE, () -> 0.0);</code>
 * <p></p>
 * <p>Next the key needs to be registered. This can simply be done in the common setup of your mod.</p>
 * <code>SyncedPlayerData.instance().registerKey(CURRENT_SPEED);</code>
 * <p></p>
 * <p>Then anywhere you want (as long as it's on the main thread), you can set the value by calling</p>
 * <code>SyncedPlayerData.instance().set(player, CURRENT_SPEED, 5.0);</code>
 * <p></p>
 * <p>The value can be retrieved on the server or client by calling</p>
 * <code>SyncedPlayerData.instance().get(player, CURRENT_SPEED);</code>
 * <p></p>
 * <p>Author: MrCrayfish</p>
 * Open source at <a href="https://github.com/MrCrayfish/Framework">Github</a> under LGPL License.
 */
public class SyncedEntityData {
    private static final Marker SYNCED_ENTITY_DATA_MARKER = MarkerManager.getMarker("SYNCED_ENTITY_DATA");
    private static SyncedEntityData instance;

    private final Set<SyncedClassKey<?>> registeredClassKeys = new HashSet<>();
    private final Object2ObjectMap<ResourceLocation, SyncedClassKey<?>> idToClassKey = new Object2ObjectOpenHashMap<>();
    private final Object2ObjectMap<String, SyncedClassKey<?>> classNameToClassKey = new Object2ObjectOpenHashMap<>();
    private final Map<String, Boolean> clientClassNameCapabilityCache = new ConcurrentHashMap<>();
    private final Map<String, Boolean> serverClassNameCapabilityCache = new ConcurrentHashMap<>();

    private final Set<SyncedDataKey<?, ?>> registeredDataKeys = new HashSet<>();
    private final Reference2ObjectMap<SyncedClassKey<?>, HashMap<ResourceLocation, SyncedDataKey<?, ?>>> classToKeys = new Reference2ObjectOpenHashMap<>();
    private final Reference2IntMap<SyncedDataKey<?, ?>> internalIds = new Reference2IntOpenHashMap<>();
    private final Int2ReferenceMap<SyncedDataKey<?, ?>> syncedIdToKey = new Int2ReferenceOpenHashMap<>();

    private final AtomicInteger nextIdTracker = new AtomicInteger();
    private final Set<Entity> pendingEntitiesForSync = new HashSet<>();
    private boolean needsSync = false;

    private SyncedEntityData() {
        // TODO just call lib function
        PlayerEvents.START_TRACKING_ENTITY.register(this::onStartTracking);
        EntityEvents.JOIN_LEVEL.register(this::onEntityJoinWorld);
        TickEvents.END_SERVER.register(this::onServerTickEnd);
        PlayerEvents.COPY.register(this::onPlayerClone);
    }

    public static SyncedEntityData instance() {
        if (instance == null) {
            instance = new SyncedEntityData();
        }
        return instance;
    }

    private <E extends Entity> void registerClassKey(SyncedClassKey<E> classKey) {
        if (!this.registeredClassKeys.contains(classKey)) {
            this.registeredClassKeys.add(classKey);
            this.idToClassKey.put(classKey.id(), classKey);
            this.classNameToClassKey.put(classKey.entityClass().getName(), classKey);
        }
    }

    /**
     * Registers a synced data key into the system.
     *
     * @param dataKey a synced data key instance
     */
    public synchronized <E extends Entity, T> void registerDataKey(SyncedDataKey<E, T> dataKey) {
        ResourceLocation keyId = dataKey.id();
        SyncedClassKey<E> classKey = dataKey.classKey();
        if (CommonRegistry.isLoadComplete()) {
            throw new IllegalStateException(String.format("Tried to register synced data key %s for %s after game initialization", keyId, classKey.id()));
        }
        if (this.registeredDataKeys.contains(dataKey)) {
            throw new IllegalArgumentException(String.format("The synced data key %s for %s is already registered", keyId, classKey.id()));
        }
        // Attempt to register the class key. Will ignore if already registered.
        this.registerClassKey(dataKey.classKey());
        this.registeredDataKeys.add(dataKey);
        this.classToKeys.computeIfAbsent(classKey, c -> new HashMap<>()).put(keyId, dataKey);
        int nextId = this.nextIdTracker.getAndIncrement();
        this.internalIds.put(dataKey, nextId);
        this.syncedIdToKey.put(nextId, dataKey);
        GunMod.LOGGER.info(SYNCED_ENTITY_DATA_MARKER, "Registered synced data key {} for {}", dataKey.id(), classKey.id());
    }

    /**
     * Sets the value of a synced data key to the specified player
     *
     * @param entity the player to assign the value to
     * @param key    a registered synced data key
     * @param value  a new value that matches the synced data key type
     */
    public <E extends Entity, T> void set(E entity, SyncedDataKey<?, ?> key, T value) {
        if (!this.registeredDataKeys.contains(key)) {
            String keys = this.registeredDataKeys.stream().map(k -> k.pairKey().toString()).collect(Collectors.joining(",", "[", "]"));
            GunMod.LOGGER.info(SYNCED_ENTITY_DATA_MARKER, "Registered keys before throwing exception: {}", keys);
            throw new IllegalArgumentException(String.format("The synced data key %s for %s is not registered!", key.id(), key.classKey().id()));
        }
        DataHolder holder = this.getDataHolder(entity);
        if (holder != null) {
            holder.set(key, value);
        }
    }

    /**
     * Gets the value for the synced data key from the specified player. It is best to check that
     * the player is alive before getting the value.
     *
     * @param entity the player to retrieve the data from
     * @param key    a registered synced data key
     */
    public <E extends Entity, T> T get(E entity, SyncedDataKey<E, T> key) {
        if (!this.registeredDataKeys.contains(key)) {
            String keys = this.registeredDataKeys.stream().map(k -> k.pairKey().toString()).collect(Collectors.joining(",", "[", "]"));
            GunMod.LOGGER.info(SYNCED_ENTITY_DATA_MARKER, "Registered keys before throwing exception: {}", keys);
            throw new IllegalArgumentException(String.format("The synced data key %s for %s is not registered!", key.id(), key.classKey().id()));
        }
        DataHolder holder = this.getDataHolder(entity);
        return holder != null ? holder.get(key) : key.defaultValueSupplier().get();
    }

    public <E extends Entity, T> void updateClientEntry(Entity entity, DataEntry<E, T> entry) {
        SyncedEntityData.instance().set(entity, entry.getKey(), entry.getValue());
    }

    public int getInternalId(SyncedDataKey<?, ?> key) {
        return this.internalIds.getInt(key);
    }

    SyncedClassKey<?> getClassKey(ResourceLocation id) {
        return this.idToClassKey.get(id);
    }

    Map<ResourceLocation, SyncedDataKey<?, ?>> getDataKeys(SyncedClassKey<?> key) {
        return this.classToKeys.get(key);
    }

    @Nullable
    SyncedDataKey<?, ?> getKey(int id) {
        return this.syncedIdToKey.get(id);
    }

    @Deprecated
    @Nullable
    public SyncedDataKey<?, ?> getKey(SyncedClassKey<?> classKey, ResourceLocation dataKey) {
        Map<ResourceLocation, SyncedDataKey<?, ?>> keys = SyncedEntityData.instance().classToKeys.get(classKey);
        if (keys == null) {
            return null;
        }
        return keys.get(dataKey);
    }

    public Set<SyncedDataKey<?, ?>> getKeys() {
        return ImmutableSet.copyOf(this.registeredDataKeys);
    }

    @Nullable
    public DataHolder getDataHolder(Entity entity) {
        return entity.getData(ModAttachment.DATA_HOLDER).setup(entity);
    }

    public boolean hasSyncedDataKey(Entity entity) {
        /* It's possible that the entity doesn't have a key, but it's superclass or subsequent does
         * have a synced data key. In order to prevent checking this every time we attach the
         * capability, a simple one time check can be performed then cache the result. */
        Class<? extends Entity> entityClass = entity.getClass();
        return this.getClassNameCapabilityCache(entity.level().isClientSide).computeIfAbsent(entityClass.getName(), c -> {
            Class<?> targetClass = entityClass;
            // Should be good enough
            while (!targetClass.isAssignableFrom(Entity.class)) {
                if (this.classNameToClassKey.containsKey(targetClass.getName())) {
                    return true;
                }
                targetClass = targetClass.getSuperclass();
            }
            return false;
        });
    }

    /**
     * Gets the class name capability cache for the effective side. This is needed to avoid
     * concurrency issue due to client and server threads; fast util does not support concurrent maps.
     */
    private Map<String, Boolean> getClassNameCapabilityCache(boolean client) {
        return client ? this.clientClassNameCapabilityCache : this.serverClassNameCapabilityCache;
    }

    private void onStartTracking(Entity target, Player player) {
        if (!player.level().isClientSide() && this.hasSyncedDataKey(target)) {
            DataHolder holder = this.getDataHolder(target);
            if (holder != null) {
                List<DataEntry<?, ?>> entries = holder.gatherAllTrackingDataEntries();
                entries.removeIf(entry -> !entry.getKey().syncMode().isTracking());
                if (!entries.isEmpty()) {
                    PacketDistributor.sendToPlayer((ServerPlayer) player, new ServerMessageUpdateEntityData(target.getId(), entries));
                }
            }
        }
    }

    private void onEntityJoinWorld(Entity entity, Level level, boolean disk) {
        if (entity instanceof Player player && !level.isClientSide() && this.hasSyncedDataKey(player)) {
            DataHolder holder = this.getDataHolder(player);
            if (holder != null) {
                List<DataEntry<?, ?>> entries = holder.gatherAllTrackingDataEntries();
                if (!entries.isEmpty()) {
                    PacketDistributor.sendToPlayer((ServerPlayer) player, new ServerMessageUpdateEntityData(player.getId(), entries));
                }
            }
        }
    }

    private void onPlayerClone(Player oldPlayer, Player newPlayer, boolean respawn) {
        if (!this.hasSyncedDataKey(newPlayer))
            return;
        DataHolder oldHolder = this.getDataHolder(oldPlayer); // , true
        if (oldHolder == null)
            return;
        DataHolder newHolder = this.getDataHolder(newPlayer);
        if (newHolder == null)
            return;
        RegistryAccess access = newPlayer.registryAccess();
        Map<SyncedDataKey<?, ?>, DataEntry<?, ?>> dataMap = new HashMap<>();
        oldHolder.dataMap.forEach((key, entry) -> {
            if (respawn || key.persistent()) {
                DataEntry<?, ?> newEntry = new DataEntry<>(newHolder, key);
                newEntry.readValue(entry.writeValue(access), access);
                dataMap.put(key, newEntry);
            }
        });
        newHolder.dataMap = dataMap;
    }

    private void onServerTickEnd(MinecraftServer server) {
        if (!this.needsSync)
            return;
        if (this.pendingEntitiesForSync.isEmpty()) {
            this.needsSync = false;
            return;
        }
        for (Entity entity : this.pendingEntitiesForSync) {
            // Don't sync entities that are removed
            if (entity.isRemoved())
                continue;
            DataHolder holder = this.getDataHolder(entity);
            if (holder == null || !holder.isPendingSync())
                continue;
            List<DataEntry<?, ?>> entries = holder.gatherPendingSyncDataEntries();
            if (entries.isEmpty())
                continue;
            List<DataEntry<?, ?>> selfEntries = entries.stream().filter(entry -> entry.getKey().syncMode().isSelf()).collect(Collectors.toList());
            if (!selfEntries.isEmpty() && entity instanceof ServerPlayer) {
                PacketDistributor.sendToPlayer((ServerPlayer) entity, new ServerMessageUpdateEntityData(entity.getId(), selfEntries));
            }
            List<DataEntry<?, ?>> trackingEntries = entries.stream().filter(entry -> entry.getKey().syncMode().isTracking()).collect(Collectors.toList());
            if (!trackingEntries.isEmpty()) {
                PacketDistributor.sendToPlayersTrackingEntity(entity, new ServerMessageUpdateEntityData(entity.getId(), trackingEntries));
            }
            holder.clearSync();
        }
        this.pendingEntitiesForSync.clear();
        this.needsSync = false;
    }

    public boolean updateMappings(ServerMessageSyncedEntityDataMapping message) {
        this.syncedIdToKey.clear();

        List<Pair<ResourceLocation, ResourceLocation>> missingKeys = new ArrayList<>();
        message.getKeyMap().forEach((classId, list) -> {
            SyncedClassKey<?> classKey = this.idToClassKey.get(classId);
            if (classKey == null || !this.classToKeys.containsKey(classKey)) {
                list.forEach(pair -> missingKeys.add(Pair.of(classId, pair.getLeft())));
                return;
            }

            Map<ResourceLocation, SyncedDataKey<?, ?>> keys = this.classToKeys.get(classKey);
            list.forEach(pair -> {
                SyncedDataKey<?, ?> syncedDataKey = keys.get(pair.getLeft());
                if (syncedDataKey == null) {
                    missingKeys.add(Pair.of(classId, pair.getLeft()));
                    return;
                }
                this.syncedIdToKey.put((int) pair.getRight(), syncedDataKey);
            });
        });

        if (!missingKeys.isEmpty()) {
            String keys = missingKeys.stream().map(Object::toString).collect(Collectors.joining(",", "[", "]"));
            GunMod.LOGGER.info(SYNCED_ENTITY_DATA_MARKER, "Received unknown synced keys: {}", keys);
        }

        return missingKeys.isEmpty();
    }

//    public List<S2CSyncedEntityData> getConfigurationMessages() {
//        Map<ResourceLocation, List<Pair<ResourceLocation, Integer>>> map = new HashMap<>();
//        this.getKeys().forEach(key -> {
//            int id = this.getInternalId(key);
//            map.computeIfAbsent(key.classKey().id(), c -> new ArrayList<>()).add(Pair.of(key.id(), id));
//        });
//        return List.of(new S2CSyncedEntityData(map));
//    }

    public boolean isDirty() {
        return this.needsSync;
    }

    public void setDirty(boolean dirty) {
        this.needsSync = dirty;
    }

    boolean markForSync(Entity entity) {
        if (entity != null && !entity.level().isClientSide() && !entity.isRemoved()) {
            this.needsSync = true;
            this.pendingEntitiesForSync.add(entity);
            return true;
        }
        return false;
    }

    public Set<Entity> getDirtyEntities() {
        return this.pendingEntitiesForSync;
    }
}
