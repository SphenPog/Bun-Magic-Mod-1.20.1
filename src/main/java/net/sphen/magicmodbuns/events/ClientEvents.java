package net.sphen.magicmodbuns.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.sphen.magicmodbuns.MagicMod;
import net.sphen.magicmodbuns.item.custom.SpellBookItem;
import net.sphen.magicmodbuns.util.Packets.CycleSpellPacket;

public class ClientEvents {

    //checks mouse scroll for Spellbook page switching
    @SubscribeEvent
    public void onMouseScroll(InputEvent.MouseScrollingEvent event) {
        Player player = Minecraft.getInstance().player;

        if (player != null && Screen.hasShiftDown()) {
            ItemStack mainHandStack = player.getMainHandItem();
            if (mainHandStack.getItem() instanceof SpellBookItem){
                int scrollDirection = event.getScrollDelta() > 0 ? 1 : -1;

                MagicMod.NETWORK.sendToServer(new CycleSpellPacket(scrollDirection));

                event.setCanceled(true);
            }
        }
    }

}
