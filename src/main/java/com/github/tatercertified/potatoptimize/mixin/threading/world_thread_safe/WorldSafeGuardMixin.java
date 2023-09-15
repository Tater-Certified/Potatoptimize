package com.github.tatercertified.potatoptimize.mixin.threading.world_thread_safe;

import com.github.tatercertified.potatoptimize.interfaces.WorldInterface;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(World.class)
public abstract class WorldSafeGuardMixin implements WorldInterface, WorldAccess {

}
