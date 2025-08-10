package net.sphen.magicmodbuns.animations.item;

import net.minecraft.resources.ResourceLocation;
import net.sphen.magicmodbuns.MagicMod;
import net.sphen.magicmodbuns.item.custom.SpellBookItem;
import software.bernie.geckolib.model.GeoModel;

public class SpellBookModel extends GeoModel<SpellBookItem> {

    @Override
    public ResourceLocation getModelResource(SpellBookItem animatable) {
        return new ResourceLocation(MagicMod.MODID, "geo/spell_book.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(SpellBookItem animatable) {
        return new ResourceLocation(MagicMod.MODID, "textures/item/spell_book.png");
    }

    @Override
    public ResourceLocation getAnimationResource(SpellBookItem animatable) {
        return new ResourceLocation(MagicMod.MODID, "animations/spell_book.animation.json");
    }
}
