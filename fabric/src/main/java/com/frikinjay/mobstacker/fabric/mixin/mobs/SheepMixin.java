package com.frikinjay.mobstacker.fabric.mixin.mobs;

import com.frikinjay.mobstacker.MobStacker;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.world.entity.LivingEntity.getSlotForHand;

@Mixin(Sheep.class)
public class SheepMixin {
    @Inject(method = "mobInteract", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;hurtAndBreak(ILnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/EquipmentSlot;)V", shift = At.Shift.AFTER))
    private void mobstacker$onShearAllSheep(Player player, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResult> cir) {
        Sheep self = (Sheep) (Object) this;
        ItemStack itemStack = player.getItemInHand(interactionHand);
        int stackSize = MobStacker.getStackSize(self);
        for (int i = 0; i < stackSize; i++) {
            self.shear(SoundSource.PLAYERS);
            if(!self.level().isClientSide) {
                itemStack.hurtAndBreak(1, player, getSlotForHand(interactionHand));
            }
        }
    }
}