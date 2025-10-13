package net.sphen.magicmodbuns.spells.logic;

import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.sphen.magicmodbuns.spells.SpellInstance;
import net.sphen.magicmodbuns.spells.SpellLogic;
import net.sphen.magicmodbuns.spells.actions.*;
import net.sphen.magicmodbuns.spells.runes.RunePatternGraph;
import net.sphen.magicmodbuns.spells.util.recipes.ILocateRecipes;
import net.sphen.magicmodbuns.spells.util.recipes.LocateRecipeRegistry;

import java.util.List;

public class SpellLogicLocate extends SpellLogic {

    private final CreateBoundingBoxAction createBoundingBoxAction = new CreateBoundingBoxAction();
    private final GetEntitiesInAreaAction getEntitiesInAreaAction = new GetEntitiesInAreaAction();
    private final ClearAreaAction clearAreaAction = new ClearAreaAction();
    private final PlaySoundAction playSoundAction = new PlaySoundAction();
    private final SpawnParticlesAction spawnParticlesAction = new SpawnParticlesAction();
    private final DestroyRandomItemAction destroyRandomItemAction = new DestroyRandomItemAction();

    @Override
    public void cast(SpellInstance instance, Player player, RunePatternGraph graph) {
        Level level = player.getCommandSenderWorld();
        if (level.isClientSide()) return;

        BlockPos origin = graph.getOrigin();

        //delete runes
        JsonObject runeConfig = new JsonObject();
        runeConfig.addProperty("shape", "rune_area");
        AABB runeBounds = this.createBoundingBoxAction.execute(player, graph, runeConfig).get(0);

        //check entities above runes
        List<ItemEntity> items = this.getEntitiesInAreaAction.execute(level, runeBounds, ItemEntity.class);

        boolean success = false;
        if (!items.isEmpty()) {
            for (ILocateRecipes recipe : LocateRecipeRegistry.RECIPES) {
                if (recipe.matches(items, level, origin)) {
                    recipe.performCraft(items, level, origin);
                    success = true;
                    break;
                }
            }
        }

        if (success) {
            handleSuccess(level, List.of(runeBounds));
        } else {
            handleFailure((ServerLevel) level, origin, items);
        }
    }

    private void handleSuccess(Level level, List<AABB> runeBounds) {
        this.clearAreaAction.execute(level, runeBounds);
    }

    private void handleFailure(ServerLevel level, BlockPos pos, List<ItemEntity> items) {
        this.playSoundAction.execute(level, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0f, 1.0f);

        this.spawnParticlesAction.execute(level, new AABB(pos), ParticleTypes.SMOKE, net.minecraft.world.phys.Vec3.ZERO, 20, 0.5);

        this.destroyRandomItemAction.execute(level, items, 0.25f);
    }

    public static void handleFailureGeneral(ServerLevel level, BlockPos pos) {
        PlaySoundAction playSoundAction = new PlaySoundAction();
        SpawnParticlesAction spawnParticlesAction = new SpawnParticlesAction();

        playSoundAction.execute(level, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0f, 1.0f);

        spawnParticlesAction.execute(level, new AABB(pos), ParticleTypes.SMOKE, net.minecraft.world.phys.Vec3.ZERO, 20, 0.5);
    }
}