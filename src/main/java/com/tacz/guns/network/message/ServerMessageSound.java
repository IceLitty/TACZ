package com.tacz.guns.network.message;

import com.tacz.guns.GunMod;
import com.tacz.guns.client.sound.SoundPlayManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ServerMessageSound(int entityId, ResourceLocation gunId, ResourceLocation gunDisplayId, String soundName,
                                 float volume, float pitch, int distance) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ServerMessageSound> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(GunMod.MOD_ID, "s2c_sound"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerMessageSound> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public ServerMessageSound decode(RegistryFriendlyByteBuf buf) {
            int entityId = buf.readVarInt();
            ResourceLocation gunId = buf.readResourceLocation();
            ResourceLocation gunDisplayId = buf.readResourceLocation();
            String soundName = buf.readUtf();
            float volume = buf.readFloat();
            float pitch = buf.readFloat();
            int distance = buf.readInt();
            return new ServerMessageSound(entityId, gunId, gunDisplayId, soundName, volume, pitch, distance);
        }
        @Override
        public void encode(RegistryFriendlyByteBuf buf, ServerMessageSound message) {
            buf.writeVarInt(message.entityId);
            buf.writeResourceLocation(message.gunId);
            buf.writeResourceLocation(message.gunDisplayId);
            buf.writeUtf(message.soundName);
            buf.writeFloat(message.volume);
            buf.writeFloat(message.pitch);
            buf.writeInt(message.distance);
        }
    };

    public static void clientHandler(final ServerMessageSound message, final IPayloadContext context) {
        context.enqueueWork(() -> SoundPlayManager.playMessageSound(message));
    }

    public static void serverHandler(final ServerMessageSound message, final IPayloadContext context) {
    }

    public int getEntityId() {
        return entityId;
    }

    public ResourceLocation getGunId() {
        return gunId;
    }

    public ResourceLocation getGunDisplayId() {
        return gunDisplayId;
    }

    public String getSoundName() {
        return soundName;
    }

    public float getVolume() {
        return volume;
    }

    public float getPitch() {
        return pitch;
    }

    public int getDistance() {
        return distance;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
