/**
 * Copyright (c) 2025 QPCrummer
 * This project is Licensed under <a href="https://github.com/Tater-Certified/Potatoptimize/blob/main/LICENSE">MIT</a>
 */
package com.github.tatercertified.vanilla.mixin.nonvanilla.experimental.tick_skipping;

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
