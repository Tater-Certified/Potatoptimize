package com.github.tatercertified.potatoptimize.mixin.unstream.nearest_item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.NearestPlayersSensor;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Credit to PaperMC patch #0555
 */
@Mixin(NearestPlayersSensor.class)
public class NearestPlayersSensorMixin {
    /**
     * @author QPCrummer
     * @reason Remove Streams
     */
    @Overwrite
    public void sense(ServerWorld world, LivingEntity entity) {
        List<PlayerEntity> players = new ArrayList<>(world.getPlayers());
        players.removeIf(player -> !EntityPredicates.EXCEPT_SPECTATOR.test(player) || !entity.isInRange(player, 16.0D));
        players.sort(Comparator.comparingDouble(entity::distanceTo));
        Brain<?> brain = entity.getBrain();
        brain.remember(MemoryModuleType.NEAREST_PLAYERS, players);

        PlayerEntity nearest = null, nearestTargetable = null;
        for (PlayerEntity player : players) {
            if (Sensor.testTargetPredicate(world, entity, player)) {
                if (nearest == null) nearest = player;
                if (Sensor.testAttackableTargetPredicate(world, entity, player)) {
                    nearestTargetable = player;
                    break;
                }
            }
        }
        brain.remember(MemoryModuleType.NEAREST_VISIBLE_PLAYER, nearest);
        brain.remember(MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER, nearestTargetable);
    }
}
