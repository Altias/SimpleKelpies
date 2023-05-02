package net.altias.simplekelpies;

import net.altias.simplekelpies.entity.ModEntities;
import net.altias.simplekelpies.entity.client.KelpieRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.HorseEntityModel;
import net.minecraft.util.Identifier;

public class SimpleKelpiesClient implements ClientModInitializer {

    public static final EntityModelLayer MODEL_KELPIE_LAYER = new EntityModelLayer(new Identifier(SimpleKelpies.MOD_ID,"kelpie"),"main");

    @Override
    public void onInitializeClient(){
        EntityRendererRegistry.register(ModEntities.KELPIE, (context) -> {
            return new KelpieRenderer(context);
        });

        EntityModelLayerRegistry.registerModelLayer(MODEL_KELPIE_LAYER, TexturedModelData.of(HorseEntityModel.getModelData(Dilation.NONE), 64, 64));
    }
}
