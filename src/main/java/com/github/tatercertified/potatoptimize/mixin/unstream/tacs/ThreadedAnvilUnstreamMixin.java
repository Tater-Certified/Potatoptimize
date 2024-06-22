package com.github.tatercertified.potatoptimize.mixin.unstream.tacs;

import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerChunkLoadingManager;
import org.spongepowered.asm.mixin.*;

import java.util.List;

@Mixin(ServerChunkLoadingManager.EntityTracker.class)
public abstract class ThreadedAnvilUnstreamMixin {
    @Shadow protected abstract int adjustTrackingDistance(int initialDistance);

    @Shadow @Final Entity entity;

    @Shadow @Final private int maxDistance;

    @Unique
    private int getHighestRange(Entity parent, int highest) {
        List<Entity> passengers = parent.getPassengerList();

        for (Entity entity : passengers) {
            int range = entity.getType().getMaxTrackDistance() * 16;

            if (range > highest) {
                highest = range;
            }

            highest = getHighestRange(entity, highest);
        }

        return highest;
    }

    /**
     * @author QPCrummer
     * @reason Remove Stream API
     */
    @Overwrite
    private int getMaxTrackDistance() {
        return this.adjustTrackingDistance(this.getHighestRange(this.entity, this.maxDistance));
    }
}
