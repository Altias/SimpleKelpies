package net.altias.simplekelpies.item;

import net.altias.simplekelpies.SimpleKelpies;
import net.altias.simplekelpies.entity.ModEntities;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final Item KELPIE_SPAWN_EGG = registerItem("kelpie_spawn_egg",
            new SpawnEggItem(ModEntities.KELPIE, 0xC4A17F, 0xFC0000,
                    new FabricItemSettings()));

    public static final Item GOLDEN_BRIDLE = registerItem("golden_bridle", new Item(new FabricItemSettings()));

    public static Item registerItem(String name, Item item)
    {
        return Registry.register(Registries.ITEM, new Identifier(SimpleKelpies.MOD_ID,name), item);
    }

    public static void addItemsToItemGroup()
    {

        addToItemGroup(ItemGroups.SPAWN_EGGS,KELPIE_SPAWN_EGG);
        addToItemGroup(ItemGroups.FUNCTIONAL, GOLDEN_BRIDLE);
    }

    public static void addToItemGroup(RegistryKey<ItemGroup> group, Item item)
    {
        ItemGroupEvents.modifyEntriesEvent(group).register(entries -> entries.add(item));
    }

    public static void registerModItems()
    {
        SimpleKelpies.LOGGER.info("Registering items!");

        addItemsToItemGroup();
    }

}
