package com.github.tatercertified.potatoptimize.mixin.random.unsafe;

import com.github.tatercertified.potatoptimize.Potatoptimize;
import com.github.tatercertified.potatoptimize.utils.UnsafeGrabber;
import com.github.tatercertified.potatoptimize.utils.random.ThreadLocalRandomImpl;
import com.moulberry.mixinconstraints.annotations.IfModAbsent;
import net.minecraft.util.math.random.RandomSplitter;
import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

@IfModAbsent("faster-random")
@Mixin(ThreadLocalRandomImpl.class)
public class ForceEnableUnsafeMixin {

    static {
        LogManager.getLogger("Potatoptimize").warn("UNSAFE ThreadLocalRandom has been enabled!!!! This can potentially cause serious issues, crashes, and/or severely hurt performance!!!");
        Potatoptimize.isUnsafeRandomEnabled = true;
    }

    @Unique
    private long seed;
    /**
     * @author QPCrummer
     * @reason Enable the cursed Unsafe!
     */
    @Overwrite
    public void setSeed(long seed) {
        this.seed = cursedSetSeed(seed);
    }

    // Unsafe
    @Unique
    private static final Unsafe U = UnsafeGrabber.UNSAFE;
    @Unique
    private static final long SEED = U.objectFieldOffset(findThreadLocalRandomSeedField());

    @Unique
    private static Field findThreadLocalRandomSeedField() {
        try {
            return Thread.class.getDeclaredField("threadLocalRandomSeed");
        } catch (NoSuchFieldException e) {
            throw new AssertionError(e);
        }
    }

    @Unique
    private long cursedSetSeed(long seed) {
        long r;
        U.putLong(Thread.currentThread(), SEED, r = seed);
        return r;
    }

    /**
     * @author QPCrummer
     * @reason Enable the cursed Unsafe!
     */
    @Overwrite
    public RandomSplitter nextSplitter() {
        return new ThreadLocalRandomImpl.Splitter(this.seed, (ThreadLocalRandomImpl)(Object)this);
    }
}
