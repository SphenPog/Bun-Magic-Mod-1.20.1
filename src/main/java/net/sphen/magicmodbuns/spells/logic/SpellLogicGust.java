package net.sphen.magicmodbuns.spells.logic;

import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.sphen.magicmodbuns.spells.SpellInstance;
import net.sphen.magicmodbuns.spells.SpellLogic;
import net.sphen.magicmodbuns.spells.actions.ClearAreaAction;
import net.sphen.magicmodbuns.spells.actions.CreateBoundingBoxAction;
import net.sphen.magicmodbuns.spells.runes.RunePatternGraph;
import net.sphen.magicmodbuns.spells.util.GustManager;
import net.sphen.magicmodbuns.spells.util.GustZone;

import java.util.List;
import java.util.UUID;

public class SpellLogicGust extends SpellLogic {
    private final CreateBoundingBoxAction createBoundingBoxAction = new CreateBoundingBoxAction();
    private final ClearAreaAction clearAreaAction = new ClearAreaAction();

    @Override
    public void cast(SpellInstance instance, Player player, RunePatternGraph graph) {
        Level level = player.level();
        if (level.isClientSide()) return;

        JsonObject spellConfig = instance.definition.config;

        //create and clear a box where the runes for the spell are
        JsonObject runeConfig = new JsonObject();
        runeConfig.addProperty("shape", "rune_area");
        List<AABB> runeBounds = this.createBoundingBoxAction.execute(player, graph, runeConfig);
        this.clearAreaAction.execute(level, runeBounds);

        //create bounding box for gust dependent on the config
        List<AABB> gustEffectBounds = this.createBoundingBoxAction.execute(player, graph, spellConfig);
        registerGustZones(player, graph.getOrigin(), spellConfig, gustEffectBounds);
    }

    private void registerGustZones(Player player, BlockPos origin, JsonObject config, List<AABB> effectBounds) {
        // get properties of gust from the spell config
        double strength = config.has("strength") ? config.get("strength").getAsDouble() : 0.5;
        int duration = config.has("duration") ? config.get("duration").getAsInt() : 5;
        Direction pushDirection = getDirectionFromPlayerToOrigin(player, origin);
        Vec3 forceVector = new Vec3(pushDirection.getStepX(), pushDirection.getStepY(), pushDirection.getStepZ()).scale(strength);

        // Create a GustZone for the given bounds
        for (AABB bounds : effectBounds) {
            GustZone newGust = new GustZone(
                    UUID.randomUUID(),
                    bounds,
                    forceVector,
                    duration * 20, // Convert seconds to ticks
                    0,
                    ParticleTypes.CLOUD
            );
            GustManager.addGust(newGust);
        }
    }

    private Direction getDirectionFromPlayerToOrigin(Player player, BlockPos origin) {
        Vec3 vecToOrigin = Vec3.atCenterOf(origin).subtract(player.position());
        return Direction.getNearest(vecToOrigin.x, -vecToOrigin.y, vecToOrigin.z);
    }
}
