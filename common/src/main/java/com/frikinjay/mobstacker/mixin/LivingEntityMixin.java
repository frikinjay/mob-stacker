package com.frikinjay.mobstacker.mixin;

import com.frikinjay.mobstacker.MobStacker;
import com.frikinjay.mobstacker.api.MobStackerAPI;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    @Unique
    private LivingEntity mobstacker$thisEntity;
    @Unique
    private Mob mobstacker$self;

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "onChangedBlock", at = @At("HEAD"))
    private void mobstacker$onChangedBlock(CallbackInfo ci) {
        mobstacker$thisEntity = (LivingEntity) (Object) this;
        if (!mobstacker$thisEntity.level().isClientSide && mobstacker$thisEntity instanceof Mob) {
            mobstacker$self = (Mob) mobstacker$thisEntity;
            if (MobStacker.getCanStack(mobstacker$self) && MobStacker.canStack(mobstacker$self)) {
                mobstacker$self.level().getEntities(mobstacker$self, mobstacker$self.getBoundingBox().inflate(MobStacker.getStackRadius()),
                                e -> e instanceof Mob && MobStacker.canStack((Mob) e))
                        .stream()
                        .filter(nearby -> MobStacker.canMerge(mobstacker$self, (Mob) nearby))
                        .findFirst()
                        .ifPresent(nearby -> MobStacker.mergeEntities((Mob) nearby, mobstacker$self));
            }
        }
    }

    @Inject(method = "die", at = @At("HEAD"))
    private void mobstacker$onDie(DamageSource damageSource, CallbackInfo ci) {
        mobstacker$thisEntity = (LivingEntity) (Object) this;
        if (mobstacker$thisEntity instanceof Mob && damageSource.is(DamageTypes.GENERIC_KILL)) {
            mobstacker$self = (Mob) mobstacker$thisEntity;
            MobStacker.setStackSize(mobstacker$self, 1);
        }
    }

    @Inject(method = "remove", at = @At("HEAD"))
    private void mobstacker$onRemoveHead(RemovalReason reason, CallbackInfo ci) {
        mobstacker$thisEntity = (LivingEntity) (Object) this;
        if (!MobStacker.getKillWholeStackOnDeath() && mobstacker$thisEntity instanceof Mob) {
            mobstacker$self = (Mob) mobstacker$thisEntity;
            int stackSize = MobStacker.getStackSize(mobstacker$self);

            if (MobStacker.shouldSpawnNewEntity(mobstacker$self, reason) && stackSize > 1 && mobstacker$self.level() instanceof ServerLevel serverLevel) {
                MobStackerAPI.executeCustomDeathHandlers(mobstacker$self, mobstacker$self.getLastDamageSource());
                MobStacker.spawnNewEntity(serverLevel, mobstacker$self, stackSize);
            }
        }
    }

    @Inject(method = "die", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;awardKillScore(Lnet/minecraft/world/entity/Entity;ILnet/minecraft/world/damagesource/DamageSource;)V", shift = At.Shift.AFTER))
    private void mobstacker$onDieAllScore(DamageSource damageSource, CallbackInfo ci) {
        mobstacker$thisEntity = (LivingEntity) (Object) this;
        if(mobstacker$thisEntity instanceof Mob && MobStacker.getKillWholeStackOnDeath()) {
            mobstacker$self = (Mob) mobstacker$thisEntity;
            int stackSize = MobStacker.getStackSize(mobstacker$self);
            LivingEntity livingEntity = mobstacker$self.getKillCredit();
            if (mobstacker$self.deathScore >= 0 && livingEntity != null) {
                for (int i = 1; i < stackSize; i++) {
                    livingEntity.awardKillScore(mobstacker$self, mobstacker$self.deathScore, damageSource);
                }
            }
        }
    }

    @Inject(method = "die", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;dropAllDeathLoot(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;)V", shift = At.Shift.AFTER))
    private void mobstacker$onDieAllDropLoot(DamageSource damageSource, CallbackInfo ci) {
        mobstacker$thisEntity = (LivingEntity) (Object) this;
        if(mobstacker$thisEntity instanceof Mob && MobStacker.getKillWholeStackOnDeath()) {
            mobstacker$self = (Mob) mobstacker$thisEntity;
            int stackSize = MobStacker.getStackSize(mobstacker$self);
            for (int i = 1; i < stackSize; i++) {
                if(!mobstacker$self.level().isClientSide()) {
                    mobstacker$self.dropAllDeathLoot((ServerLevel) mobstacker$self.level(), damageSource);
                }
            }
        }
    }

    @Inject(method = "die", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;dropAllDeathLoot(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;)V", shift = At.Shift.AFTER))
    private void mobstacker$onDieAllCreateWRose(DamageSource damageSource, CallbackInfo ci) {
        mobstacker$thisEntity = (LivingEntity) (Object) this;
        if(mobstacker$thisEntity instanceof Mob && MobStacker.getKillWholeStackOnDeath()) {
            mobstacker$self = (Mob) mobstacker$thisEntity;
            int stackSize = MobStacker.getStackSize(mobstacker$self);
            LivingEntity livingEntity = mobstacker$self.getKillCredit();
            for (int i = 1; i < stackSize; i++) {
                mobstacker$self.createWitherRose(livingEntity);
            }
        }
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void mobstacker$onConstructed(EntityType<?> entityType, Level level, CallbackInfo ci) {
        mobstacker$thisEntity = (LivingEntity) (Object) this;
        if (!level.isClientSide && mobstacker$thisEntity instanceof Mob) {
            mobstacker$self = (Mob) mobstacker$thisEntity;
            if (MobStacker.getStackSize(mobstacker$self) == 1) {
                MobStacker.setStackSize(mobstacker$self, 1);
            }
            if (MobStacker.getCanStack(mobstacker$self)) {
                MobStacker.setCanStack(mobstacker$self, true);
            }
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
    private void mobstacker$onReadAdditionalSaveData(CompoundTag compound, CallbackInfo ci) {
        mobstacker$thisEntity = (LivingEntity) (Object) this;
        if (!mobstacker$thisEntity.level().isClientSide && mobstacker$thisEntity instanceof Mob) {
            mobstacker$self = (Mob) mobstacker$thisEntity;
            MobStacker.updateStackDisplay(mobstacker$self);
        }
    }
}