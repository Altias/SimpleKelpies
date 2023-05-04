package net.altias.simplekelpies;

import net.altias.simplekelpies.entity.ModEntities;
import net.altias.simplekelpies.entity.custom.KelpieEntity;
import net.altias.simplekelpies.item.ModItems;
import net.altias.simplekelpies.util.ModLootTableModifiers;
import net.altias.simplekelpies.world.gen.ModEntityGeneration;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleKelpies implements ModInitializer {
    public static final String MOD_ID = "simplekelpies";
    public static final Logger LOGGER = LoggerFactory.getLogger("simplekelpies");

    @Override
    public void onInitialize() {
        ModItems.registerModItems();
        FabricDefaultAttributeRegistry.register(ModEntities.KELPIE, KelpieEntity.setAttributes());

        ModEntityGeneration.addSpawns();
        ModLootTableModifiers.modifyLootTables();

    }
}