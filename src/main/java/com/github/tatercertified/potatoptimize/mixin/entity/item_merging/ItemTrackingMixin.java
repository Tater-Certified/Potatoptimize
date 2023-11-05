package com.github.tatercertified.potatoptimize.mixin.entity.item_merging;

import com.github.tatercertified.potatoptimize.utils.interfaces.MergeCacheInterface;
import com.github.tatercertified.potatoptimize.utils.interfaces.MergeableItem;
import com.google.common.collect.Lists;
import me.jellysquid.mods.lithium.common.world.WorldHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.function.LazyIterationConsumer;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.minecraft.world.entity.SectionedEntityCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.function.Predicate;

@Mixin(ItemEntity.class)
public abstract class ItemTrackingMixin implements MergeableItem {

    private byte cachedState = UNCACHED;

    @Shadow
    protected abstract boolean canMerge();

    @Shadow
    public abstract ItemStack getStack();

    @Override
    public boolean canEntityMerge() {
        return this.canMerge();
    }

    @Override
    public byte getCachedState() {
        return this.cachedState;
    }

    @Override
    public void setCachedState(byte state) {
        this.cachedState = state;
    }

    @Override
    public boolean canMergeItself() {
        ItemStack stack = this.getStack();
        return stack.getCount() <= stack.getMaxCount() / 2;
    }

    @Override
    public boolean isMoreEmpty() {
        ItemStack stack = this.getStack();
        return stack.getCount() < stack.getMaxCount() / 2;
    }

    @Redirect(
            method = "tryMerge()V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getEntitiesByClass(Ljava/lang/Class;Lnet/minecraft/util/math/Box;Ljava/util/function/Predicate;)Ljava/util/List;"))

    private <T extends Entity> List<T> redirectGetEntitiesByClass(World world, Class<T> entityClass, Box box, Predicate<? super T> predicate) {
        SectionedEntityCache<Entity> entityCache = WorldHelper.getEntityCacheOrNull(world);
        if (entityCache == null) {
            return world.getEntitiesByClass(entityClass, box, predicate);
        }

        List<T> entities = Lists.newArrayList();
        entityCache.forEachInBox(box, section -> {
            ((MergeCacheInterface) section).forEachMergables(this, (entity) -> {
                if (entity.getBoundingBox().intersects(box)) {
                    entities.add((T) entity);
                }
            });
            return LazyIterationConsumer.NextIteration.CONTINUE;
        });

        return entities;
    }
}
