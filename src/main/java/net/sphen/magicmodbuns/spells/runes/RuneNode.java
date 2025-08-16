package net.sphen.magicmodbuns.spells.runes;

import net.minecraft.core.BlockPos;

import java.lang.annotation.ElementType;

public class RuneNode {
    public final BlockPos pos;
    public final RuneType runeType;
    public final ElementType element;

    public RuneNode(BlockPos pos, RuneType runeType, ElementType element){
        this.pos = pos;
        this.runeType = runeType;
        this.element = element;
    }
}
