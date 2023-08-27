package com.github.tatercertified.potatoptimize.mixin.random.entity;

import com.github.tatercertified.potatoptimize.utils.random.ThreadLocalRandomImpl;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Entity.class)
public abstract class RandomEntityMixin {
    @Shadow @Mutable @Final protected Random random = new ThreadLocalRandomImpl();

}
