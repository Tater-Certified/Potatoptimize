package com.github.tatercertified.potatoptimize.mixin.unstream.pathfinding;

import com.llamalad7.mixinextras.sugar.Local;
import com.moulberry.mixinconstraints.annotations.IfModAbsent;
import net.minecraft.entity.ai.pathing.*;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.chunk.ChunkCache;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

/**
 * Credit PaperMC patch #0485
 */
@IfModAbsent(value = "servercore")
@Mixin(PathNodeNavigator.class)
public abstract class PatchNodeNavigatorMixin {
    @Shadow
    @Final
    private PathMinHeap minHeap;

    @Shadow
    @Final
    private PathNodeMaker pathNodeMaker;

    @Shadow @Nullable protected abstract Path findPathToAny(Profiler profiler, PathNode startNode, Map<TargetPathNode, BlockPos> positions, float followRange, int distance, float rangeMultiplier);

    @Shadow protected abstract Path createPath(PathNode endNode, BlockPos target, boolean reachesTarget);

    /**
     * @author QPCrummer
     * @reason Remove Streams
     */
    @Overwrite
    public @Nullable Path findPathToAny(ChunkCache world, MobEntity mob, Set<BlockPos> positions, float followRange, int distance, float rangeMultiplier) {
        this.minHeap.clear();
        this.pathNodeMaker.init(world, mob);
        PathNode pathNode = this.pathNodeMaker.getStart();
        if (pathNode == null) {
            return null;
        } else {
            Map<TargetPathNode, BlockPos> map = new HashMap<>();
            for (BlockPos pos : positions) {
                TargetPathNode node = this.pathNodeMaker.getNode(pos.getX(), pos.getY(), pos.getZ());
                map.put(node, pos);
            }
            Path path = this.findPathToAny(world.getProfiler(), pathNode, map, followRange, distance, rangeMultiplier);
            this.pathNodeMaker.clear();
            return path;
        }
    }

    @Inject(method = "findPathToAny(Lnet/minecraft/util/profiler/Profiler;Lnet/minecraft/entity/ai/pathing/PathNode;Ljava/util/Map;FIF)Lnet/minecraft/entity/ai/pathing/Path;", at = @At(value = "INVOKE", target = "Ljava/util/Set;isEmpty()Z", ordinal = 1, shift = At.Shift.BEFORE), cancellable = true)
    private void removeStreamAPI(Profiler profiler, PathNode startNode, Map<TargetPathNode, BlockPos> positions, float followRange, int distance, float rangeMultiplier, CallbackInfoReturnable<Path> cir, @Local(ordinal = 0) Set<TargetPathNode> set3, @Local(ordinal = 0) Set<TargetPathNode> set) {
        Optional<Path> optional;
        Path shortestPath = null;
        if (!set3.isEmpty()) {
            int minLength = Integer.MAX_VALUE;
            for (TargetPathNode node : set3) {
                Path path = this.createPath(node.getNearestNode(), positions.get(node), true);
                if (path.getLength() < minLength) {
                    shortestPath = path;
                    minLength = path.getLength();
                }
            }
        } else {
            double minDistance = Double.MAX_VALUE;
            int minLength = Integer.MAX_VALUE;
            for (TargetPathNode targetPathNode : set) {
                Path path = this.createPath(targetPathNode.getNearestNode(), positions.get(targetPathNode), false);
                if (path.getManhattanDistanceFromTarget() < minDistance || (path.getManhattanDistanceFromTarget() == minDistance && path.getLength() < minLength)) {
                    shortestPath = path;
                    minDistance = path.getManhattanDistanceFromTarget();
                    minLength = path.getLength();
                }
            }
        }
        optional = Optional.ofNullable(shortestPath);
        profiler.pop();

        cir.setReturnValue(optional.orElse(null));
    }
}
