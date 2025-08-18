package net.sphen.magicmodbuns.spells.runes;

import net.sphen.magicmodbuns.screen.elements.PatternObject;
import net.sphen.magicmodbuns.spells.SpellDefinition;

import java.util.*;

public class RuneRegistry {
    private static final Map<String, RuneType> singleRuneSignatures = new HashMap<>();
    private static final Map<String, SpellDefinition> comboRuneSignatures = new HashMap<>();
    private static final Map<String, RuneType> idToType = new HashMap<>();
    private static int count = 0;

    private static String keyFromRunes(Set<RuneType> runes) {
        if (runes == null || runes.isEmpty()) return "";

        List<String> sorted = new ArrayList<>();
        for (RuneType rune : runes) {
            if (rune != null && rune != RuneType.UNKNOWN) {
                sorted.add(rune.name());
            }
        }

        if (sorted.isEmpty()) return "";
        Collections.sort(sorted);
        return String.join("+", sorted);
    }

    public static void registerSingleRune(RuneDefinition definition) {
        for (String signature : definition.signatures) {
            String normalized = PatternObject.loadData(signature).getSortedLines();
            singleRuneSignatures.put(normalized, definition.type);
        }
        count++;
    }

    public static RuneType detectSingleRune(String signature) {
        return singleRuneSignatures.getOrDefault(signature, RuneType.UNKNOWN);
    }

    public static Set<String> getAllSignatures() {
        return singleRuneSignatures.keySet();
    }

    public static void registerSpell(Set<RuneType> runes, SpellDefinition spell) {
        comboRuneSignatures.put(keyFromRunes(runes), spell);
    }

    public static SpellDefinition detectSpell(Set<RuneType> runes) {
        return comboRuneSignatures.get(keyFromRunes(runes));
    }

    public static int size() {
        return count;
    }

    public static void clear() {
        singleRuneSignatures.clear();
        comboRuneSignatures.clear();
        count = 0;
    }

    public static void registerRuneId(String id, RuneType type) {
        idToType.put(id, type);
    }

    public static RuneType getRuneTypeFromId(String id) {
        return idToType.getOrDefault(id, null);
    }
}
