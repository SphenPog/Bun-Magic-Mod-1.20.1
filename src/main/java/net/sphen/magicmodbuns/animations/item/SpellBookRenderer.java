package net.sphen.magicmodbuns.animations.item;

import net.sphen.magicmodbuns.item.custom.SpellBookItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class SpellBookRenderer extends GeoItemRenderer<SpellBookItem> {
    public SpellBookRenderer() {
        super(new SpellBookModel());
    }
}
