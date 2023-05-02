package net.altias.simplekelpies.entity;

import net.altias.simplekelpies.SimpleKelpies;
import net.altias.simplekelpies.entity.custom.KelpieEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {
    public static final EntityType<KelpieEntity> KELPIE = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(SimpleKelpies.MOD_ID,"kelpie"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE,KelpieEntity::new)
                    .dimensions(EntityDimensions.fixed(1.4f,1.6f)).build());
}
