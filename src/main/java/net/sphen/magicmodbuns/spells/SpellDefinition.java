package net.sphen.magicmodbuns.spells;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

class SpellDefinition {
    ResourceLocation id;
    JsonObject config;
    SpellLogic logic;
}
