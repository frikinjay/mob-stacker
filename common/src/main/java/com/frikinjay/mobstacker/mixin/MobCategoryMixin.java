package com.frikinjay.mobstacker.mixin;

import com.frikinjay.mobstacker.MobStacker;
import net.minecraft.world.entity.MobCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobCategory.class)
public class MobCategoryMixin {
    @Inject(method = "getMaxInstancesPerChunk", at = @At("HEAD"), cancellable = true)
    private void modifyMobCap(CallbackInfoReturnable<Integer> cir) {
        MobCategory category = (MobCategory)(Object)this;

        switch(category) {
            case MONSTER -> cir.setReturnValue(MobStacker.getMonsterMobCap());  // Default is 70
            case CREATURE -> cir.setReturnValue(MobStacker.getCreatureMobCap()); // Default is 10
            case AMBIENT -> cir.setReturnValue(MobStacker.getAmbientMobCap());  // Default is 15
            case AXOLOTLS -> cir.setReturnValue(MobStacker.getAxolotlsMobCap()); // Default is 5
            case UNDERGROUND_WATER_CREATURE -> cir.setReturnValue(MobStacker.getUndergroundWaterCreatureMobCap()); // Default is 5
            case WATER_CREATURE -> cir.setReturnValue(MobStacker.getWaterCreatureMobCap()); // Default is 5
            case WATER_AMBIENT -> cir.setReturnValue(MobStacker.getWaterAmbientMobCap()); // Default is 20
        }
    }
}
