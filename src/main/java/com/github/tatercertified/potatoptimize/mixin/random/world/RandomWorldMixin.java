package com.github.tatercertified.potatoptimize.mixin.random.world;

import com.github.tatercertified.potatoptimize.utils.random.ThreadLocalRandomImpl;
import com.moulberry.mixinconstraints.annotations.IfModAbsent;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@IfModAbsent("faster-random")
@Mixin(World.class)
public class RandomWorldMixin {
    @Shadow @Final @Mutable @Deprecated private Random threadSafeRandom = new ThreadLocalRandomImpl();
    @Shadow @Final @Mutable public Random random = new ThreadLocalRandomImpl();
    @Shadow @Mutable protected int lcgBlockSeed = random.nextInt();
}
