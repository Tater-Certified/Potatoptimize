package com.github.tatercertified.potatoptimize.mixin.unstream.nearest_item;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.NearestItemsSensor;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Mixin(NearestItemsSensor.class)
public class NearestItemSensorMixin {

    /**
     * @author QPCrummer
     * @reason Remove Streams
     */
    @Overwrite
    public void sense(ServerWorld serverWorld, MobEntity mobEntity) {
        Brain<?> brain = mobEntity.getBrain();
        List<ItemEntity> list = serverWorld.getEntitiesByClass(ItemEntity.class, mobEntity.getBoundingBox().expand(32.0, 16.0, 32.0), (itemEntity) -> true);
        Objects.requireNonNull(mobEntity);
        list.sort(Comparator.comparingDouble(mobEntity::squaredDistanceTo));
        ItemEntity nearest = null;
        for (ItemEntity item : list) {
            if (mobEntity.canPickupItem(item.getStack()) && item.isInRange(mobEntity, 32.0D) && mobEntity.canSee(item)) {
                nearest = item;
                break;
            }
        }
        brain.remember(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, Optional.ofNullable(nearest));
    }
}
