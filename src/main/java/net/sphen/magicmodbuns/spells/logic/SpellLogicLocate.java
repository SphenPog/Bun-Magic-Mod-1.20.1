package net.sphen.magicmodbuns.spells.logic;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.sphen.magicmodbuns.spells.SpellInstance;
import net.sphen.magicmodbuns.spells.SpellLogic;
import net.sphen.magicmodbuns.spells.logic.recipes.ILocateRecipes;
import net.sphen.magicmodbuns.spells.logic.recipes.LocateRecipeRegistry;
import net.sphen.magicmodbuns.spells.runes.RunePatternGraph;

import java.util.List;

public class SpellLogicLocate extends SpellLogic {

    @Override
    public void cast(SpellInstance instance, Player player, RunePatternGraph graph) {
        Level level = player.getCommandSenderWorld();
        BlockPos pos = graph.getOrigin();
        AABB bounds = graph.getBounds();

        checkForRecipe(level, pos, bounds);
    }

    private static void checkForRecipe(Level level, BlockPos pos, AABB bounds) {
        if (!(level instanceof ServerLevel serverLevel)) return;

        List<ItemEntity> itemsInRecipe = level.getEntitiesOfClass(ItemEntity.class, bounds);

        if (itemsInRecipe.isEmpty()) {
            //empty failure result
            handleFailureGeneral(serverLevel, pos);
            return;
        }

        for (ILocateRecipes recipe : LocateRecipeRegistry.RECIPES) {
            //check if items match any recipe
            if (recipe.matches(itemsInRecipe, level, pos)) {
                recipe.performCraft(itemsInRecipe, level, pos);
                handleSuccessRunes((ServerLevel) level, bounds);
                return;
            }
        }

        handleCraftingFailure(itemsInRecipe, (ServerLevel) level, pos);
    }

    //removes all runes in the bounds
    private static void handleSuccessRunes(ServerLevel level, AABB bounds) {
        BlockPos min = BlockPos.containing(bounds.minX, bounds.minY, bounds.minZ);
        BlockPos max = BlockPos.containing(bounds.maxX - 1, bounds.maxY - 1, bounds.maxZ - 1);

        for (BlockPos pos : BlockPos.betweenClosed(min, max)){
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        }
    }

    public static void handleFailureGeneral(ServerLevel level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0f, 1.0f);
        level.sendParticles(ParticleTypes.SMOKE, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 20, 0.2, 0.2, 0.2, 0.0);
    }

    private static void handleCraftingFailure(List<ItemEntity> items, ServerLevel level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0f, 1.0f);
        level.sendParticles(ParticleTypes.SMOKE, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 20, 0.2, 0.2, 0.2, 0.0);

        if (level.random.nextFloat() < 0.25f) {
            if (!items.isEmpty()) {
                items.get(level.random.nextInt(items.size())).discard();
            }
        }
    }

}