package com.frikinjay.mobstacker.neoforge.util;

import com.google.common.collect.ImmutableMap;
import net.neoforged.fml.loading.LoadingModList;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ConditionalLoaderMixinPlugin implements IMixinConfigPlugin {

    private static final Map<String, String> MIXIN_MODIDS = ImmutableMap.of(
            "com.frikinjay.mobstacker.forge.mixin.industrialforegoing.MobImprisonmentToolItemMixin", "industrialforegoing"
    );

    @Override
    public void onLoad(String s) {

    }

    @Override
    public String getRefMapperConfig() {
        return "";
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        final String MODID = MIXIN_MODIDS.get(mixinClassName);

        if (MODID == null) {
            return true;
        }

        return LoadingModList.get().getMods().stream().anyMatch(modInfo -> modInfo.getModId().equals(MODID));
    }

    @Override
    public void acceptTargets(Set<String> set, Set<String> set1) {

    }

    @Override
    public List<String> getMixins() {
        return List.of();
    }

    @Override
    public void preApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {

    }

    @Override
    public void postApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {

    }

}
