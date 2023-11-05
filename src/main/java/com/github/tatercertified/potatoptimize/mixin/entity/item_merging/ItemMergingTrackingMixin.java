package com.github.tatercertified.potatoptimize.mixin.entity.item_merging;

import com.github.tatercertified.potatoptimize.utils.interfaces.MergeCacheInterface;
import com.github.tatercertified.potatoptimize.utils.interfaces.MergeableItem;
import com.google.common.collect.Lists;
import net.minecraft.entity.ItemEntity;
import net.minecraft.world.entity.EntityLike;
import net.minecraft.world.entity.EntityTrackingSection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

/**
 * Credit to Andrews54757 (unmerged lithium PR)
 */
@Mixin(EntityTrackingSection.class)
public abstract class ItemMergingTrackingMixin<T extends EntityLike> implements MergeCacheInterface {

    private final List<ItemEntity> mergableItemEntities = Lists.newArrayList();

    private final List<ItemEntity> mergableItemEntitiesHalfEmpty = Lists.newArrayList();

    @Inject(method = "add(Lnet/minecraft/world/entity/EntityLike;)V", at = @At("RETURN"))
    private void onEntityAdded(T entityLike, CallbackInfo ci) {
        if (entityLike instanceof ItemEntity entity) {
            MergeableItem mergableItem = (MergeableItem) entity;
            if (mergableItem.canEntityMerge()) {
                mergableItemEntities.add(entity);

                if (mergableItem.isMoreEmpty()) {
                    mergableItemEntitiesHalfEmpty.add(entity);
                    mergableItem.setCachedState(MergeableItem.CACHED_HALFEMPTY);
                } else {
                    mergableItem.setCachedState(MergeableItem.CACHED_MOREFULL);
                }
            }
        }
    }

    @Inject(method = "remove(Lnet/minecraft/world/entity/EntityLike;)Z", at = @At("RETURN"))
    private void onEntityRemoved(T entityLike, CallbackInfoReturnable<Boolean> cir) {
        if (entityLike instanceof ItemEntity entity) {
            MergeableItem mergableItem = (MergeableItem) entity;
            byte cachedState = mergableItem.getCachedState();

            if (cachedState == MergeableItem.CACHED_HALFEMPTY) {
                mergableItemEntitiesHalfEmpty.remove(entity);
                mergableItemEntities.remove(entity);
            } else if (cachedState == MergeableItem.CACHED_MOREFULL) {
                mergableItemEntities.remove(entity);
            }

            mergableItem.setCachedState(MergeableItem.UNCACHED);
        }
    }

    @Override
    public void forEachMergables(MergeableItem item, Consumer<ItemEntity> consumer) {
        boolean canMergeItself = item.canMergeItself();
        List<ItemEntity> list = canMergeItself ? mergableItemEntities : mergableItemEntitiesHalfEmpty;
        Iterator<ItemEntity> iterator = list.iterator();

        while (iterator.hasNext()) {
            ItemEntity entity = iterator.next();
            MergeableItem mergableItem = (MergeableItem) entity;

            if (!mergableItem.canEntityMerge()) {
                iterator.remove();

                if (!canMergeItself) {
                    mergableItemEntities.remove(entity);
                } else if (mergableItem.getCachedState() == MergeableItem.CACHED_HALFEMPTY) {
                    mergableItemEntitiesHalfEmpty.remove(entity);
                }

                mergableItem.setCachedState(MergeableItem.UNCACHED);
                continue;
            }

            if (mergableItem.getCachedState() == MergeableItem.CACHED_HALFEMPTY && !mergableItem.isMoreEmpty()) {
                if (canMergeItself) {
                    mergableItemEntitiesHalfEmpty.remove(entity);
                } else {
                    iterator.remove();
                    continue;
                }

                mergableItem.setCachedState(MergeableItem.CACHED_MOREFULL);
            }

            if (item == mergableItem) {
                continue;
            }

            consumer.accept(entity);
        }
    }
}
