package com.frikinjay.mobstacker.neoforge.mixin.mobs;

import com.frikinjay.mobstacker.MobStacker;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import static net.minecraft.world.entity.LivingEntity.getSlotForHand;

@Mixin(value = MushroomCow.class)
public class MushroomCowMixin {

    @Inject(method = "mobInteract", at = @At(value = "TAIL", target = "Lnet/minecraft/world/entity/animal/MushroomCow;mobInteract(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;"))
    private void mobstacker$onShearAllMoo(Player player, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResult> cir) {
        MushroomCow self = (MushroomCow) (Object) this;
        ItemStack itemStack = player.getItemInHand(interactionHand);
        int stackSize = MobStacker.getStackSize(self);
        if(itemStack.is(Items.SHEARS) && self.readyForShearing() && !self.level().isClientSide) {
            for (int i = 0; i < stackSize; i++) {
                for (int j = 0; j < 5; ++j) {
                    self.level().addFreshEntity(new ItemEntity(self.level(), self.getX(), self.getY(1.0), self.getZ(), new ItemStack(self.getVariant().getBlockState().getBlock())));
                }
                itemStack.hurtAndBreak(1, player, getSlotForHand(interactionHand));
            }
        }
    }

    @Inject(method = "shear", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/Cow;moveTo(DDDFF)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void mobstacker$onInternalShearAllMoo(SoundSource soundSource, CallbackInfo ci, Cow cow) {
        MushroomCow self = (MushroomCow) (Object) this;
        if (cow != null) {
            MobStacker.setStackSize(cow, MobStacker.getStackSize(self));
        }
    }
}