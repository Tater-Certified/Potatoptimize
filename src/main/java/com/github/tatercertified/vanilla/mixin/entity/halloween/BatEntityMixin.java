package com.github.tatercertified.vanilla.mixin.entity.halloween;

import com.github.tatercertified.vanilla.utils.interfaces.HalloweenInterface;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ambient.AmbientCreature;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Bat.class)
public abstract class BatEntityMixin extends AmbientCreature {


    protected BatEntityMixin(EntityType<? extends AmbientCreature> entityType, Level world) {
        super(entityType, world);
    }

    @Redirect(method = "checkBatSpawnRules", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ambient/Bat;isHalloween()Z"))
    private static boolean optimizedIsHalloween(@Local(ordinal = 0, argsOnly = true) LevelAccessor world) {
        return ((HalloweenInterface) world.getServer()).isNearHalloween();
    }
}
