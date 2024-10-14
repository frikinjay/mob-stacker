package com.frikinjay.mobstacker;

import com.frikinjay.almanac.Almanac;
import com.frikinjay.mobstacker.api.MobStackerAPI;
import com.frikinjay.mobstacker.command.MobStackerCommands;
import com.frikinjay.mobstacker.config.MobStackerConfig;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import org.slf4j.Logger;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;

public final class MobStacker {
    public static final String MOD_ID = "mobstacker";

    public static final Logger logger = LogUtils.getLogger();
    public static final String STACK_SIZE_KEY = "StackSize";
    public static MobStackerConfig config;
    public static final File CONFIG_FILE = new File("config/mobstacker.json");

    public static void init() {
        config = MobStackerConfig.load();
        Almanac.addConfigChangeListener(CONFIG_FILE, newConfig -> {
            config = (MobStackerConfig) newConfig;
            logger.info("MobStacker config reloaded");
        });
        config.save();
        Almanac.addCommandRegistration(MobStackerCommands::register);
    }

    public static boolean canStack(Mob entity) {
        if (!(entity instanceof Mob) || entity.isBaby()) {
            return false;
        }

        ResourceLocation entityId = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
        String modId = entityId.getNamespace();

        if (config.getIgnoredEntities().contains(entityId.toString()) || config.getIgnoredMods().contains(modId)) {
            return false;
        }

        return hasValidCustomNameForStacking(entity) && getStackSize(entity) < config.getMaxMobStackSize();
    }

    public static boolean canMerge(Mob self, Mob nearby) {
        if (self.getClass() != nearby.getClass()) return false;
        if ((getStackSize(self) + getStackSize(nearby)) > getMaxMobStackSize()) return false;

        if (self instanceof Sheep) {
            return (((Sheep) self).getColor() == ((Sheep) nearby).getColor()) && (((Sheep) self).isSheared() == ((Sheep) nearby).isSheared());
        } else if (self instanceof Villager || self instanceof ZombieVillager) {
            boolean isVillager = self instanceof Villager;
            Object selfCast = isVillager ? (Villager) self : (ZombieVillager) self;
            Object nearbyCast = isVillager ? (Villager) nearby : (ZombieVillager) nearby;

            if (isVillager ? ((Villager) selfCast).getVariant() != ((Villager) nearbyCast).getVariant()
                    : ((ZombieVillager) selfCast).getVariant() != ((ZombieVillager) nearbyCast).getVariant()) {
                return false;
            }

            VillagerData selfData = isVillager ? ((Villager) selfCast).getVillagerData()
                    : ((ZombieVillager) selfCast).getVillagerData();
            VillagerData nearbyData = isVillager ? ((Villager) nearbyCast).getVillagerData()
                    : ((ZombieVillager) nearbyCast).getVillagerData();

            return selfData.getProfession() == VillagerProfession.NONE &&
                    nearbyData.getProfession() == VillagerProfession.NONE;
        } else if (self instanceof Slime) {
            return ((Slime) self).getSize() == ((Slime) nearby).getSize();
        }

        Map<Class<? extends Mob>, Function<Mob, Object>> variantCheckers = Map.of(
                Frog.class, e -> ((Frog) e).getVariant(),
                Axolotl.class, e -> ((Axolotl) e).getVariant(),
                Cat.class, e -> ((Cat) e).getVariant(),
                Fox.class, e -> ((Fox) e).getVariant(),
                MushroomCow.class, e -> ((MushroomCow) e).getVariant()
        );

        Function<Mob, Object> variantChecker = variantCheckers.get(self.getClass());
        if (variantChecker != null) {
            if (!variantChecker.apply(self).equals(variantChecker.apply(nearby))) {
                return false;
            }
        }

        // Apply custom merge checks
        return MobStackerAPI.checkCustomMergingConditions(self, nearby);
    }

    public static void spawnNewEntity(ServerLevel serverLevel, Mob self, int stackSize) {
        EntityType<?> entityType = self.getType();
        Mob newEntity = (Mob) entityType.create(serverLevel);
        if (newEntity != null) {
            newEntity.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(self.blockPosition()), MobSpawnType.NATURAL, null);
            newEntity.moveTo(self.position().x, self.position().y, self.position().z, self.getYRot(), self.getXRot());
            newEntity.yBodyRot = self.yBodyRot;
            if (self.hasCustomName()) {
                newEntity.setCustomName(self.getCustomName());
            }
            MobStacker.setStackSize(newEntity, stackSize - 1);

            switch (self) {
                case Sheep sheep -> {
                    ((Sheep) newEntity).setSheared(sheep.isSheared());
                    ((Sheep) newEntity).setColor(sheep.getColor());
                }
                case Villager villager -> {
                    ((Villager) newEntity).setVillagerData(villager.getVillagerData());
                    ((Villager) newEntity).setVariant(villager.getVariant());
                }
                case ZombieVillager zombieVillager -> {
                    ((ZombieVillager) newEntity).setVillagerData(zombieVillager.getVillagerData());
                    ((ZombieVillager) newEntity).setVariant(zombieVillager.getVariant());
                }
                case Slime slime -> ((Slime) newEntity).setSize(slime.getSize(), true);
                case Frog frog -> ((Frog) newEntity).setVariant(frog.getVariant());
                case Axolotl axolotl -> ((Axolotl) newEntity).setVariant(axolotl.getVariant());
                case Cat cat -> ((Cat) newEntity).setVariant(cat.getVariant());
                case Fox fox -> ((Fox) newEntity).setVariant(fox.getVariant());
                case MushroomCow mushroomCow -> ((MushroomCow) newEntity).setVariant(mushroomCow.getVariant());
                default -> {
                }
            }

            // Apply custom entity data
            MobStackerAPI.applyEntityDataModifiers(self, newEntity);
            serverLevel.addFreshEntity(newEntity);
        }
    }

    public static void mergeEntities(Mob target, Mob source) {
        int targetStack = getStackSize(target);
        int sourceStack = getStackSize(source);
        int newStackSize = Math.min(targetStack + sourceStack, getMaxMobStackSize());

        CompoundTag targetNbt = new CompoundTag();
        CompoundTag sourceNbt = new CompoundTag();
        target.saveWithoutId(targetNbt);
        source.saveWithoutId(sourceNbt);

        Almanac.dropEquipmentOnDiscard(source);
        Almanac.dropEquipmentOnDiscard(target);

        for (String key : sourceNbt.getAllKeys()) {
            if (!key.equals("Pos") && !key.equals("UUID") && !key.equals("Motion") && !key.equals("Health")) {
                targetNbt.put(key, sourceNbt.get(key));
            }
        }

        if (!targetNbt.contains("CustomData", 10)) {
            targetNbt.put("CustomData", new CompoundTag());
        }
        CompoundTag customData = targetNbt.getCompound("CustomData");

        customData.putInt(STACK_SIZE_KEY, newStackSize);

        target.load(targetNbt);

        updateHealth(target, source);

        source.discard();

        updateStackDisplay(target);
    }

    public static void separateEntity(Mob entity) {
        try {
            if (!entity.level().isClientSide()) {
                EntityType<?> entityType = entity.getType();
                Mob newEntity = (Mob) entityType.create(entity.level());
                ServerLevel serverLevel = (ServerLevel) entity.level();
                MobStacker.setStackSize(entity, MobStacker.getStackSize(entity) - 1);
                if (newEntity != null) {
                    newEntity.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(entity.blockPosition()), MobSpawnType.NATURAL, null);
                    newEntity.moveTo(entity.position().x, entity.position().y, entity.position().z, entity.getYRot(), entity.getXRot());
                    newEntity.yBodyRot = entity.yBodyRot;
                    newEntity.setCustomName(Component.literal("Lone " + Almanac.getLocalizedEntityName(entity.getType()).getString()));

                    switch (entity) {
                        case Sheep sheep -> {
                            ((Sheep) newEntity).setSheared(sheep.isSheared());
                            ((Sheep) newEntity).setColor(sheep.getColor());
                        }
                        case Villager villager -> {
                            ((Villager) newEntity).setVillagerData(villager.getVillagerData());
                            ((Villager) newEntity).setVariant(villager.getVariant());
                        }
                        case ZombieVillager zombieVillager -> {
                            ((ZombieVillager) newEntity).setVillagerData(zombieVillager.getVillagerData());
                            ((ZombieVillager) newEntity).setVariant(zombieVillager.getVariant());
                        }
                        case Slime slime -> ((Slime) newEntity).setSize(slime.getSize(), true);
                        case Frog frog -> ((Frog) newEntity).setVariant(frog.getVariant());
                        case Axolotl axolotl -> ((Axolotl) newEntity).setVariant(axolotl.getVariant());
                        case Cat cat -> ((Cat) newEntity).setVariant(cat.getVariant());
                        case Fox fox -> ((Fox) newEntity).setVariant(fox.getVariant());
                        case MushroomCow mushroomCow -> ((MushroomCow) newEntity).setVariant(mushroomCow.getVariant());
                        default -> {
                        }
                    }

                    if (MobStacker.getStackHealth() && entity.getHealth() > newEntity.getMaxHealth()) {
                        entity.setHealth(entity.getHealth() - newEntity.getMaxHealth());
                    }

                    // Apply custom entity data
                    MobStackerAPI.applyEntityDataModifiersOnSeparation(entity, newEntity);
                    entity.level().addFreshEntity(newEntity);
                }
            }
        } catch (Exception e) {
            MobStacker.setStackSize(entity, MobStacker.getStackSize(entity) + 1);
            logger.error("Error occurred while separating entity: {}", e.getMessage());
        }
    }

    public static int getStackSize(Mob entity) {
        if (entity instanceof ICustomDataHolder) {
            CompoundTag customData = ((ICustomDataHolder) entity).mobstacker$getCustomData();
            return customData.contains(STACK_SIZE_KEY) ? customData.getInt(STACK_SIZE_KEY) : 1;
        }
        return 1;
    }

    public static void setStackSize(Mob entity, int size) {
        if (entity instanceof ICustomDataHolder) {
            ((ICustomDataHolder) entity).mobstacker$getCustomData().putInt(STACK_SIZE_KEY, size);
            updateStackDisplay(entity);
        }
    }

    public static void updateStackDisplay(Mob entity) {
        int stackSize = getStackSize(entity);

        boolean isBoss = isBossEntity(entity);

        if (entity.hasCustomName() && !Almanac.matchesStackedName(entity.getCustomName().getString(), entity)) {
            Component customName = entity.getCustomName();
            String customNameString = customName.getString();

            customNameString = customNameString.replaceFirst(" x\\d+$", "");

            String newNameString = customNameString + (stackSize > 1 ? " x" + stackSize : "");

            Component newName = Component.literal(newNameString).withStyle(customName.getStyle());

            entity.setCustomName(newName);
            if (isBoss) {
                getBossField(entity).setName(newName);
            }
        } else if (stackSize > 1) {
            entity.setCustomName(Component.literal(Almanac.getLocalizedEntityName(entity.getType()).getString() + " x" + stackSize));
            if (isBoss) {
                getBossField(entity).setName(Component.literal(Almanac.getLocalizedEntityName(entity.getType()).getString() + " x" + stackSize));
            }
        } else {
            if (!isBoss) {
                if(entity.hasCustomName()) {
                    entity.setCustomName(null);
                }
            } else {
                delayExecution((ServerLevel) entity.level(), 1, () -> {
                    ServerBossEvent bossEvent = getBossField(entity);
                    if (bossEvent != null) {
                        bossEvent.setName(Component.literal(entity.getDisplayName().getString()));
                        entity.setCustomName(null);
                    }
                });
            }
        }
        if (!isBoss) {
            if (Almanac.hasNonCustomName(entity)) entity.setCustomNameVisible(false);
        }
    }

    public static void updateHealth(Mob target, Mob source) {
        double maxHealth = target.getMaxHealth();
        float newHealth = target.getHealth() + source.getHealth();
        if (getStackHealth() && getKillWholeStackOnDeath()) {
            maxHealth += source.getMaxHealth();
            target.getAttribute(Attributes.MAX_HEALTH).setBaseValue(maxHealth);
        }
        target.setHealth(Math.min(newHealth, (float) maxHealth));
    }

    public static void delayExecution(ServerLevel serverLevel, int ticksDelay, Runnable task) {
        serverLevel.getServer().execute(new Runnable() {
            int ticksLeft = ticksDelay;

            @Override
            public void run() {
                if (ticksLeft > 0) {
                    ticksLeft--;
                    serverLevel.getServer().execute(this);
                } else {
                    task.run();
                }
            }
        });
    }

    public static boolean isBossEntity(Entity entity) {
        if (!(entity instanceof Mob)) return false;
        Class<?> entityClass = entity.getClass();
        Class<?> bossClass = ServerBossEvent.class;

        Field[] fields = entityClass.getDeclaredFields();
        for (Field field : fields) {
            if (field.getType().equals(bossClass)) {
                return true;
            }
        }
        return false;
    }

    public static ServerBossEvent getBossField(Entity entity) {
        Class<?> entityClass = entity.getClass();
        Class<?> bossClass = ServerBossEvent.class;

        Field[] fields = entityClass.getDeclaredFields();

        for (Field field : fields) {
            if (field.getType().equals(bossClass)) {
                try {
                    field.setAccessible(true);
                    return (ServerBossEvent) field.get(entity);
                } catch (IllegalAccessException e) {
                    logger.error("Could not set ServerBossEvent field as accessible for entity: {} ", entity.getType(), e);
                }
            }
        }

        return null;
    }

    public static boolean hasValidCustomNameForStacking(Mob entity) {
        if (entity.hasCustomName()) {
            String customName = entity.getCustomName().getString();
            return matchesStackedName(customName, entity); // Return true if it doesn't already have a stack indicator
        }
        return true;
    }

    public static boolean matchesStackedName(String customName, Entity entity) {
        return Pattern.compile(Pattern.quote(Almanac.getLocalizedEntityName(entity.getType()).getString()) + " x\\d+").matcher(customName).find();
    }

    public static boolean shouldSpawnNewEntity(Mob entity, Entity.RemovalReason reason) {
        return (entity instanceof Creeper creeper && creeper.isIgnited()) || reason == Entity.RemovalReason.KILLED;
    }

    public static double getStackRadius() {
        return config.getStackRadius();
    }

    public static int getMaxMobStackSize() {
        return config.getMaxMobStackSize();
    }

    public static boolean getKillWholeStackOnDeath() {
        return config.getKillWholeStackOnDeath();
    }

    public static boolean getStackHealth() {
        return config.getStackHealth();
    }

    public static boolean getEnableSeparator() {
        return config.getEnableSeparator();
    }

    public static boolean getConsumeSeparator() {
        return config.getConsumeSeparator();
    }

    public static String getSeparatorItem() {
        return config.getSeparatorItem();
    }
}
