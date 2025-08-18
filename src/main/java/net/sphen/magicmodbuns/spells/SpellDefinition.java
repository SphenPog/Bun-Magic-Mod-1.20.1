package net.sphen.magicmodbuns.spells;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

public class SpellDefinition {
   public ResourceLocation id;
   public JsonObject config;
   public SpellLogic logic;

   public SpellDefinition(ResourceLocation id, JsonObject config, SpellLogic logic){
      this.id = id;
      this.config = config;
      this.logic = logic;
   }
}
