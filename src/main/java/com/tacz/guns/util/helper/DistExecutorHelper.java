package com.tacz.guns.util.helper;

import com.tacz.guns.GunMod;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

/**
 * {@link net.minecraftforge.fml.DistExecutor}
 */
@SuppressWarnings("JavadocReference")
public class DistExecutorHelper {

    public static void unsafeRunWhenOn(Dist dist, Supplier<Runnable> toRun) {
        if (dist == FMLEnvironment.dist) {
            toRun.get().run();
        }
    }

    public static void safeRunWhenOn(Dist dist, Supplier<SafeRunnable> toRun) {
        validateSafeReferent(toRun);
        if (dist == FMLEnvironment.dist)  {
            toRun.get().run();
        }
    }

    public interface SafeReferent {}

    public interface SafeCallable<T> extends SafeReferent, Callable<T>, Serializable {}

    public interface SafeSupplier<T> extends SafeReferent, Supplier<T>, Serializable {}

    public interface SafeRunnable extends SafeReferent, Runnable, Serializable {}

    private static final void validateSafeReferent(Supplier<? extends SafeReferent> safeReferentSupplier) {
        if (FMLEnvironment.production) return;
        final SafeReferent setter;
        try {
            setter = safeReferentSupplier.get();
        } catch (Exception e) {
            // Typically a class cast exception, just return out, expected.
            return;
        }
        for (Class<?> cl = setter.getClass(); cl != null; cl = cl.getSuperclass()) {
            try {
                Method m = cl.getDeclaredMethod("writeReplace");
                m.setAccessible(true);
                Object replacement = m.invoke(setter);
                if (!(replacement instanceof SerializedLambda))
                    break;// custom interface implementation
                SerializedLambda l = (SerializedLambda) replacement;
                if (Objects.equals(l.getCapturingClass(), l.getImplClass())) {
                    GunMod.LOGGER.fatal("Detected unsafe referent usage, please view the code at {}",Thread.currentThread().getStackTrace()[3]);
                    throw new RuntimeException("Unsafe Referent usage found in safe referent method");
                }
            } catch (NoSuchMethodException e) {
            } catch (IllegalAccessException | InvocationTargetException e) {
                break;
            }
        }
    }

}
