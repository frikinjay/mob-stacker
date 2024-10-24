package com.frikinjay.mobstacker.mixin;

import com.frikinjay.mobstacker.ICustomDataHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.frikinjay.mobstacker.MobStacker.STACK_DATA_KEY;

@Mixin(Entity.class)
public class EntityDataMixin implements ICustomDataHolder {

    @Unique
    private CompoundTag mobstacker$stackData;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void mobstacker$onConstruct(CallbackInfo ci) {
        this.mobstacker$stackData = new CompoundTag();
    }

    @Inject(method = "saveWithoutId", at = @At("RETURN"))
    private void mobstacker$onSaveWithoutId(CompoundTag compound, CallbackInfoReturnable<CompoundTag> cir) {
        compound.put(STACK_DATA_KEY, this.mobstacker$stackData);
    }

    @Inject(method = "load", at = @At("RETURN"))
    private void mobstacker$onLoad(CompoundTag compound, CallbackInfo ci) {
        if (compound.contains(STACK_DATA_KEY, 10)) {
            this.mobstacker$stackData = compound.getCompound(STACK_DATA_KEY);
        }
    }

    @Override
    public CompoundTag mobstacker$getCustomData() {
        return this.mobstacker$stackData;
    }
}