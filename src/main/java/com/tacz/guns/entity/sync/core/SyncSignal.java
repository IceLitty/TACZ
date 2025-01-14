package com.tacz.guns.entity.sync.core;

import org.jetbrains.annotations.Nullable;

/**
 * Author: MrCrayfish
 * Open source at <a href="https://github.com/MrCrayfish/Framework">Github</a> under LGPL License.
 */
public final class SyncSignal
{
    private final Runnable markDirty;

    public SyncSignal(Runnable markDirty)
    {
        this.markDirty = markDirty;
    }

    public void tell()
    {
        this.markDirty.run();
    }

    public interface Consumer
    {
        void accept(@Nullable SyncSignal signal);
    }
}