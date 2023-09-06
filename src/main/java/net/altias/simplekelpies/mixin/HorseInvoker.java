package net.altias.simplekelpies.mixin;

import net.minecraft.entity.passive.HorseColor;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.passive.HorseMarking;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(HorseEntity.class)
public interface HorseInvoker {

    @Invoker("setHorseVariant")
    public void invokeSetHorseVariant(HorseColor color, HorseMarking marking);

}
