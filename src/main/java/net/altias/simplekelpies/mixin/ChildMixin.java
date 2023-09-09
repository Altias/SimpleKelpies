package net.altias.simplekelpies.mixin;

import net.altias.simplekelpies.entity.custom.KelpieEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Util;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(HorseEntity.class)
public class ChildMixin {
 Random random = Random.create();
    @Inject(method = "createChild(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/passive/PassiveEntity;)Lnet/minecraft/entity/passive/PassiveEntity;", at = @At("HEAD"),locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void injected(ServerWorld world, PassiveEntity entity, CallbackInfoReturnable<PassiveEntity> cir)
    {
        if (entity instanceof KelpieEntity)
        {
            KelpieEntity horseEntity = (KelpieEntity)entity;
            HorseEntity horseEntity2 = (HorseEntity) EntityType.HORSE.create(world);
            if (horseEntity2 != null) {
                int i = random.nextInt(9);
                HorseColor horseColor;
                if (i < 4) {
                    horseColor = ((HorseEntity)(Object)this).getVariant();
                } else if (i < 8) {
                    horseColor = HorseColor.WHITE;
                } else {
                    horseColor = (HorseColor) Util.getRandom(HorseColor.values(), random);
                }

                int j = this.random.nextInt(5);
                HorseMarking horseMarking;
                if (j < 2) {
                    horseMarking = ((HorseEntity)(Object)this).getMarking();
                } else if (j < 4) {
                    horseMarking = HorseMarking.NONE;
                } else {
                    horseMarking = (HorseMarking)Util.getRandom(HorseMarking.values(), this.random);
                }

                ((HorseInvoker)horseEntity2).invokeSetHorseVariant(horseColor, horseMarking);
                ((AttributeInvoker)(Object)this).invokeSetChildAttributes(entity, horseEntity2);
            }

            cir.setReturnValue(horseEntity2);
        }
    }
}
