package com.github.tatercertified.potatoptimize.mixin.entity.fall_damage;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.TntMinecartEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(TntMinecartEntity.class)
public abstract class FixTntMinecartMixin extends Entity {

    public FixTntMinecartMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    protected void fall(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition) {
        if (onGround) {
            if (this.fallDistance > 0.0f) {
                state.getBlock().onLandedUpon(this.getWorld(), state, landedPosition, this, this.fallDistance);
                this.getWorld().emitGameEvent(GameEvent.HIT_GROUND, this.getPos(), GameEvent.Emitter.of(this, this.supportingBlockPos.map(blockPos -> this.getWorld().getBlockState(blockPos)).orElse(state)));
            }
            this.onLanding();
        } else if (heightDifference < 0.0) {
            this.fallDistance -= (float)heightDifference;
        }
    }
}
