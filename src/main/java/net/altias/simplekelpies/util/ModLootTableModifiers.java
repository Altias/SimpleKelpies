package net.altias.simplekelpies.util;

import net.altias.simplekelpies.SimpleKelpies;
import net.altias.simplekelpies.config.SimpleKelpiesConfig;
import net.altias.simplekelpies.item.ModItems;
import net.fabricmc.fabric.api.loot.v2.FabricLootPoolBuilder;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.util.Identifier;

public class ModLootTableModifiers {

    public static final Identifier DUNGEON_CHEST = new Identifier ("minecraft","chests/simple_dungeon");
    public static final Identifier BIG_WATER_CHEST = new Identifier ("minecraft","chests/underwater_ruin_big");
    public static final Identifier SMALL_WATER_CHEST = new Identifier ("minecraft","chests/underwater_ruin_small");
    public static final Identifier SHIPWRECK_CHEST = new Identifier("minecraft", "chests/shipwreck_treasure");

    public static final Identifier FISHING = new Identifier("minecraft","gameplay/fishing/fish");

    public static void modifyLootTables() {
        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) ->{

            if(DUNGEON_CHEST.equals(id)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .conditionally(RandomChanceLootCondition.builder(SimpleKelpies.CONFIG.bridleDungeonDrop())) //1% Drop rate
                        .with(ItemEntry.builder(ModItems.GOLDEN_BRIDLE));

                        tableBuilder.pool(poolBuilder);
            }

            if(BIG_WATER_CHEST.equals(id)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .conditionally(RandomChanceLootCondition.builder(SimpleKelpies.CONFIG.bridleBigWaterRuinDrop())) //90% Drop rate
                        .with(ItemEntry.builder(ModItems.GOLDEN_BRIDLE));

                tableBuilder.pool(poolBuilder);
            }

            if(SMALL_WATER_CHEST.equals(id)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .conditionally(RandomChanceLootCondition.builder(SimpleKelpies.CONFIG.bridleSmallWaterRuinDrop())) //50% Drop rate
                        .with(ItemEntry.builder(ModItems.GOLDEN_BRIDLE));

                tableBuilder.pool(poolBuilder);
            }

            if(SHIPWRECK_CHEST.equals(id)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .conditionally(RandomChanceLootCondition.builder(SimpleKelpies.CONFIG.bridleShipewreckDrop())) //10% Drop rate
                        .with(ItemEntry.builder(ModItems.GOLDEN_BRIDLE));

                tableBuilder.pool(poolBuilder);
            }

            if (FISHING.equals(id)){
                LootPool.Builder poolBuilder = LootPool.builder()
                        .conditionally(RandomChanceLootCondition.builder(SimpleKelpies.CONFIG.goldfinCodFishChance())) //1% Drop rate
                        .with(ItemEntry.builder(ModItems.GOLDFIN_COD));

                tableBuilder.pool(poolBuilder);
            }

        });
    }

}
