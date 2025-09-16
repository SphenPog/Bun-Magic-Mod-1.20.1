package net.sphen.magicmodbuns.util.Packets;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class ClientPacketHandler {
    public static void handlePlayerVelocity(Vec3 velocity) {
        Player player = Minecraft.getInstance().player;

        if (player != null) {
            player.addDeltaMovement(velocity);
        }
    }
}
