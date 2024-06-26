package com.github.tatercertified.potatoptimize.mixin.entity.halloween;

import com.github.tatercertified.potatoptimize.utils.interfaces.IsHalloweenInterface;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.AmbientEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BatEntity.class)
public abstract class BatEntityMixin extends AmbientEntity {


    protected BatEntityMixin(EntityType<? extends AmbientEntity> entityType, World world) {
        super(entityType, world);
    }

    @Redirect(method = "canSpawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/BatEntity;isTodayAroundHalloween()Z"))
    private static boolean optimizedIsHalloween(@Local(ordinal = 0, argsOnly = true) WorldAccess world) {
        return ((IsHalloweenInterface) world.getServer()).isNearHalloween();
    }
}
