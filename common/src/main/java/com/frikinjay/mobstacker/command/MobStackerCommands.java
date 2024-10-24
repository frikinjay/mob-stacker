package com.frikinjay.mobstacker.command;

import com.frikinjay.mobstacker.MobStacker;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static com.frikinjay.mobstacker.MobStacker.MOD_ID;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class MobStackerCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal(MOD_ID)
                .requires(source -> source.hasPermission(2))
                .then(literal("stackerConfig")
                        .then(literal("killWholeStackOnDeath")
                                .then(argument("value", BoolArgumentType.bool())
                                        .executes(MobStackerCommands::setKillWholeStackOnDeath)))
                        .then(literal("stackHealth")
                                .then(argument("value", BoolArgumentType.bool())
                                        .executes(MobStackerCommands::setStackHealth)))
                        .then(literal("maxStackSize")
                                .then(argument("value", IntegerArgumentType.integer(1))
                                        .executes(MobStackerCommands::setMaxStackSize)))
                        .then(literal("stackRadius")
                                .then(argument("value", DoubleArgumentType.doubleArg(0.1, 42000))
                                        .executes(MobStackerCommands::setStackRadius)))
                        .then(literal("separator")
                                .then(literal("enableSeparator")
                                        .then(argument("value", BoolArgumentType.bool())
                                                .executes(MobStackerCommands::setEnableSeparator)))
                                .then(literal("consumeSeparator")
                                        .then(argument("value", BoolArgumentType.bool())
                                                .executes(MobStackerCommands::setConsumeSeparator)))
                                .then(literal("separatorItem")
                                        .then(argument("item", StringArgumentType.greedyString())
                                                .suggests(MobStackerCommands::suggestItems)
                                                .executes(MobStackerCommands::setSeparatorItem)))))
                .then(literal("mobCapConfig")
                        .then(literal("monsterMobCap")
                                .then(argument("value", IntegerArgumentType.integer(0,128))
                                        .executes(MobStackerCommands::setMonsterMobCap)))
                        .then(literal("creatureMobCap")
                                .then(argument("value", IntegerArgumentType.integer(0,128))
                                        .executes(MobStackerCommands::setCreatureMobCap)))
                        .then(literal("ambientMobCap")
                                .then(argument("value", IntegerArgumentType.integer(0,128))
                                        .executes(MobStackerCommands::setAmbientMobCap)))
                        .then(literal("axolotlsMobCap")
                                .then(argument("value", IntegerArgumentType.integer(0,128))
                                        .executes(MobStackerCommands::setAxolotlsMobCap)))
                        .then(literal("undergroundWaterCreatureMobCap")
                                .then(argument("value", IntegerArgumentType.integer(0,128))
                                        .executes(MobStackerCommands::setUndergroundWaterCreatureMobCap)))
                        .then(literal("waterCreatureMobCap")
                                .then(argument("value", IntegerArgumentType.integer(0,128))
                                        .executes(MobStackerCommands::setWaterCreatureMobCap)))
                        .then(literal("waterAmbientMobCap")
                                .then(argument("value", IntegerArgumentType.integer(0,128))
                                        .executes(MobStackerCommands::setWaterAmbientMobCap))))
                .then(literal("ignore")
                        .then(literal("entity")
                                .then(argument("entityId", ResourceLocationArgument.id())
                                        .suggests(MobStackerCommands::suggestEntities)
                                        .executes(MobStackerCommands::ignoreEntity)))
                        .then(literal("mod")
                                .then(argument("modId", StringArgumentType.word())
                                        .suggests(MobStackerCommands::suggestMods)
                                        .executes(MobStackerCommands::ignoreMod))))
                .then(literal("unignore")
                        .then(literal("entity")
                                .then(argument("entityId", ResourceLocationArgument.id())
                                        .suggests(MobStackerCommands::suggestIgnoredEntities)
                                        .executes(MobStackerCommands::unignoreEntity)))
                        .then(literal("mod")
                                .then(argument("modId", StringArgumentType.word())
                                        .suggests(MobStackerCommands::suggestIgnoredMods)
                                        .executes(MobStackerCommands::unignoreMod))))
                .then(literal("setStackSize")
                        .then(argument("entity", EntityArgument.entity())
                                .then(argument("size", IntegerArgumentType.integer(1))
                                        .executes(MobStackerCommands::setStackSize)))));
    }

    private static CompletableFuture<Suggestions> suggestEntities(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        String remaining = builder.getRemaining().toLowerCase();
        BuiltInRegistries.ENTITY_TYPE.forEach(entityType -> {
            if (entityType.create(context.getSource().getLevel()) instanceof Mob) {
                ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
                if (id.toString().toLowerCase().startsWith(remaining)) {
                    builder.suggest(id.toString());
                }
            }
        });
        return builder.buildFuture();
    }

    private static CompletableFuture<Suggestions> suggestMods(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        String remaining = builder.getRemaining().toLowerCase();
        Set<String> modsWithMobs = new HashSet<>();

        BuiltInRegistries.ENTITY_TYPE.forEach(entityType -> {
            if (entityType.create(context.getSource().getLevel()) instanceof Mob) {
                String modId = BuiltInRegistries.ENTITY_TYPE.getKey(entityType).getNamespace();
                modsWithMobs.add(modId);
            }
        });

        modsWithMobs.stream()
                .filter(modId -> modId.toLowerCase().startsWith(remaining))
                .forEach(builder::suggest);

        return builder.buildFuture();
    }

    private static CompletableFuture<Suggestions> suggestIgnoredEntities(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        MobStacker.config.getIgnoredEntities().stream()
                .filter(entity -> entity.startsWith(builder.getRemaining()))
                .forEach(builder::suggest);
        return builder.buildFuture();
    }

    private static CompletableFuture<Suggestions> suggestIgnoredMods(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        MobStacker.config.getIgnoredMods().stream()
                .filter(mod -> mod.startsWith(builder.getRemaining()))
                .forEach(builder::suggest);
        return builder.buildFuture();
    }

    private static int setKillWholeStackOnDeath(CommandContext<CommandSourceStack> context) {
        boolean newValue = BoolArgumentType.getBool(context, "value");
        if (MobStacker.config.getKillWholeStackOnDeath() == newValue) {
            context.getSource().sendSuccess(() -> Component.literal("Kill whole stack on death is already set to " + newValue).withStyle(ChatFormatting.RED), false);
        } else if (!newValue && MobStacker.config.getStackHealth()) {
            context.getSource().sendSuccess(() -> Component.literal("Cannot set killWholeStackOnDeath to false while stackHealth is true").withStyle(ChatFormatting.RED), false);
        } else {
            MobStacker.config.setKillWholeStackOnDeath(newValue);
            context.getSource().sendSuccess(() -> Component.literal("Kill whole stack on death has been set to " + newValue).withStyle(ChatFormatting.AQUA), true);
        }
        return 1;
    }

    private static int setStackHealth(CommandContext<CommandSourceStack> context) {
        boolean newValue = BoolArgumentType.getBool(context, "value");
        if (MobStacker.config.getStackHealth() == newValue) {
            context.getSource().sendSuccess(() -> Component.literal("Stack health is already set to " + newValue).withStyle(ChatFormatting.RED), false);
        } else {
            MobStacker.config.setStackHealth(newValue);
            if (newValue) {
                MobStacker.config.setKillWholeStackOnDeath(true);
            }
            context.getSource().sendSuccess(() -> Component.literal("Stack health has been set to " + newValue + (newValue ? " (Kill whole stack on death set to true)" : "")).withStyle(ChatFormatting.AQUA), true);
        }
        return 1;
    }

    private static int setMaxStackSize(CommandContext<CommandSourceStack> context) {
        int newValue = IntegerArgumentType.getInteger(context, "value");
        if (MobStacker.config.getMaxMobStackSize() == newValue) {
            context.getSource().sendSuccess(() -> Component.literal("Max stack size is already set to " + newValue).withStyle(ChatFormatting.RED), false);
        } else {
            MobStacker.config.setMaxMobStackSize(newValue);
            context.getSource().sendSuccess(() -> Component.literal("Max stack size has been set to " + newValue).withStyle(ChatFormatting.AQUA), true);
        }
        return 1;
    }

    private static int setStackRadius(CommandContext<CommandSourceStack> context) {
        double newValue = DoubleArgumentType.getDouble(context, "value");
        if (MobStacker.config.getStackRadius() == newValue) {
            context.getSource().sendSuccess(() -> Component.literal("Stack radius is already set to " + newValue).withStyle(ChatFormatting.RED), false);
        } else {
            MobStacker.config.setStackRadius(newValue);
            context.getSource().sendSuccess(() -> Component.literal("Stack radius has been set to " + newValue).withStyle(ChatFormatting.AQUA), true);
        }
        return 1;
    }

    private static int ignoreEntity(CommandContext<CommandSourceStack> context) {
        ResourceLocation entityId = ResourceLocationArgument.getId(context, "entityId");
        String entityIdString = entityId.toString();
        if (MobStacker.config.getIgnoredEntities().contains(entityIdString)) {
            context.getSource().sendSuccess(() -> Component.literal("Entity '" + entityIdString + "' is already ignored").withStyle(ChatFormatting.RED), false);
        } else {
            MobStacker.config.addIgnoredEntity(entityIdString);
            context.getSource().sendSuccess(() -> Component.literal("Added '" + entityIdString + "' to ignored entities").withStyle(ChatFormatting.AQUA), true);
        }
        return 1;
    }

    private static int ignoreMod(CommandContext<CommandSourceStack> context) {
        String modId = StringArgumentType.getString(context, "modId");
        if (MobStacker.config.getIgnoredMods().contains(modId)) {
            context.getSource().sendSuccess(() -> Component.literal("Mod '" + modId + "' is already ignored").withStyle(ChatFormatting.RED), false);
        } else {
            MobStacker.config.addIgnoredMod(modId);
            context.getSource().sendSuccess(() -> Component.literal("Added '" + modId + "' to ignored mods").withStyle(ChatFormatting.AQUA), true);
        }
        return 1;
    }

    private static int unignoreEntity(CommandContext<CommandSourceStack> context) {
        ResourceLocation entityId = ResourceLocationArgument.getId(context, "entityId");
        String entityIdString = entityId.toString();
        if (!MobStacker.config.getIgnoredEntities().contains(entityIdString)) {
            context.getSource().sendSuccess(() -> Component.literal("Entity '" + entityIdString + "' is not in the ignored list").withStyle(ChatFormatting.RED), false);
        } else {
            MobStacker.config.removeIgnoredEntity(entityIdString);
            context.getSource().sendSuccess(() -> Component.literal("Removed '" + entityIdString + "' from ignored entities").withStyle(ChatFormatting.GOLD), true);
        }
        return 1;
    }

    private static int unignoreMod(CommandContext<CommandSourceStack> context) {
        String modId = StringArgumentType.getString(context, "modId");
        if (!MobStacker.config.getIgnoredMods().contains(modId)) {
            context.getSource().sendSuccess(() -> Component.literal("Mod '" + modId + "' is not in the ignored list").withStyle(ChatFormatting.RED), false);
        } else {
            MobStacker.config.removeIgnoredMod(modId);
            context.getSource().sendSuccess(() -> Component.literal("Removed '" + modId + "' from ignored mods").withStyle(ChatFormatting.GOLD), true);
        }
        return 1;
    }

    private static int setStackSize(CommandContext<CommandSourceStack> context) {
        try {
            Entity targetEntity = EntityArgument.getEntity(context, "entity");
            int newSize = IntegerArgumentType.getInteger(context, "size");

            if (!(targetEntity instanceof LivingEntity)) {
                context.getSource().sendFailure(Component.literal("Target is not a living entity").withStyle(ChatFormatting.RED));
                return 1;
            }

            Mob livingEntity = (Mob) targetEntity;
            int currentSize = MobStacker.getStackSize(livingEntity);

            if (currentSize == newSize) {
                context.getSource().sendSuccess(() -> Component.literal("Stack size is already set to " + newSize).withStyle(ChatFormatting.RED), false);
            } else {
                MobStacker.setStackSize(livingEntity, newSize);
                context.getSource().sendSuccess(() -> Component.literal("Set stack size of entity to " + newSize).withStyle(ChatFormatting.AQUA), true);
            }
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("Error setting stack size: " + e.getMessage()).withStyle(ChatFormatting.RED));
        }
        return 1;
    }

    private static int setEnableSeparator(CommandContext<CommandSourceStack> context) {
        boolean newValue = BoolArgumentType.getBool(context, "value");
        if (MobStacker.config.getEnableSeparator() == newValue) {
            context.getSource().sendSuccess(() -> Component.literal("Separator is already " + (newValue ? "enabled" : "disabled")).withStyle(ChatFormatting.RED), false);
        } else {
            MobStacker.config.setEnableSeparator(newValue);
            context.getSource().sendSuccess(() -> Component.literal("Separator has been " + (newValue ? "enabled" : "disabled")).withStyle(ChatFormatting.AQUA), true);
        }
        return 1;
    }

    private static int setConsumeSeparator(CommandContext<CommandSourceStack> context) {
        boolean newValue = BoolArgumentType.getBool(context, "value");
        if (MobStacker.config.getConsumeSeparator() == newValue) {
            context.getSource().sendSuccess(() -> Component.literal("Consume separator is already set to " + newValue).withStyle(ChatFormatting.RED), false);
        } else {
            MobStacker.config.setConsumeSeparator(newValue);
            context.getSource().sendSuccess(() -> Component.literal("Consume separator has been set to " + newValue).withStyle(ChatFormatting.AQUA), true);
        }
        return 1;
    }

    private static CompletableFuture<Suggestions> suggestItems(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        String remaining = builder.getRemaining().toLowerCase();
        for (ResourceLocation itemId : BuiltInRegistries.ITEM.keySet()) {
            String itemString = itemId.toString();
            if (itemString.toLowerCase().startsWith(remaining)) {
                builder.suggest(itemString);
            }
        }
        return builder.buildFuture();
    }

    private static int setSeparatorItem(CommandContext<CommandSourceStack> context) {
        String itemString = StringArgumentType.getString(context, "item");
        ResourceLocation itemId = ResourceLocation.tryParse(itemString);
        if (itemId != null && BuiltInRegistries.ITEM.containsKey(itemId)) {
            if (MobStacker.config.getSeparatorItem().equals(itemString)) {
                context.getSource().sendSuccess(() -> Component.literal("Separator item is already set to " + itemString).withStyle(ChatFormatting.RED), false);
            } else {
                MobStacker.config.setSeparatorItem(itemString);
                context.getSource().sendSuccess(() -> Component.literal("Separator item has been set to " + itemString).withStyle(ChatFormatting.AQUA), true);
            }
        } else {
            context.getSource().sendFailure(Component.literal("Invalid item: " + itemString).withStyle(ChatFormatting.RED));
        }
        return 1;
    }

    // Mob Cap

    private static int setMonsterMobCap(CommandContext<CommandSourceStack> context) {
        int newValue = IntegerArgumentType.getInteger(context, "value");
        if (MobStacker.config.getMonsterMobCap() == newValue) {
            context.getSource().sendSuccess(() -> Component.literal("Monster mob cap is already set to " + newValue).withStyle(ChatFormatting.RED), false);
        } else {
            MobStacker.config.setMonsterMobCap(newValue);
            context.getSource().sendSuccess(() -> Component.literal("Monster mob cap has been set to " + newValue).withStyle(ChatFormatting.AQUA), true);
        }
        return 1;
    }

    private static int setCreatureMobCap(CommandContext<CommandSourceStack> context) {
        int newValue = IntegerArgumentType.getInteger(context, "value");
        if (MobStacker.config.getCreatureMobCap() == newValue) {
            context.getSource().sendSuccess(() -> Component.literal("Creature mob cap is already set to " + newValue).withStyle(ChatFormatting.RED), false);
        } else {
            MobStacker.config.setCreatureMobCap(newValue);
            context.getSource().sendSuccess(() -> Component.literal("Creature mob cap has been set to " + newValue).withStyle(ChatFormatting.AQUA), true);
        }
        return 1;
    }

    private static int setAmbientMobCap(CommandContext<CommandSourceStack> context) {
        int newValue = IntegerArgumentType.getInteger(context, "value");
        if (MobStacker.config.getAmbientMobCap() == newValue) {
            context.getSource().sendSuccess(() -> Component.literal("Ambient mob cap is already set to " + newValue).withStyle(ChatFormatting.RED), false);
        } else {
            MobStacker.config.setAmbientMobCap(newValue);
            context.getSource().sendSuccess(() -> Component.literal("Ambient mob cap has been set to " + newValue).withStyle(ChatFormatting.AQUA), true);
        }
        return 1;
    }

    private static int setAxolotlsMobCap(CommandContext<CommandSourceStack> context) {
        int newValue = IntegerArgumentType.getInteger(context, "value");
        if (MobStacker.config.getAxolotlsMobCap() == newValue) {
            context.getSource().sendSuccess(() -> Component.literal("Axolotls mob cap is already set to " + newValue).withStyle(ChatFormatting.RED), false);
        } else {
            MobStacker.config.setAxolotlsMobCap(newValue);
            context.getSource().sendSuccess(() -> Component.literal("Axolotls mob cap has been set to " + newValue).withStyle(ChatFormatting.AQUA), true);
        }
        return 1;
    }

    private static int setUndergroundWaterCreatureMobCap(CommandContext<CommandSourceStack> context) {
        int newValue = IntegerArgumentType.getInteger(context, "value");
        if (MobStacker.config.getUndergroundWaterCreatureMobCap() == newValue) {
            context.getSource().sendSuccess(() -> Component.literal("Underground water creature mob cap is already set to " + newValue).withStyle(ChatFormatting.RED), false);
        } else {
            MobStacker.config.setUndergroundWaterCreatureMobCap(newValue);
            context.getSource().sendSuccess(() -> Component.literal("Underground water creature mob cap has been set to " + newValue).withStyle(ChatFormatting.AQUA), true);
        }
        return 1;
    }

    private static int setWaterCreatureMobCap(CommandContext<CommandSourceStack> context) {
        int newValue = IntegerArgumentType.getInteger(context, "value");
        if (MobStacker.config.getWaterCreatureMobCap() == newValue) {
            context.getSource().sendSuccess(() -> Component.literal("Water creature mob cap is already set to " + newValue).withStyle(ChatFormatting.RED), false);
        } else {
            MobStacker.config.setWaterCreatureMobCap(newValue);
            context.getSource().sendSuccess(() -> Component.literal("Water creature mob cap has been set to " + newValue).withStyle(ChatFormatting.AQUA), true);
        }
        return 1;
    }

    private static int setWaterAmbientMobCap(CommandContext<CommandSourceStack> context) {
        int newValue = IntegerArgumentType.getInteger(context, "value");
        if (MobStacker.config.getWaterAmbientMobCap() == newValue) {
            context.getSource().sendSuccess(() -> Component.literal("Water ambient mob cap is already set to " + newValue).withStyle(ChatFormatting.RED), false);
        } else {
            MobStacker.config.setWaterAmbientMobCap(newValue);
            context.getSource().sendSuccess(() -> Component.literal("Water ambient mob cap has been set to " + newValue).withStyle(ChatFormatting.AQUA), true);
        }
        return 1;
    }
}