package net.sphen.magicmodbuns.item.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.sphen.magicmodbuns.MagicMod;
import net.sphen.magicmodbuns.block.ChalkType;
import net.sphen.magicmodbuns.screen.elements.PatternObject;
import net.sphen.magicmodbuns.util.Packets.PlaceChalkPatternPacket;

public class SpellPaperItem extends Item {
    private PatternObject pattern;
    private ChalkType chalkType;

    public SpellPaperItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        if (pContext.getItemInHand().hasTag()){
            CompoundTag paperData = pContext.getItemInHand().getTag();

            if (paperData != null) {
                String patternData = paperData.getString("pattern");
                this.pattern = PatternObject.loadData(patternData);

                int chalkTypeId = paperData.getInt("chalk_type");
                this.chalkType = ChalkType.getById(chalkTypeId);
            }
            BlockPos blockPos = pContext.getClickedPos();
            String textureFileName = "pattern_" + blockPos.getX() + "_" + (blockPos.getY() + 1) + "_" + blockPos.getZ();

            pContext.getItemInHand().shrink(1);

            MagicMod.NETWORK.sendToServer(new PlaceChalkPatternPacket(blockPos.above(), this.pattern.storeData(), textureFileName, this.chalkType));
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    public PatternObject getPattern() {
        return pattern;
    }

    public void setPattern(PatternObject pattern) {
        this.pattern = pattern;
    }

}
