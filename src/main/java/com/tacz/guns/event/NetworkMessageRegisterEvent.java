package com.tacz.guns.event;

import com.tacz.guns.GunMod;
import com.tacz.guns.network.message.*;
import com.tacz.guns.network.message.event.*;
import com.tacz.guns.network.message.handshake.Acknowledge;
import com.tacz.guns.network.message.handshake.ServerMessageSyncedEntityDataMapping;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.HandlerThread;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = GunMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class NetworkMessageRegisterEvent {

    /**
     * 注册网络包处理器事件
     */
    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        // 协议版本"1"
        final PayloadRegistrar registrar = event.registrar("1.0.4").executesOn(HandlerThread.MAIN);
        // 以下处理器在主线程执行
        registrar.playBidirectional(
                Acknowledge.TYPE, Acknowledge.STREAM_CODEC,
                new DirectionalPayloadHandler<>(Acknowledge::handle, Acknowledge::handle)
        );
        registrar.playBidirectional(
                ServerMessageSyncedEntityDataMapping.TYPE, ServerMessageSyncedEntityDataMapping.STREAM_CODEC,
                new DirectionalPayloadHandler<>(ServerMessageSyncedEntityDataMapping::handle, ServerMessageSyncedEntityDataMapping::handle)
        );
        registrar.playBidirectional(
                ServerMessageGunDraw.TYPE, ServerMessageGunDraw.STREAM_CODEC,
                new DirectionalPayloadHandler<>(ServerMessageGunDraw::clientHandler, ServerMessageGunDraw::serverHandler)
        );
        registrar.playBidirectional(
                ServerMessageGunFire.TYPE, ServerMessageGunFire.STREAM_CODEC,
                new DirectionalPayloadHandler<>(ServerMessageGunFire::clientHandler, ServerMessageGunFire::serverHandler)
        );
        registrar.playBidirectional(
                ServerMessageGunFireSelect.TYPE, ServerMessageGunFireSelect.STREAM_CODEC,
                new DirectionalPayloadHandler<>(ServerMessageGunFireSelect::clientHandler, ServerMessageGunFireSelect::serverHandler)
        );
        registrar.playBidirectional(
                ServerMessageGunHurt.TYPE, ServerMessageGunHurt.STREAM_CODEC,
                new DirectionalPayloadHandler<>(ServerMessageGunHurt::clientHandler, ServerMessageGunHurt::serverHandler)
        );
        registrar.playBidirectional(
                ServerMessageGunKill.TYPE, ServerMessageGunKill.STREAM_CODEC,
                new DirectionalPayloadHandler<>(ServerMessageGunKill::clientHandler, ServerMessageGunKill::serverHandler)
        );
        registrar.playBidirectional(
                ServerMessageGunMelee.TYPE, ServerMessageGunMelee.STREAM_CODEC,
                new DirectionalPayloadHandler<>(ServerMessageGunMelee::clientHandler, ServerMessageGunMelee::serverHandler)
        );
        registrar.playBidirectional(
                ServerMessageGunReload.TYPE, ServerMessageGunReload.STREAM_CODEC,
                new DirectionalPayloadHandler<>(ServerMessageGunReload::clientHandler, ServerMessageGunReload::serverHandler)
        );
        registrar.playBidirectional(
                ServerMessageGunShoot.TYPE, ServerMessageGunShoot.STREAM_CODEC,
                new DirectionalPayloadHandler<>(ServerMessageGunShoot::clientHandler, ServerMessageGunShoot::serverHandler)
        );
        registrar.playBidirectional(
                ClientMessageCraft.TYPE, ClientMessageCraft.STREAM_CODEC,
                new DirectionalPayloadHandler<>(ClientMessageCraft::clientHandler, ClientMessageCraft::serverHandler)
        );
        registrar.playBidirectional(
                ClientMessagePlayerAim.TYPE, ClientMessagePlayerAim.STREAM_CODEC,
                new DirectionalPayloadHandler<>(ClientMessagePlayerAim::clientHandler, ClientMessagePlayerAim::serverHandler)
        );
        registrar.playBidirectional(
                ClientMessagePlayerBoltGun.TYPE, ClientMessagePlayerBoltGun.STREAM_CODEC,
                new DirectionalPayloadHandler<>(ClientMessagePlayerBoltGun::clientHandler, ClientMessagePlayerBoltGun::serverHandler)
        );
        registrar.playBidirectional(
                ClientMessagePlayerCancelReload.TYPE, ClientMessagePlayerCancelReload.STREAM_CODEC,
                new DirectionalPayloadHandler<>(ClientMessagePlayerCancelReload::clientHandler, ClientMessagePlayerCancelReload::serverHandler)
        );
        registrar.playBidirectional(
                ClientMessagePlayerCrawl.TYPE, ClientMessagePlayerCrawl.STREAM_CODEC,
                new DirectionalPayloadHandler<>(ClientMessagePlayerCrawl::clientHandler, ClientMessagePlayerCrawl::serverHandler)
        );
        registrar.playBidirectional(
                ClientMessagePlayerDrawGun.TYPE, ClientMessagePlayerDrawGun.STREAM_CODEC,
                new DirectionalPayloadHandler<>(ClientMessagePlayerDrawGun::clientHandler, ClientMessagePlayerDrawGun::serverHandler)
        );
        registrar.playBidirectional(
                ClientMessagePlayerFireSelect.TYPE, ClientMessagePlayerFireSelect.STREAM_CODEC,
                new DirectionalPayloadHandler<>(ClientMessagePlayerFireSelect::clientHandler, ClientMessagePlayerFireSelect::serverHandler)
        );
        registrar.playBidirectional(
                ClientMessagePlayerMelee.TYPE, ClientMessagePlayerMelee.STREAM_CODEC,
                new DirectionalPayloadHandler<>(ClientMessagePlayerMelee::clientHandler, ClientMessagePlayerMelee::serverHandler)
        );
        registrar.playBidirectional(
                ClientMessagePlayerReloadGun.TYPE, ClientMessagePlayerReloadGun.STREAM_CODEC,
                new DirectionalPayloadHandler<>(ClientMessagePlayerReloadGun::clientHandler, ClientMessagePlayerReloadGun::serverHandler)
        );
        registrar.playBidirectional(
                ClientMessagePlayerShoot.TYPE, ClientMessagePlayerShoot.STREAM_CODEC,
                new DirectionalPayloadHandler<>(ClientMessagePlayerShoot::clientHandler, ClientMessagePlayerShoot::serverHandler)
        );
        registrar.playBidirectional(
                ClientMessagePlayerZoom.TYPE, ClientMessagePlayerZoom.STREAM_CODEC,
                new DirectionalPayloadHandler<>(ClientMessagePlayerZoom::clientHandler, ClientMessagePlayerZoom::serverHandler)
        );
        registrar.playBidirectional(
                ClientMessageRefitGun.TYPE, ClientMessageRefitGun.STREAM_CODEC,
                new DirectionalPayloadHandler<>(ClientMessageRefitGun::clientHandler, ClientMessageRefitGun::serverHandler)
        );
        registrar.playBidirectional(
                ClientMessageSyncBaseTimestamp.TYPE, ClientMessageSyncBaseTimestamp.STREAM_CODEC,
                new DirectionalPayloadHandler<>(ClientMessageSyncBaseTimestamp::clientHandler, ClientMessageSyncBaseTimestamp::serverHandler)
        );
        registrar.playBidirectional(
                ClientMessageUnloadAttachment.TYPE, ClientMessageUnloadAttachment.STREAM_CODEC,
                new DirectionalPayloadHandler<>(ClientMessageUnloadAttachment::clientHandler, ClientMessageUnloadAttachment::serverHandler)
        );
        registrar.playBidirectional(
                ServerMessageCraft.TYPE, ServerMessageCraft.STREAM_CODEC,
                new DirectionalPayloadHandler<>(ServerMessageCraft::clientHandler, ServerMessageCraft::serverHandler)
        );
        registrar.playBidirectional(
                ServerMessageLevelUp.TYPE, ServerMessageLevelUp.STREAM_CODEC,
                new DirectionalPayloadHandler<>(ServerMessageLevelUp::clientHandler, ServerMessageLevelUp::serverHandler)
        );
        registrar.playBidirectional(
                ServerMessageRefreshRefitScreen.TYPE, ServerMessageRefreshRefitScreen.STREAM_CODEC,
                new DirectionalPayloadHandler<>(ServerMessageRefreshRefitScreen::clientHandler, ServerMessageRefreshRefitScreen::serverHandler)
        );
        registrar.playBidirectional(
                ServerMessageSound.TYPE, ServerMessageSound.STREAM_CODEC,
                new DirectionalPayloadHandler<>(ServerMessageSound::clientHandler, ServerMessageSound::serverHandler)
        );
        registrar.playBidirectional(
                ServerMessageSwapItem.TYPE, ServerMessageSwapItem.STREAM_CODEC,
                new DirectionalPayloadHandler<>(ServerMessageSwapItem::clientHandler, ServerMessageSwapItem::serverHandler)
        );
        registrar.playBidirectional(
                ServerMessageSyncGunPack.TYPE, ServerMessageSyncGunPack.STREAM_CODEC,
                new DirectionalPayloadHandler<>(ServerMessageSyncGunPack::clientHandler, ServerMessageSyncGunPack::serverHandler)
        );
        registrar.playBidirectional(
                ServerMessageUpdateEntityData.TYPE, ServerMessageUpdateEntityData.STREAM_CODEC,
                new DirectionalPayloadHandler<>(ServerMessageUpdateEntityData::clientHandler, ServerMessageUpdateEntityData::serverHandler)
        );
        registrar.executesOn(HandlerThread.NETWORK);
        // 以下处理器在网络线程执行
        registrar.playBidirectional(
                ServerMessageSyncBaseTimestamp.TYPE, ServerMessageSyncBaseTimestamp.STREAM_CODEC,
                new DirectionalPayloadHandler<>(ServerMessageSyncBaseTimestamp::clientHandler, ServerMessageSyncBaseTimestamp::serverHandler)
        );
    }

}
