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

@Mixin(Entity.class)
public class EntityDataMixin implements ICustomDataHolder {

    @Unique
    private CompoundTag mobstacker$customData;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void mobstacker$onConstruct(CallbackInfo ci) {
        this.mobstacker$customData = new CompoundTag();
    }

    @Inject(method = "saveWithoutId", at = @At("RETURN"))
    private void mobstacker$onSaveWithoutId(CompoundTag compound, CallbackInfoReturnable<CompoundTag> cir) {
        compound.put("CustomData", this.mobstacker$customData);
    }

    @Inject(method = "load", at = @At("RETURN"))
    private void mobstacker$onLoad(CompoundTag compound, CallbackInfo ci) {
        if (compound.contains("CustomData", 10)) {
            this.mobstacker$customData = compound.getCompound("CustomData");
        }
    }

    @Override
    public CompoundTag mobstacker$getCustomData() {
        return this.mobstacker$customData;
    }
}