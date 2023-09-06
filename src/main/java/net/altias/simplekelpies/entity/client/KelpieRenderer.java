package net.altias.simplekelpies.entity.client;

import net.altias.simplekelpies.SimpleKelpies;
import net.altias.simplekelpies.SimpleKelpiesClient;
import net.altias.simplekelpies.entity.custom.KelpieEntity;
import net.minecraft.client.render.entity.AbstractHorseEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.HorseEntityRenderer;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.HorseEntityModel;
import net.minecraft.util.Identifier;



public class KelpieRenderer extends MobEntityRenderer<KelpieEntity, HorseEntityModel<KelpieEntity>> {
    private static final Identifier NORMAL = new Identifier(SimpleKelpies.MOD_ID, "textures/entity/kelpie.png");

    private static final Identifier RARE = new Identifier(SimpleKelpies.MOD_ID,"textures/entity/kelpie_rare.png");
    private static final Identifier ANGER = new Identifier(SimpleKelpies.MOD_ID,"textures/entity/kelpie_anger.png");

    public KelpieRenderer(EntityRendererFactory.Context context) {
        super(context, new HorseEntityModel(context.getPart(SimpleKelpiesClient.MODEL_KELPIE_LAYER)), 0.75f);
    }

    @Override
    public Identifier getTexture(KelpieEntity entity) {

        if (entity.hasAngerTime())
        {
            return ANGER;
        }

        if (entity.isTame() && entity.isWet())
        {
            return ANGER;
        }

        if (entity.rareColor)
        {
            return(RARE);
        }
        return NORMAL;
    }
}
