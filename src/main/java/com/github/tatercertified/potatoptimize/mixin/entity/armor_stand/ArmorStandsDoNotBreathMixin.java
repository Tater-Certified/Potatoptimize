package com.github.tatercertified.potatoptimize.mixin.entity.armor_stand;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Credit to PaperMC patch #0130
 */
@Mixin(ArmorStandEntity.class)
public abstract class ArmorStandsDoNotBreathMixin extends LivingEntity {

    protected ArmorStandsDoNotBreathMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public boolean canBreatheInWater() {
        return true;
    }
}
