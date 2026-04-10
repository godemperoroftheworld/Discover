package com.t2pellet.discover.client.util;

import dev.architectury.event.events.client.ClientTickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.server.TickTask;

import java.util.Comparator;
import java.util.PriorityQueue;

public class ClientScheduler implements ClientTickEvent<Minecraft> {

    private final PriorityQueue<TickTask> queue = new PriorityQueue<TickTask>(Comparator.comparingInt(TickTask::getTick));
    protected int currentTick = 0;

    protected ClientScheduler() {
    }

    @Override
    public void tick(Minecraft instance) {
        TickTask top = queue.peek();
        if (top != null && top.getTick() <= currentTick) {
            TickTask next = queue.poll();
            instance.execute(next);
        }
        ++currentTick;
    }

    public void runInTicks(int ticks, Runnable runnable) {
        TickTask task = new TickTask(ticks + currentTick, runnable);
        queue.add(task);
    }
}
