package net.sphen.magicmodbuns.spells.runes;

import net.minecraft.core.BlockPos;
import net.sphen.magicmodbuns.block.entity.ChalkPatternBlockEntity;

import java.lang.annotation.ElementType;

public class RuneNode {
    public final BlockPos pos;
    public final ElementType element;
    public final ChalkPatternBlockEntity entity;
    public final RuneType runeType;

    public RuneNode(BlockPos pos, ElementType element, ChalkPatternBlockEntity entity, RuneType runeType){
        this.pos = pos;
        this.element = element;
        this.entity = entity;
        this.runeType = runeType;
    }
}
