package com.tacz.guns.entity.sync;

import com.tacz.guns.api.entity.ReloadState;
import com.tacz.guns.entity.sync.core.DataSerializer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class ModSerializers {
    public static final DataSerializer<ReloadState> RELOAD_STATE = new DataSerializer<>(StreamCodec.composite(
            ByteBufCodecs.VAR_INT, po -> po.getStateType().ordinal(),
            ByteBufCodecs.VAR_LONG, ReloadState::getCountDown,
            (stateType, countDown) -> new ReloadState(ReloadState.StateType.values()[stateType], countDown)
    ), (reloadState, provider) -> {
        CompoundTag compound = new CompoundTag();
        compound.putString("StateType", reloadState.getStateType().toString());
        compound.putLong("CountDown", reloadState.getCountDown());
        return compound;
    }, (tag, provider) -> {
        CompoundTag compound = (CompoundTag) tag;
        try {
            ReloadState.StateType stateType = ReloadState.StateType.valueOf(compound.getString("StateType"));
            long countDown = compound.getLong("CountDown");
            ReloadState reloadState = new ReloadState();
            reloadState.setStateType(stateType);
            reloadState.setCountDown(countDown);
            return reloadState;
        } catch (IllegalArgumentException ignore) {
        }
        return new ReloadState();
    });
}
