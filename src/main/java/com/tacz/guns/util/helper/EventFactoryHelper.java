package com.tacz.guns.util.helper;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.ExplosionEvent;

import java.util.List;

@SuppressWarnings("JavadocReference")
public class EventFactoryHelper {

    /**
     * {@link net.minecraftforge.event.ForgeEventFactory#onExplosionDetonate}
     */
    public static void onExplosionDetonate(Level level, Explosion explosion, List<Entity> list, double diameter) {
        //Filter entities to only those who are effected, to prevent modders from seeing more then will be hurt.
        /* Enable this if we get issues with modders looping to much.
        Iterator<Entity> itr = list.iterator();
        Vec3 p = explosion.getPosition();
        while (itr.hasNext())
        {
            Entity e = itr.next();
            double dist = e.getDistance(p.xCoord, p.yCoord, p.zCoord) / diameter;
            if (e.isImmuneToExplosions() || dist > 1.0F) itr.remove();
        }
        */
        NeoForge.EVENT_BUS.post(new ExplosionEvent.Detonate(level, explosion, list));
    }

    /**
     * {@link net.minecraftforge.event.ForgeEventFactory#onExplosionStart}
     */
    public static boolean onExplosionStart(Level level, Explosion explosion) {
        return NeoForge.EVENT_BUS.post(new ExplosionEvent.Start(level, explosion)).isCanceled();
    }

}
