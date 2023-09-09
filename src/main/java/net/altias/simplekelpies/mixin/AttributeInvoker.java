package net.altias.simplekelpies.mixin;

import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.HorseColor;
import net.minecraft.entity.passive.HorseMarking;
import net.minecraft.entity.passive.PassiveEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractHorseEntity.class)
public interface AttributeInvoker {

    @Invoker("setChildAttributes")
    public void invokeSetChildAttributes(PassiveEntity other, AbstractHorseEntity child);

}

