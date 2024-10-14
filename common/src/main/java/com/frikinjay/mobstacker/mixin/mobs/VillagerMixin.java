package com.frikinjay.mobstacker.mixin.mobs;

import com.frikinjay.mobstacker.MobStacker;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.npc.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Villager.class)
public class VillagerMixin {

    @Inject(method = "thunderHit",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/Witch;setPersistenceRequired()V"),
            locals = LocalCapture.CAPTURE_FAILSOFT)
    private void mobstacker$thunderHit(ServerLevel serverLevel, LightningBolt lightningBolt, CallbackInfo ci, Witch witch) {
        if (witch != null) {
            Villager instance = (Villager) (Object) this;
            MobStacker.setStackSize(witch, MobStacker.getStackSize(instance));
            witch.setCustomName(null);
            MobStacker.updateStackDisplay(witch);
        }
    }

}
