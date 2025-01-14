package com.tacz.guns.entity.sync.core;

import org.jetbrains.annotations.Nullable;

/**
 * Author: MrCrayfish
 * Open source at <a href="https://github.com/MrCrayfish/Framework">Github</a> under LGPL License.
 */
public abstract class SyncedObject implements SyncSignal.Consumer {
    @Nullable
    private SyncSignal signal;

    @Override
    public void accept(@Nullable SyncSignal signal) {
        this.signal = signal;
    }

    protected final void markDirty() {
        if (this.signal != null) {
            this.signal.tell();
        }
    }
}