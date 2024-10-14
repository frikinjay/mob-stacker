package com.frikinjay.mobstacker.neoforge.mixin.industrialforegoing;

import com.buuz135.industrial.item.MobImprisonmentToolItem;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobImprisonmentToolItem.class)
public class MobImprisonmentToolItemMixin {

    @Inject(method = "capture(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;)Z",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;remove(Lnet/minecraft/world/entity/Entity$RemovalReason;)V"),
            cancellable = true)
    private void mobstacker$onCapture(ItemStack stack, LivingEntity target, CallbackInfoReturnable<Boolean> cir) {
        cir.cancel();
        if (target != null) {
            //System.out.println("MobStacker: Custom pickup logic for: " + target.getName().getString());
            target.remove(Entity.RemovalReason.DISCARDED);
        }
        cir.setReturnValue(true);
    }

}
