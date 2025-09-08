package net.sphen.magicmodbuns.util.Packets;

import com.google.common.collect.Queues;

import java.util.Queue;

record PacketAttempt<T>(T packet, int retries) {}

public class ClientPacketQueue {

    private static final Queue<PacketAttempt<SyncChalkPatternPacket>> pendingChalkUpdates = Queues.newConcurrentLinkedQueue();
    private static final int maxRetries = 100;

    public static void queuePacket(SyncChalkPatternPacket packet) {
        pendingChalkUpdates.add(new PacketAttempt<>(packet, 0));
    }

    public static void processQueue() {
        if (pendingChalkUpdates.isEmpty()){
            return;
        }

        int size = pendingChalkUpdates.size();
        for (int i = 0; i < size; i++) {
            PacketAttempt<SyncChalkPatternPacket> attempt = pendingChalkUpdates.poll();
            if (attempt == null) continue;

            if (!SyncChalkPatternPacket.tryApplyPattern(attempt.packet())) {
                if (attempt.retries() < maxRetries) {
                    pendingChalkUpdates.add(new PacketAttempt<>(attempt.packet(), attempt.retries() + 1));
                } else {
                    System.out.println("Discrading chalk pattern packet for " + attempt.packet().getPos() + " after max retries.");
                }
            }
        }
    }

}
