package com.frikinjay.mobstacker.neoforge;

import com.frikinjay.mobstacker.MobStacker;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import static net.minecraft.world.entity.LivingEntity.getSlotForHand;

@EventBusSubscriber(modid = MobStacker.MOD_ID)
public class SheepShearEventHandler {

    @SubscribeEvent
    public static void onMooshroomSheared(PlayerInteractEvent.EntityInteract event) {
        if (event.getTarget() instanceof Sheep sheep) {
            Player player = event.getEntity();
            ItemStack itemStack = event.getItemStack();

            if (itemStack.getItem() instanceof ShearsItem) {
                if (sheep.readyForShearing()) {

                    int stackSize = MobStacker.getStackSize(sheep);
                    for (int i = 0; i < stackSize; i++) {
                        sheep.shear(SoundSource.PLAYERS);
                        if(!sheep.level().isClientSide()) {
                            itemStack.hurtAndBreak(1, player, getSlotForHand(event.getHand()));
                        }
                    }
                }
            }
        }
    }

}
