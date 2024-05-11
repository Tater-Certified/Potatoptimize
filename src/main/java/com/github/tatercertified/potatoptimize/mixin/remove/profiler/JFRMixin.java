package com.github.tatercertified.potatoptimize.mixin.remove.profiler;

import net.minecraft.network.NetworkPhase;
import net.minecraft.network.packet.PacketType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.profiling.jfr.Finishable;
import net.minecraft.util.profiling.jfr.InstanceType;
import net.minecraft.util.profiling.jfr.JfrProfiler;
import net.minecraft.util.profiling.jfr.event.NetworkSummaryEvent;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.io.Reader;
import java.net.SocketAddress;
import java.nio.file.Path;

@Mixin(JfrProfiler.class)
public class JFRMixin {

    /**
     * @author QPCrummer
     * @reason Removal
     */
    @Overwrite
    public boolean start(InstanceType instanceType) {
        LoggerFactory.getLogger("Potatoptimize").warn("/jfr has been removed; Use spark or alternative instead!");
        return false;
    }

    /**
     * @author QPCrummer
     * @reason Removal
     */
    @Overwrite
    public Path stop() {
        return null;
    }

    /**
     * @author QPCrummer
     * @reason Removal
     */
    @Overwrite
    public boolean isProfiling() {
        return false;
    }

    /**
     * @author QPCrummer
     * @reason Removal
     */
    @Overwrite
    public boolean isAvailable() {
        return false;
    }

    /**
     * @author QPCrummer
     * @reason Removal
     */
    @Overwrite
    private boolean start(Reader reader, InstanceType instanceType) {
        return false;
    }

    /**
     * @author QPCrummer
     * @reason Removal
     */
    @Overwrite
    private void addListener() {
    }

    /**
     * @author QPCrummer
     * @reason Removal
     */
    @Overwrite
    public void onTick(float tickTime) {
    }

    /**
     * @author QPCrummer
     * @reason Removal
     */
    @Overwrite
    public void onPacketReceived(NetworkPhase state, PacketType<?> type, SocketAddress remoteAddress, int bytes) {
    }

    /**
     * @author QPCrummer
     * @reason Removal
     */
    @Overwrite
    public void onPacketSent(NetworkPhase state, PacketType<?> type, SocketAddress remoteAddress, int bytes) {
    }

    /**
     * @author QPCrummer
     * @reason Removal
     */
    @Overwrite
    private NetworkSummaryEvent.Recorder getOrCreateSummaryRecorder(SocketAddress address) {
        return null;
    }

    /**
     * @author QPCrummer
     * @reason Removal
     */
    @Overwrite
    public @Nullable Finishable startWorldLoadProfiling() {
        return null;
    }

    /**
     * @author QPCrummer
     * @reason Removal
     */
    @Overwrite
    public @Nullable Finishable startChunkGenerationProfiling(ChunkPos chunkPos, RegistryKey<World> world, String targetStatus) {
        return null;
    }
}
