package com.tacz.guns.util.helper;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;

import java.util.Optional;
import java.util.stream.Stream;

public class ParticleOptionsHelper {

    private static final HolderLookup.Provider provider = new HolderLookup.Provider() {
        @Override
        public Stream<ResourceKey<? extends Registry<?>>> listRegistries() {
            return Stream.of(Registries.PARTICLE_TYPE);
        }
        @Override
        public <T> Optional<HolderLookup.RegistryLookup<T>> lookup(ResourceKey<? extends Registry<? extends T>> pRegistryKey) {
            return Optional.of((HolderLookup.RegistryLookup<T>) BuiltInRegistries.PARTICLE_TYPE.asLookup());
        }
    };

    public static HolderLookup.Provider getParticleTypesProvider() {
        return provider;
    }

}
