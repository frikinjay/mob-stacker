package com.frikinjay.mobstacker.mixin.mobs;

import com.frikinjay.mobstacker.MobStacker;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.npc.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Zombie.class)
public class ZombieMixin {

    @Inject(
            method = "killedEntity(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/monster/ZombieVillager;setVillagerXp(I)V",
                    shift = At.Shift.AFTER),
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private void mobstacker$killedEntity(ServerLevel serverLevel, LivingEntity livingEntity, CallbackInfoReturnable<Boolean> cir, boolean bl,Villager villager, ZombieVillager zombieVillager) {
        if (zombieVillager != null) {
            MobStacker.setStackSize(zombieVillager, MobStacker.getStackSize(villager));
            zombieVillager.setCustomName(null);
            MobStacker.updateStackDisplay(zombieVillager);
        }
    }

}
