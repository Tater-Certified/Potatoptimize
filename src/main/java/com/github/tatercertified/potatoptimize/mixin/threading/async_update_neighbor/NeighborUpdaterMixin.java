package com.github.tatercertified.potatoptimize.mixin.threading.async_update_neighbor;

import com.github.tatercertified.potatoptimize.Potatoptimize;
import net.minecraft.world.World;
import net.minecraft.world.block.ChainRestrictedNeighborUpdater;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayDeque;
import java.util.List;

@Mixin(ChainRestrictedNeighborUpdater.class)
public abstract class NeighborUpdaterMixin {

    @Shadow @Final private ArrayDeque<ChainRestrictedNeighborUpdater.Entry> queue;

    @Shadow @Final private List<ChainRestrictedNeighborUpdater.Entry> pending;

    @Shadow @Final private World world;

    @Shadow private int depth;

    /**
     * @author QPCrummer
     * @reason Make it async!
     */
    @Overwrite
    private void runQueuedUpdates() {
        Potatoptimize.almightyServerInstance.runTasks(() -> {
            try {
                block3: while (!this.queue.isEmpty() || !this.pending.isEmpty()) {
                    for (int i = this.pending.size() - 1; i >= 0; --i) {
                        this.queue.push(this.pending.get(i));
                    }
                    this.pending.clear();
                    ChainRestrictedNeighborUpdater.Entry entry = this.queue.peek();
                    while (this.pending.isEmpty()) {
                        if (entry.update(this.world)) continue;
                        this.queue.pop();
                        continue block3;
                    }
                }
            } finally {
                this.queue.clear();
                this.pending.clear();
                this.depth = 0;
            }
            return true;
        });
    }
}
