package net.sphen.magicmodbuns.events;

import net.minecraft.client.Minecraft;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.sphen.magicmodbuns.util.Packets.ClientPacketQueue;

public class ClientTickHandler {

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (Minecraft.getInstance().level != null) {
                ClientPacketQueue.processQueue();
            }
        }
    }

}
