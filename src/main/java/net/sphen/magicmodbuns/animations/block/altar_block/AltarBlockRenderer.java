package net.sphen.magicmodbuns.animations.block.altar_block;

import net.sphen.magicmodbuns.block.custom.AltarBlock;
import net.sphen.magicmodbuns.block.entity.AltarBlockEntity;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class AltarBlockRenderer extends GeoBlockRenderer<AltarBlockEntity> {
    public AltarBlockRenderer() {
        super(new AltarBlockModel());
    }
}
