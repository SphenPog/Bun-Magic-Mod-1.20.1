package net.sphen.magicmodbuns.spells;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.sphen.magicmodbuns.MagicMod;
import net.sphen.magicmodbuns.spells.runes.RuneRegistry;
import net.sphen.magicmodbuns.spells.runes.RuneType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SpellLoader extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final Map<ResourceLocation, SpellDefinition> spells = new HashMap<>();

    public SpellLoader() {
        super(GSON, "spells");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsons, ResourceManager manager, ProfilerFiller profiler) {
        spells.clear();
        MagicMod.LOGGER.info("[SpellLoader] Reloading {} spell JSONs", jsons.size());

        for (var entry : jsons.entrySet()) {
            try {
                JsonObject obj = entry.getValue().getAsJsonObject();
                SpellDefinition def = new SpellDefinition(
                        entry.getKey(),
                        obj.getAsJsonObject("config"),
                        SpellLogicRegistry.get(new ResourceLocation(obj.get("logic").getAsString()))
                );
                spells.put(def.id, def);
                MagicMod.LOGGER.info("[SpellLoader] Loaded spell {} with logic {}", def.id, def.logic);
            } catch (Exception e) {
                MagicMod.LOGGER.error("Failed to load spell {}: {}", entry.getKey(), e);
            }
        }
        MagicMod.LOGGER.info("Loaded {} spells", spells.size());
    }

    public static SpellDefinition findSpellFromRunes(Set<RuneType> runeTypes) {

        if (runeTypes == null || runeTypes.isEmpty()) return null;

        for (SpellDefinition definition : spells.values()) {
            if (!definition.config.has("runes")) continue;

            Set<RuneType> spellRunes = new HashSet<>();

            definition.config.getAsJsonArray("runes").forEach(e -> {

                String id = e.getAsString(); // e.g. "magicmodbuns:light"
                RuneType type = RuneRegistry.getRuneTypeFromId(id);

                if (type != null) spellRunes.add(type);
            });

            MagicMod.LOGGER.info("[Spell Loader] spell runes: " + spellRunes);

            if (runeTypes.equals(spellRunes)) {
                return definition;
            }
        }

        return null;
    }
}
