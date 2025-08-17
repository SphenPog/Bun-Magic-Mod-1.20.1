package net.sphen.magicmodbuns.spells.runes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.Map;

public class RuneReloadListener extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().create();

    public RuneReloadListener() {
        super(GSON, "runes");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> pJson, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
     RuneRegistry.clear();

     pJson.forEach((id, element) -> {
         try {
             RuneDefinition definition = RuneDefinition.fromJson(id, element.getAsJsonObject());
             RuneRegistry.register(definition);
             System.out.println("Loaded rune: " + definition.name + " -> " +definition.signatures);
         } catch (Exception e) {
             System.err.println("Failed to load rune " + id + ": " + e.getMessage());
         }
     });

        System.out.println("Loaded " + RuneRegistry.size() + " runes.");
    }
}
