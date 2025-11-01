package com.github.tatercertified.vanilla.mixin.experimental.tick_skipping;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntityTickList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityTickList.class)
public interface EntityTickingListAccessor {
    @Accessor("active")
    Int2ObjectMap<Entity> getActive();
}
