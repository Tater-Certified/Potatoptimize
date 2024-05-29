package com.github.tatercertified.potatoptimize.mixin.entity.halloween;

import com.github.tatercertified.potatoptimize.Potatoptimize;
import com.github.tatercertified.potatoptimize.interfaces.IsHalloweenInterface;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.AmbientEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BatEntity.class)
public abstract class BatEntityMixin extends AmbientEntity {

    protected BatEntityMixin(EntityType<? extends AmbientEntity> entityType, World world) {
        super(entityType, world);
    }

    @Redirect(method = "canSpawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/BatEntity;isTodayAroundHalloween()Z"))
    private static boolean optimizedIsHalloween() {
        return ((IsHalloweenInterface) Potatoptimize.almightyServerInstance).isNearHalloween();
    }

}
