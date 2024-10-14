package com.frikinjay.mobstacker.mixin;

import com.frikinjay.mobstacker.ICustomDataHolder;
import com.frikinjay.mobstacker.MobStacker;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerEntity.class)
public class ServerEntityMixin {

    @Shadow @Final private Entity entity;

    @Inject(
            method = "sendChanges",
            at = @At("HEAD")
    )
    private void mobstacker$updateStackDisplayBeforeSendingChanges(CallbackInfo ci) {
        if (entity instanceof Mob livingEntity && livingEntity instanceof ICustomDataHolder) {
            CompoundTag customData = ((ICustomDataHolder) livingEntity).mobstacker$getCustomData();
            int stackSize = customData.getInt(MobStacker.STACK_SIZE_KEY);
            if (stackSize > 1) {
                MobStacker.updateStackDisplay(livingEntity);
            }
        }
    }
}