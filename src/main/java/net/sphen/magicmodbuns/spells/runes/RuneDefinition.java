package net.sphen.magicmodbuns.spells.runes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

public class RuneDefinition {
    public final String name;
    public final Set<String> signatures;
    public final RuneType type;

    public RuneDefinition(String name, RuneType type, Set<String> signatures) {
        this.name = name;
        this.type = type;
        this.signatures = signatures;
    }

    public static RuneDefinition fromJson(ResourceLocation id, JsonObject json) {
        String name = GsonHelper.getAsString(json, "name", id.getPath());

        String typeStr = GsonHelper.getAsString(json, "type");
        RuneType runeType = RuneType.valueOf(typeStr.toUpperCase(Locale.ROOT));

        Set<String> signatures = new LinkedHashSet<>();
        if (json.has("signature")) {
            signatures.add(GsonHelper.getAsString(json, "signature"));
        }
        if (json.has("signatures")) {
            JsonArray array = GsonHelper.getAsJsonArray(json, "signatures");
            for (JsonElement element : array) {
                signatures.add(GsonHelper.convertToString(element, "signature"));
            }
        }
        if (signatures.isEmpty()) {
            throw new IllegalArgumentException("Rune " + id + " must define 'signature' or 'signatures'.");
        }

        return new RuneDefinition(name, runeType, signatures);
    }
}
