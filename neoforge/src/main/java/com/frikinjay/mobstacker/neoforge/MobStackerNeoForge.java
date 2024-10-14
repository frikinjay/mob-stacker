package com.frikinjay.mobstacker.neoforge;

import com.frikinjay.mobstacker.MobStacker;
import net.neoforged.fml.common.Mod;

@Mod(MobStacker.MOD_ID)
public final class MobStackerNeoForge {
    public MobStackerNeoForge() {
        // Run our common setup.
        MobStacker.init();
    }
}
