package com.github.tatercertified.potatoptimize.mixin.entity.villager_task;

import com.github.tatercertified.potatoptimize.utils.ai.WorkloadDistributor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.Memory;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * From Leaf patch #0044
 */
@Mixin(Brain.class)
public abstract class VillagerTasksMixin<E extends LivingEntity> {
    @Shadow @Final private Map<MemoryModuleType<?>, Optional<? extends Memory<?>>> memories;

    @Shadow public abstract <U> void forget(MemoryModuleType<U> type);

    @Shadow @Final private Map<Integer, Map<Activity, Set<Task<? super E>>>> tasks;
    @Shadow @Final private Set<Activity> possibleActivities;

    @Shadow @Deprecated public abstract List<Task<? super E>> getRunningTasks();

    private final WorkloadDistributor workloadDistribution = new WorkloadDistributor(20000L, 500L, 10000L);

    @Inject(method = "tick", at = @At("TAIL"))
    private void injectTick(ServerWorld world, E entity, CallbackInfo ci) {
        workloadDistribution.tick();
    }

    @Redirect(method = "tickSensors", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/brain/sensor/Sensor;tick(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/LivingEntity;)V"))
    private void redirectSenorTicking(Sensor instance, ServerWorld world, E entity) {
        workloadDistribution.addTask(() -> instance.tick(world, entity));
    }

    /**
     * @author QPCrummer & Leaf
     * @reason Add to workload distributor
     */
    @Overwrite
    private void tickMemories() {
        for(Map.Entry<MemoryModuleType<?>, Optional<? extends Memory<?>>> entry : this.memories.entrySet()) {
            workloadDistribution.addTask(() -> {
                if (entry.getValue().isPresent()) {
                    Memory<?> memoryValue = entry.getValue().get();
                    if (memoryValue.isExpired()) {
                        this.forget(entry.getKey());
                    }
                    memoryValue.tick();
                }
            });
        }
    }

    /**
     * @author QPCrummer & Leaf
     * @reason Add to workload distributor
     */
    @Overwrite
    private void startTasks(ServerWorld world, E entity) {
        long l = world.getTime();
        for(Map<Activity, Set<Task<? super E>>> map : this.tasks.values()) {
            for (Map.Entry<Activity, Set<Task<? super E>>> entry : map.entrySet()) {
                workloadDistribution.addTask(() -> {
                    Activity activity = entry.getKey();
                    if (this.possibleActivities.contains(activity)) {
                        for (Task<? super E> task : entry.getValue()) {
                            if (task.getStatus() == MultiTickTask.Status.STOPPED) {
                                task.tryStarting(world, entity, l);
                            }
                        }
                    }
                });
            }
        }
    }

    /**
     * @author QPCrummer & Leaf
     * @reason Add to workload distributor
     */
    @Overwrite
    private void updateTasks(ServerWorld world, E entity) {
        long l = world.getTime();
        for(Task<? super E> task : this.getRunningTasks()) {
            workloadDistribution.addTask(() -> task.tick(world, entity, l));
        }
    }
}
