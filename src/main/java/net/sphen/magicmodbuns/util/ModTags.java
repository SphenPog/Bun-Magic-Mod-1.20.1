package net.sphen.magicmodbuns.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.sphen.magicmodbuns.MagicMod;

public class ModTags {

    public static class Blocks {
        public static final TagKey<Block> METAL_DETECTOR_VALUABLES = tag("metal_detector_valuables");

        //block tags here ^

        private static TagKey<Block> tag(String name){
            return BlockTags.create(new ResourceLocation(MagicMod.MODID, name));
        }
    }

    public static class Items {

        public static final TagKey<Item> SPELL_BOOK_PAPER_TAG = tag("spell_book_paper_tag");
        public static final TagKey<Item> CHALK_TYPE_TAG = tag("chalk_type_tag");
        public static final TagKey<Item> LOCATE_MINERALS = tag("locate_minerals_tag");
        //item tags here ^

        private static TagKey<Item> tag(String name){
            return ItemTags.create(new ResourceLocation(MagicMod.MODID, name));
        }
    }
}
