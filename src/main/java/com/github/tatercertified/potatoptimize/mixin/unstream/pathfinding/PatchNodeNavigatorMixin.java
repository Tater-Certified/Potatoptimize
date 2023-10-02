package com.github.tatercertified.potatoptimize.mixin.unstream.pathfinding;

import com.google.common.collect.Lists;
import net.minecraft.entity.ai.pathing.*;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.SampleType;
import net.minecraft.world.chunk.ChunkCache;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Credit PaperMC patch #0485
 */
@Mixin(PathNodeNavigator.class)
public abstract class PatchNodeNavigatorMixin {
    @Shadow
    @Final
    private PathMinHeap minHeap;

    @Shadow
    @Final
    private PathNodeMaker pathNodeMaker;

    @Shadow
    @Final
    private int range;

    @Shadow
    @Final
    private PathNode[] successors;

    @Shadow
    protected abstract float getDistance(PathNode a, PathNode b);

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
            List<Map.Entry<TargetPathNode, BlockPos>> map = Lists.newArrayList();
            for (BlockPos pos : positions) {
                map.add(new java.util.AbstractMap.SimpleEntry<>(this.pathNodeMaker.getNode(pos.getX(), pos.getY(), pos.getZ()), pos));
            }
            Path path = this.findPathToAny(world.getProfiler(), pathNode, map, followRange, distance, rangeMultiplier);
            this.pathNodeMaker.clear();
            return path;
        }
    }

    @Unique
    private Path findPathToAny(Profiler profiler, PathNode startNode, List<Map.Entry<TargetPathNode, BlockPos>> positions, float followRange, int distance, float rangeMultiplier) {
        profiler.push("find_path");
        profiler.markSampleType(SampleType.PATH_FINDING);
        startNode.penalizedPathLength = 0.0F;
        startNode.distanceToNearestTarget = this.calculateDistances(startNode, positions);
        startNode.heapWeight = startNode.distanceToNearestTarget;
        this.minHeap.clear();
        this.minHeap.push(startNode);
        int i = 0;
        List<Map.Entry<TargetPathNode, BlockPos>> entryList = Lists.newArrayListWithExpectedSize(positions.size());
        int j = (int) ((float) this.range * rangeMultiplier);

        while (!this.minHeap.isEmpty()) {
            ++i;
            if (i >= j) {
                break;
            }

            PathNode pathNode = this.minHeap.pop();
            pathNode.visited = true;

            for (final Map.Entry<TargetPathNode, BlockPos> entry : positions) {
                TargetPathNode target = entry.getKey();
                if (pathNode.getManhattanDistance(target) <= (float) distance) {
                    target.markReached();
                    entryList.add(entry);
                }
            }

            if (!entryList.isEmpty()) {
                break;
            }

            if (!(pathNode.getDistance(startNode) >= followRange)) {
                int k = this.pathNodeMaker.getSuccessors(this.successors, pathNode);

                for (int l = 0; l < k; ++l) {
                    PathNode pathNode2 = this.successors[l];
                    float f = this.getDistance(pathNode, pathNode2);
                    pathNode2.pathLength = pathNode.pathLength + f;
                    float g = pathNode.penalizedPathLength + f + pathNode2.penalty;
                    if (pathNode2.pathLength < followRange && (!pathNode2.isInHeap() || g < pathNode2.penalizedPathLength)) {
                        pathNode2.previous = pathNode;
                        pathNode2.penalizedPathLength = g;
                        pathNode2.distanceToNearestTarget = this.calculateDistances(pathNode2, positions) * 1.5F;
                        if (pathNode2.isInHeap()) {
                            this.minHeap.setNodeWeight(pathNode2, pathNode2.penalizedPathLength + pathNode2.distanceToNearestTarget);
                        } else {
                            pathNode2.heapWeight = pathNode2.penalizedPathLength + pathNode2.distanceToNearestTarget;
                            this.minHeap.push(pathNode2);
                        }
                    }
                }
            }
        }

        Path best = null;
        boolean entryListIsEmpty = entryList.isEmpty();
        Comparator<Path> comparator = entryListIsEmpty ? Comparator.comparingInt(Path::getLength)
                : Comparator.comparingDouble(Path::getManhattanDistanceFromTarget).thenComparingInt(Path::getLength);
        for (Map.Entry<TargetPathNode, BlockPos> entry : entryListIsEmpty ? positions : entryList) {
            Path path = this.createPath(entry.getKey().getNearestNode(), entry.getValue(), !entryListIsEmpty);
            if (best == null || comparator.compare(path, best) < 0)
                best = path;
        }
        return best;
    }

    @Unique
    private float calculateDistances(PathNode node, List<Map.Entry<TargetPathNode, BlockPos>> targets) {
        float f = Float.MAX_VALUE;

        float g;
        for (Map.Entry<TargetPathNode, BlockPos> targetPathNodeBlockPosEntry : targets) {
            final TargetPathNode target = targetPathNodeBlockPosEntry.getKey();
            g = node.getDistance(target);
            target.updateNearestNode(g, node);
        }

        return f;
    }
}
