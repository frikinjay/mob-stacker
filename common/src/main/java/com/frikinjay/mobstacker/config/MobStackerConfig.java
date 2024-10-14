package com.frikinjay.mobstacker.config;

import com.frikinjay.almanac.Almanac;
import com.frikinjay.mobstacker.MobStacker;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MobStackerConfig {
    private static final File CONFIG_FILE = new File("config/mobstacker.json");

    private boolean killWholeStackOnDeath = false;
    private boolean stackHealth = false;
    private int maxMobStackSize = 16;
    private double stackRadius = 6.0;
    private boolean enableSeparator = false;
    private boolean consumeSeparator = true;
    private String separatorItem = "minecraft:diamond";
    private List<String> ignoredEntities = new ArrayList<>(List.of("minecraft:ender_dragon"));
    private List<String> ignoredMods = new ArrayList<>(List.of("corpse"));

    public static MobStackerConfig load() {
        return Almanac.loadConfig(CONFIG_FILE, MobStackerConfig.class);
    }

    public void save() {
        if(MobStacker.getStackHealth()) {
            if(!MobStacker.getKillWholeStackOnDeath()) this.killWholeStackOnDeath = true;
        }
        Almanac.saveConfig(CONFIG_FILE, this);
    }

    public String getSeparatorItem() {
        return separatorItem;
    }

    public void setSeparatorItem(String separatorItem) {
        this.separatorItem = separatorItem;
        save();
    }

    public boolean getConsumeSeparator() {
        return consumeSeparator;
    }

    public void setConsumeSeparator(boolean consumeSeparator) {
        this.consumeSeparator = consumeSeparator;
        save();
    }

    public boolean getEnableSeparator() {
        return enableSeparator;
    }

    public void setEnableSeparator(boolean enableSeparator) {
        this.enableSeparator = enableSeparator;
        save();
    }

    public boolean getKillWholeStackOnDeath() {
        return killWholeStackOnDeath;
    }

    public void setKillWholeStackOnDeath(boolean killWholeStackOnDeath) {
        this.killWholeStackOnDeath = killWholeStackOnDeath;
        save();
    }

    public boolean getStackHealth() {
        return stackHealth;
    }

    public void setStackHealth(boolean stackHealth) {
        this.stackHealth = stackHealth;
        save();
    }

    public int getMaxMobStackSize() {
        return maxMobStackSize;
    }

    public void setMaxMobStackSize(int maxMobStackSize) {
        this.maxMobStackSize = maxMobStackSize;
        save();
    }

    public double getStackRadius() {
        return stackRadius;
    }

    public void setStackRadius(double stackRadius) {
        if (stackRadius > 42000) {
            stackRadius = 42000;
        }
        this.stackRadius = stackRadius;
        save();
    }

    public List<String> getIgnoredEntities() {
        return new ArrayList<>(ignoredEntities);
    }

    public boolean addIgnoredEntity(String entityId) {
        if (!ignoredEntities.contains(entityId)) {
            ignoredEntities.add(entityId);
            save();
            return true;
        }
        return false;
    }

    public boolean removeIgnoredEntity(String entityId) {
        if (ignoredEntities.remove(entityId)) {
            save();
            return true;
        }
        return false;
    }

    public List<String> getIgnoredMods() {
        return new ArrayList<>(ignoredMods);
    }

    public boolean addIgnoredMod(String modId) {
        if (!ignoredMods.contains(modId)) {
            ignoredMods.add(modId);
            save();
            return true;
        }
        return false;
    }

    public boolean removeIgnoredMod(String modId) {
        if (ignoredMods.remove(modId)) {
            save();
            return true;
        }
        return false;
    }
}