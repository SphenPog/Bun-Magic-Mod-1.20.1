package net.sphen.magicmodbuns.spells.runes;

import net.minecraft.core.BlockPos;
import net.sphen.magicmodbuns.block.ChalkType;
import net.sphen.magicmodbuns.block.entity.ChalkPatternBlockEntity;

public class RuneNode {
    public final BlockPos pos;
    public final ChalkType Chalkelement;
    public final ChalkPatternBlockEntity entity;
    public final RuneType runeType;

    public RuneNode(BlockPos pos, ChalkType chalkElement, ChalkPatternBlockEntity entity, RuneType runeType){
        this.pos = pos;
        this.Chalkelement = chalkElement;
        this.entity = entity;
        this.runeType = runeType;
    }
}
