package net.sphen.magicmodbuns.animations.block;

import net.minecraft.resources.ResourceLocation;
import net.sphen.magicmodbuns.MagicMod;
import net.sphen.magicmodbuns.block.entity.MortarAndPestleBlockEntity;
import software.bernie.geckolib.model.GeoModel;

public class MortarAndPestleModel extends GeoModel<MortarAndPestleBlockEntity> {
    @Override
    public ResourceLocation getModelResource(MortarAndPestleBlockEntity animatable) {
        return new ResourceLocation(MagicMod.MODID, "geo/mortar_and_pestle.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(MortarAndPestleBlockEntity animatable) {
        return new ResourceLocation(MagicMod.MODID, "textures/block/mortar_and_pestle.png");
    }

    @Override
    public ResourceLocation getAnimationResource(MortarAndPestleBlockEntity animatable) {
        return new ResourceLocation(MagicMod.MODID, "animations/mortar_and_pestle.animation.json");
    }
}
