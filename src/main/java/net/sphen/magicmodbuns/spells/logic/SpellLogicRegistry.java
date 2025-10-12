package net.sphen.magicmodbuns.spells.logic;

import net.minecraft.resources.ResourceLocation;
import net.sphen.magicmodbuns.MagicMod;
import net.sphen.magicmodbuns.spells.SpellLogic;

import java.util.HashMap;
import java.util.Map;

public class SpellLogicRegistry {
    private static final Map<ResourceLocation, SpellLogic> LOGICS = new HashMap<>();

    public static void register(ResourceLocation id, SpellLogic logic) {
        LOGICS.put(id, logic);
    }

    public static SpellLogic get(ResourceLocation id) {
        return LOGICS.get(id);
    }

    public static void init() {
        register(new ResourceLocation(MagicMod.MODID, "place_block"), new SpellLogicPlaceBlock());
        register(new ResourceLocation(MagicMod.MODID, "locate"), new SpellLogicLocate());
        register(new ResourceLocation(MagicMod.MODID, "gust"), new SpellLogicGust());
        register(new ResourceLocation(MagicMod.MODID, "add_effect"), new SpellLogicAddEffect());
        // add more here
    }

}
