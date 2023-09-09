package net.altias.simplekelpies.mixin;

import net.minecraft.entity.passive.AbstractHorseEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractHorseEntity.class)
public interface AHorseInvoker {
    @Invoker("canBreed")
    public boolean invokeCanBreed();
}
