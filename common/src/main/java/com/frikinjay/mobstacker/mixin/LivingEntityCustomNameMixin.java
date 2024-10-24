package com.frikinjay.mobstacker.mixin;

import com.frikinjay.almanac.Almanac;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = LivingEntity.class, priority = 999)
public class LivingEntityCustomNameMixin {

    @Redirect(
            method = "die(Lnet/minecraft/world/damagesource/DamageSource;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;hasCustomName()Z"
            ),
            require = 0
    )
    private boolean mobstacker$replaceHasCustomName(LivingEntity instance) {
        return !Almanac.hasNonCustomName(instance);
    }

}
