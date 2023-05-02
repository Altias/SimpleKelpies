package net.altias.simplekelpies.entity.client;

import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.HorseEntityModel;

public class KelpieModelData {

        public static TexturedModelData createModelData()
        {
            return (TexturedModelData.of(HorseEntityModel.getModelData(Dilation.NONE), 64, 64));
        }

}
