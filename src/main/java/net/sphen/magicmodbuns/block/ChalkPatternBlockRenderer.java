package net.sphen.magicmodbuns.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.sphen.magicmodbuns.block.entity.ChalkPatternBlockEntity;


public class ChalkPatternBlockRenderer implements BlockEntityRenderer<ChalkPatternBlockEntity> {

    @Override
    public void render(ChalkPatternBlockEntity blockEntity, float partialTicks, PoseStack poseStack,
                       MultiBufferSource bufferSource, int light, int overlay) {
        ResourceLocation texture = blockEntity.getTexturePath();
        if (texture == null) {
            return;
        }

        // Bind the texture
        Minecraft.getInstance().getTextureManager().bindForSetup(texture);

        VertexConsumer buffer = bufferSource.getBuffer(RenderType.entityCutout(texture));

        poseStack.pushPose();

        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityTranslucent(texture));
        PoseStack.Pose matrix = poseStack.last();

        poseStack.translate(0, 0.0001, 0);

        vertexConsumer.vertex(matrix.pose(), 0, 0, 0).color(255, 255, 255, 255).uv(0, 0).overlayCoords(overlay).uv2(light).normal(0, 1, 0).endVertex();
        vertexConsumer.vertex(matrix.pose(), 1, 0, 0).color(255, 255, 255, 255).uv(1, 0).overlayCoords(overlay).uv2(light).normal(0, 1, 0).endVertex();
        vertexConsumer.vertex(matrix.pose(), 1, 0, 1).color(255, 255, 255, 255).uv(1, 1).overlayCoords(overlay).uv2(light).normal(0, 1, 0).endVertex();
        vertexConsumer.vertex(matrix.pose(), 0, 0, 1).color(255, 255, 255, 255).uv(0, 1).overlayCoords(overlay).uv2(light).normal(0, 1, 0).endVertex();

        poseStack.popPose();
    }

}
