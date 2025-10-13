package net.sphen.magicmodbuns.spells.util;

import net.minecraft.server.level.ServerLevel;
import net.sphen.magicmodbuns.spells.actions.ApplyForceAction;
import net.sphen.magicmodbuns.spells.actions.SpawnParticlesAction;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

//manages all active GustZone effects.
public class GustManager {
    private static final ConcurrentHashMap<UUID, GustZone> activeGusts = new ConcurrentHashMap<>();
    private static final SpawnParticlesAction spawnParticlesAction = new SpawnParticlesAction();
    private static final ApplyForceAction applyForceAction = new ApplyForceAction();

    public static void addGust(GustZone gust) {
        activeGusts.put(gust.id(), gust);
    }

    public static void onServerTick(ServerLevel level) {
        if (activeGusts.isEmpty()) return;

        activeGusts.forEach(((uuid, gust) -> {
            if (gust.isExpired()) {
                activeGusts.remove(uuid);
                return;
            }

            // Use the generic particle action
            spawnParticlesAction.execute(level, gust.areaOfEffect(), gust.particle(), gust.forceVector().multiply(0.25, 0.25, 0.25), 1, 0.25);
            applyForceAction.execute(level, gust.areaOfEffect(), gust.forceVector());

            activeGusts.replace(uuid, gust.tick());
        }));
    }
}

