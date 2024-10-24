package com.frikinjay.mobstacker.config;

import com.frikinjay.almanac.Almanac;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.frikinjay.mobstacker.MobStacker.CONFIG_FILE;

public class MobStackerConfig {
    private static final int MAX_CAP_VALUE = 128;
    private static final double MAX_RADIUS = 42000.0;

    private boolean killWholeStackOnDeath = false;
    private boolean stackHealth = false;
    private int maxMobStackSize = 16;
    private double stackRadius = 6.0;
    private boolean enableSeparator = false;
    private boolean consumeSeparator = true;
    private String separatorItem = "minecraft:diamond";

    private List<String> ignoredEntities = new ArrayList<>(Arrays.asList(
            "minecraft:ender_dragon",
            "minecraft:vex"
    ));
    private List<String> ignoredMods = new ArrayList<>(Arrays.asList(
            "corpse"
    ));

    private final MobCaps mobCaps = new MobCaps();

    public static MobStackerConfig load() {
        return Almanac.loadConfig(CONFIG_FILE, MobStackerConfig.class);
    }

    public void save() {
        if (stackHealth && !killWholeStackOnDeath) {
            killWholeStackOnDeath = true;
        }
        Almanac.saveConfig(CONFIG_FILE, this);
    }

    public String getSeparatorItem() { return separatorItem; }
    public boolean getConsumeSeparator() { return consumeSeparator; }
    public boolean getEnableSeparator() { return enableSeparator; }
    public boolean getKillWholeStackOnDeath() { return killWholeStackOnDeath; }
    public boolean getStackHealth() { return stackHealth; }
    public int getMaxMobStackSize() { return maxMobStackSize; }
    public double getStackRadius() { return stackRadius; }

    public void setSeparatorItem(String separatorItem) {
        this.separatorItem = separatorItem;
        save();
    }

    public void setConsumeSeparator(boolean consumeSeparator) {
        this.consumeSeparator = consumeSeparator;
        save();
    }

    public void setEnableSeparator(boolean enableSeparator) {
        this.enableSeparator = enableSeparator;
        save();
    }

    public void setKillWholeStackOnDeath(boolean killWholeStackOnDeath) {
        this.killWholeStackOnDeath = killWholeStackOnDeath;
        save();
    }

    public void setStackHealth(boolean stackHealth) {
        this.stackHealth = stackHealth;
        save();
    }

    public void setMaxMobStackSize(int maxMobStackSize) {
        this.maxMobStackSize = maxMobStackSize;
        save();
    }

    public void setStackRadius(double stackRadius) {
        this.stackRadius = Math.min(stackRadius, MAX_RADIUS);
        save();
    }

    public List<String> getIgnoredEntities() {
        return Collections.unmodifiableList(ignoredEntities);
    }

    public List<String> getIgnoredMods() {
        return Collections.unmodifiableList(ignoredMods);
    }

    public boolean addIgnoredEntity(String entityId) {
        return addToList(entityId, ignoredEntities);
    }

    public boolean removeIgnoredEntity(String entityId) {
        return removeFromList(entityId, ignoredEntities);
    }

    public boolean addIgnoredMod(String modId) {
        return addToList(modId, ignoredMods);
    }

    public boolean removeIgnoredMod(String modId) {
        return removeFromList(modId, ignoredMods);
    }

    private boolean addToList(String item, List<String> list) {
        if (!list.contains(item)) {
            list.add(item);
            save();
            return true;
        }
        return false;
    }

    private boolean removeFromList(String item, List<String> list) {
        if (list.remove(item)) {
            save();
            return true;
        }
        return false;
    }

    public int getMonsterMobCap() { return mobCaps.monster; }
    public int getCreatureMobCap() { return mobCaps.creature; }
    public int getAmbientMobCap() { return mobCaps.ambient; }
    public int getAxolotlsMobCap() { return mobCaps.axolotls; }
    public int getUndergroundWaterCreatureMobCap() { return mobCaps.undergroundWaterCreature; }
    public int getWaterCreatureMobCap() { return mobCaps.waterCreature; }
    public int getWaterAmbientMobCap() { return mobCaps.waterAmbient; }

    public void setMonsterMobCap(int value) { mobCaps.setMonster(value); save(); }
    public void setCreatureMobCap(int value) { mobCaps.setCreature(value); save(); }
    public void setAmbientMobCap(int value) { mobCaps.setAmbient(value); save(); }
    public void setAxolotlsMobCap(int value) { mobCaps.setAxolotls(value); save(); }
    public void setUndergroundWaterCreatureMobCap(int value) { mobCaps.setUndergroundWaterCreature(value); save(); }
    public void setWaterCreatureMobCap(int value) { mobCaps.setWaterCreature(value); save(); }
    public void setWaterAmbientMobCap(int value) { mobCaps.setWaterAmbient(value); save(); }

    private static class MobCaps {
        private int monster = 22;
        private int creature = 5;
        private int ambient = 7;
        private int axolotls = 2;
        private int undergroundWaterCreature = 2;
        private int waterCreature = 2;
        private int waterAmbient = 8;

        private void setMonster(int value) { monster = validateCap(value); }
        private void setCreature(int value) { creature = validateCap(value); }
        private void setAmbient(int value) { ambient = validateCap(value); }
        private void setAxolotls(int value) { axolotls = validateCap(value); }
        private void setUndergroundWaterCreature(int value) { undergroundWaterCreature = validateCap(value); }
        private void setWaterCreature(int value) { waterCreature = validateCap(value); }
        private void setWaterAmbient(int value) { waterAmbient = validateCap(value); }

        private static int validateCap(int value) {
            return Math.min(value, MAX_CAP_VALUE);
        }
    }
}