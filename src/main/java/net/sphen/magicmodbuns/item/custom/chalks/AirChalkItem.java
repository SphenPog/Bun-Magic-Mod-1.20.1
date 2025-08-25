package net.sphen.magicmodbuns.item.custom.chalks;

import net.sphen.magicmodbuns.block.ChalkType;

public class AirChalkItem extends ChalkItem{

    public AirChalkItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public ChalkType getChalkType() {
        return ChalkType.AIR;
    }
}
