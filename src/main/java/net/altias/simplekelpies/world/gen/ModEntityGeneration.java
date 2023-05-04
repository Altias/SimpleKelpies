package net.altias.simplekelpies.world.gen;

import net.altias.simplekelpies.entity.ModEntities;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.BiomeKeys;

public class ModEntityGeneration {
    public static void addSpawns()
    {
        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(BiomeKeys.BEACH,BiomeKeys.RIVER, BiomeKeys.OCEAN), SpawnGroup.CREATURE, ModEntities.KELPIE,15, 1,1);

        SpawnRestriction.register(ModEntities.KELPIE,SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::isValidNaturalSpawn);
    }
}
