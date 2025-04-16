package net.sphen.magicmodbuns.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.sphen.magicmodbuns.block.custom.ChalkPatternBlock;
import net.sphen.magicmodbuns.block.entity.ChalkPatternBlockEntity;


public class ChalkPatternBlockRenderer implements BlockEntityRenderer<ChalkPatternBlockEntity> {

    @Override
    public void render(ChalkPatternBlockEntity blockEntity, float partialTicks, PoseStack poseStack,
                       MultiBufferSource bufferSource, int light, int overlay) {
        Direction facing = blockEntity.getBlockState().getValue(ChalkPatternBlock.FACING);

        // Rotate based on block's facing
        float angle = switch (facing) {
            case NORTH -> 0f;
            case EAST  -> 90f;
            case SOUTH -> 180f;
            case WEST  -> 270f;
            default    -> 0f;
        };

        ResourceLocation texture = blockEntity.getTexturePath();
        if (texture == null) {
            return;
        }

        // Bind the texture
        Minecraft.getInstance().getTextureManager().bindForSetup(texture);

        VertexConsumer buffer = bufferSource.getBuffer(RenderType.entityCutout(texture));

        poseStack.pushPose();

        //translate so that the rotation is centered on the block
        poseStack.translate(0.5, 0.0001, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(angle));

        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityTranslucent(texture));
        PoseStack.Pose matrix = poseStack.last();

        //translate back so that the texture is applied correctly
        poseStack.translate(-0.5, 0, -0.5);

        vertexConsumer.vertex(matrix.pose(), 0, 0, 0).color(255, 255, 255, 255).uv(0, 0).overlayCoords(overlay).uv2(light).normal(0, 1, 0).endVertex();
        vertexConsumer.vertex(matrix.pose(), 1, 0, 0).color(255, 255, 255, 255).uv(1, 0).overlayCoords(overlay).uv2(light).normal(0, 1, 0).endVertex();
        vertexConsumer.vertex(matrix.pose(), 1, 0, 1).color(255, 255, 255, 255).uv(1, 1).overlayCoords(overlay).uv2(light).normal(0, 1, 0).endVertex();
        vertexConsumer.vertex(matrix.pose(), 0, 0, 1).color(255, 255, 255, 255).uv(0, 1).overlayCoords(overlay).uv2(light).normal(0, 1, 0).endVertex();

        poseStack.popPose();
    }

}
