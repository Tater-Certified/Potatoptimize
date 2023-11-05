package com.github.tatercertified.potatoptimize.utils.interfaces;

import net.minecraft.entity.ItemEntity;

import java.util.function.Consumer;

public interface MergeCacheInterface {
    void forEachMergables(MergeableItem item, Consumer<ItemEntity> consumer);
}
