package net.altias.simplekelpies.mixin;

import net.altias.simplekelpies.entity.custom.KelpieEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.DonkeyEntity;
import net.minecraft.entity.passive.HorseEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(HorseEntity.class)
public class BreedingMixin {
    @Inject(method = "canBreedWith(Lnet/minecraft/entity/passive/AnimalEntity;)Z", at = @At("HEAD"),locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void injected(AnimalEntity other, CallbackInfoReturnable<Boolean> cir)
    {
        if (other == ((HorseEntity)(Object)this)) {
            cir.setReturnValue(false);
        } else if (!(other instanceof DonkeyEntity) && !(other instanceof HorseEntity) && !(other instanceof KelpieEntity)) {
            cir.setReturnValue(false);
        } else {
            cir.setReturnValue(((AHorseInvoker)(Object)this).invokeCanBreed() && ((AHorseInvoker)other).invokeCanBreed());
        }
    }
}
