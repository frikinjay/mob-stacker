package com.frikinjay.mobstacker.mixin;

import com.frikinjay.mobstacker.MobStacker;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

import static net.minecraft.world.entity.Entity.DATA_CUSTOM_NAME;

@Mixin(Entity.class)
public class EntityMixin {

    @Inject(method = "setCustomName", at = @At("TAIL"))
    private void mobstacker$onSetCustomName(@Nullable Component component, CallbackInfo ci) {
        Entity self = (Entity) (Object) this;
        if (!self.level().isClientSide && self instanceof Mob) {
            Component customName = self.getCustomName();

            if (customName != null) {
                int stackSize = MobStacker.getStackSize((Mob) self);
                String nameString = customName.getString();
                Style nameStyle = component != null ? component.getStyle() : Style.EMPTY;
                String updatedNameString = mobstacker$getString(stackSize, nameString);
                self.getEntityData().set(DATA_CUSTOM_NAME, Optional.of(
                        Component.literal(updatedNameString).withStyle(nameStyle)
                ));
            }
        }
    }

    @Unique
    private static @NotNull String mobstacker$getString(int stackSize, String nameString) {
        String stackSizeString = "x" + stackSize;

        if (nameString.endsWith(stackSizeString)) {
            return nameString.substring(0, nameString.length() - String.valueOf(stackSize).length() - 1) + stackSizeString;
        }

        return nameString;
    }
}