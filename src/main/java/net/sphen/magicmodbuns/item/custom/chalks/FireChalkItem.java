package net.sphen.magicmodbuns.item.custom.chalks;

import net.sphen.magicmodbuns.block.ChalkType;

public class FireChalkItem extends ChalkItem{

    public FireChalkItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public ChalkType getChalkType() {
        return ChalkType.FIRE;
    }
}
