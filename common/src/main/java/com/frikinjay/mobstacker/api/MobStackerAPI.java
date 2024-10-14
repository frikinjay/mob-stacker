package com.frikinjay.mobstacker.api;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

public class MobStackerAPI {
    private static final List<BiPredicate<Mob, Mob>> mergingConditions = new ArrayList<>();
    private static final List<BiConsumer<Mob, DamageSource>> deathHandlers = new ArrayList<>();
    private static final List<BiConsumer<Mob, Mob>> entityDataModifiers = new ArrayList<>();
    private static final List<BiConsumer<Mob, Mob>> entityDataModifiersOnSeparation = new ArrayList<>();

    /**
     * Adds a custom condition for merging entities.
     * @param condition A BiPredicate that takes two LivingEntity objects and returns true if they can be merged.
     */
    public static void addMergingCondition(BiPredicate<Mob, Mob> condition) {
        mergingConditions.add(condition);
    }

    /**
     * Checks all custom merging conditions.
     * @param entity1 The first entity to check.
     * @param entity2 The second entity to check.
     * @return true if all custom conditions are met, false otherwise.
     */
    public static boolean checkCustomMergingConditions(Mob entity1, Mob entity2) {
        for (BiPredicate<Mob, Mob> condition : mergingConditions) {
            if (!condition.test(entity1, entity2)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Adds a custom handler for entity death.
     * @param handler A BiConsumer that takes a LivingEntity object and a DamageSource, and performs actions on the entity's death.
     */
    public static void addDeathHandler(BiConsumer<Mob, DamageSource> handler) {
        deathHandlers.add(handler);
    }

    /**
     * Executes all custom death handlers for stacked entities.
     * @param entity The entity that died.
     * @param damageSource The source of damage that caused the death.
     */
    public static void executeCustomDeathHandlers(Mob entity, DamageSource damageSource) {
        for (BiConsumer<Mob, DamageSource> handler : deathHandlers) {
            handler.accept(entity, damageSource);
        }
    }

    /**
     * Adds a custom modifiers on entity death and new entity creation if the entity is a stacked entity.
     * @param handler A BiConsumer that takes two LivingEntity objects, and performs actions on the dying entity and the replacement entity.
     */
    public static void addEntityDataModifiers(BiConsumer<Mob, Mob> handler) {
        entityDataModifiers.add(handler);
    }

    /**
     * Applies all custom death modifiers for stacked entities.
     * @param dyingEntity The entity that died.
     * @param newEntity The replacement entity with a reduced stack size by 1.
     */
    public static void applyEntityDataModifiers(Mob dyingEntity, Mob newEntity) {
        for (BiConsumer<Mob, Mob> handler : entityDataModifiers) {
            handler.accept(dyingEntity, newEntity);
        }
    }

    /**
     * Adds a custom modifiers on entity separation and new separated lone entity creation if the entity is a stacked entity.
     * @param handler A BiConsumer that takes two LivingEntity objects, and performs actions on the stacked entity and the separated entity.
     */
    public static void addEntityDataModifiersOnSeparation(BiConsumer<Mob, Mob> handler) {
        entityDataModifiersOnSeparation.add(handler);
    }

    /**
     * Applies all custom separation modifiers for stacked entities.
     * @param stackedEntity The main stacked entity with a reduced stack size by 1.
     * @param separatedEntity The separated entity with a stack size of 1.
     */
    public static void applyEntityDataModifiersOnSeparation(Mob stackedEntity, Mob separatedEntity) {
        for (BiConsumer<Mob, Mob> handler : entityDataModifiersOnSeparation) {
            handler.accept(stackedEntity, separatedEntity);
        }
    }
}