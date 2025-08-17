package net.sphen.magicmodbuns.spells.runes;

import net.sphen.magicmodbuns.screen.elements.PatternObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RuneRegistry {
    private static final Map<String, RuneType> runeSignatures = new HashMap<>();
    private static int count = 0;

    public static RuneType detectRune(PatternObject pattern) {
        return runeSignatures.getOrDefault(pattern.getSortedLines(), RuneType.UNKNOWN);
    }

    public static int size() {
        return count;
    }

    public static void clear() {
        runeSignatures.clear();
        count = 0;
    }

    public static void register(RuneDefinition definition) {
        for (String signature : definition.signatures) {
            runeSignatures.put(signature, definition.type);
        }
        count++;
    }

    public static Set<String> getAllSignatures() {
        return runeSignatures.keySet();
    }
}
