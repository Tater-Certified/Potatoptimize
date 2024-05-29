package com.github.tatercertified.potatoptimize.mixin.random.generators;

import com.github.tatercertified.potatoptimize.utils.random.ThreadLocalRandomImpl;
import com.moulberry.mixinconstraints.annotations.IfModAbsent;
import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.math.random.RandomSeed;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@IfModAbsent("faster-random")
@Mixin(Random.class)
public interface RandomMixin {
    /**
     * @author QPCrummer
     * @reason Use my implementation of ThreadLocalRandom
     */
    @Deprecated
    @Overwrite
    static Random createThreadSafe() {
        return new ThreadLocalRandomImpl(RandomSeed.getSeed());
    }

    /**
     * @author QPCrummer
     * @reason Use my implementation of ThreadLocalRandom
     */
    @Overwrite
    static Random createLocal() {
        return new ThreadLocalRandomImpl(ThreadLocalRandom.current().nextLong());
    }

    /**
     * @author QPCrummer
     * @reason Use my implementation of ThreadLocalRandom
     *
     * This has been removed for now as it causes issues with chunk rebuilding!
     *
    @Overwrite
    static Random create(long seed) {
        return new ThreadLocalRandomImpl(seed);
    }
     */
}
