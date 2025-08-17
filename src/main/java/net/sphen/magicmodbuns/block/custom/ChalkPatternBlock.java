package net.sphen.magicmodbuns.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.sphen.magicmodbuns.block.entity.ChalkPatternBlockEntity;
import net.sphen.magicmodbuns.screen.elements.PatternObject;
import net.sphen.magicmodbuns.spells.runes.*;

public class ChalkPatternBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public ChalkPatternBlock(Properties pProperties) {
        super(pProperties);
    }

    //override use function to allow players to rotate the chalk pattern once placed.
    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        boolean sneaking = pPlayer.isShiftKeyDown();

        //gets new rotation direction
        if(!pLevel.isClientSide && !sneaking){
            Direction currentDirection = pState.getValue(FACING);
            Direction newDirection = currentDirection.getClockWise();

            pLevel.setBlockAndUpdate(pPos, pState.setValue(FACING, newDirection));
        } else if (!pLevel.isClientSide && sneaking) {
            int radius = 8;
            boolean diagonals = true;
            RunePatternGraph graph = RuneSearch.findPattern(pLevel, pPos, radius, diagonals);

            System.out.println(graph);
            System.out.println(graph.getNodes());

            //remove (debug)
            if (pLevel.getBlockEntity(pPos) instanceof ChalkPatternBlockEntity be) {
                PatternObject pattern = be.getPattern();

                System.out.println(pattern.getSortedLines());
                RuneType type = RuneRegistry.detectRune(pattern);
                System.out.println("RUNE TYPE : " + type.name());
                System.out.println("RUNE SIGS : " + RuneRegistry.getAllSignatures());
            }
        }



        return InteractionResult.sidedSuccess(pLevel.isClientSide());
    }

    //override onRemove function to delete the associated texture when pattern is broken.
    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {

        if (!pState.is(pNewState.getBlock())) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof ChalkPatternBlockEntity chalkEntity) {
                chalkEntity.deleteAssociatedTexture();
            }
        }

        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return Block.box(0, 0, 0, 16, 1, 16);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        super.animateTick(pState, pLevel, pPos, pRandom);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new ChalkPatternBlockEntity(pPos, pState);
    }
}
