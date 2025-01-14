package com.tacz.guns.network.message;

import com.tacz.guns.GunMod;
import com.tacz.guns.entity.sync.core.DataEntry;
import com.tacz.guns.entity.sync.core.SyncedEntityData;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;

public record ServerMessageUpdateEntityData(int entityId, List<DataEntry<?, ?>> entries) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ServerMessageUpdateEntityData> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(GunMod.MOD_ID, "s2c_update_entity_data"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerMessageUpdateEntityData> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public ServerMessageUpdateEntityData decode(RegistryFriendlyByteBuf buffer) {
            int entityId = buffer.readVarInt();
            int size = buffer.readVarInt();
            List<DataEntry<?, ?>> entries = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                entries.add(DataEntry.read(buffer));
            }
            return new ServerMessageUpdateEntityData(entityId, entries);
        }
        @Override
        public void encode(RegistryFriendlyByteBuf buffer, ServerMessageUpdateEntityData message) {
            buffer.writeVarInt(message.entityId);
            buffer.writeVarInt(message.entries.size());
            message.entries.forEach(entry -> entry.write(buffer));
        }
    };

    public static void clientHandler(final ServerMessageUpdateEntityData message, final IPayloadContext context) {
        context.enqueueWork(() -> onHandle(message));
    }

    public static void serverHandler(final ServerMessageUpdateEntityData message, final IPayloadContext context) {
    }

    @OnlyIn(Dist.CLIENT)
    private static void onHandle(ServerMessageUpdateEntityData message) {
        Level level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        Entity entity = level.getEntity(message.entityId);
        if (entity == null) {
            return;
        }
        SyncedEntityData instance = SyncedEntityData.instance();
        message.entries.forEach(entry -> instance.set(entity, entry.getKey(), entry.getValue()));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
