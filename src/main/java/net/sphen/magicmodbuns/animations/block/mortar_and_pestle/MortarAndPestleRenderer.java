package net.sphen.magicmodbuns.animations.block.mortar_and_pestle;

import net.sphen.magicmodbuns.block.entity.MortarAndPestleBlockEntity;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class MortarAndPestleRenderer extends GeoBlockRenderer<MortarAndPestleBlockEntity> {
    public MortarAndPestleRenderer() {
        super(new MortarAndPestleModel());
    }
}
