package com.github.tatercertified.potatoptimize.mixin.threading.entity_ticking;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BoatEntity.class)
public abstract class TestBoatEntityMixin extends Entity {

    public TestBoatEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    // TEST 1

    // TODO This is an issue
    //@Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/vehicle/BoatEntity;checkLocation()Lnet/minecraft/entity/vehicle/BoatEntity$Location;"))
    //private BoatEntity.Location test1(BoatEntity instance) {
    //    return BoatEntity.Location.IN_WATER;
    //}

    // **************** TESTING for the method above **************** \\

    @Redirect(method = "checkLocation", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/vehicle/BoatEntity;getUnderWaterLocation()Lnet/minecraft/entity/vehicle/BoatEntity$Location;"))
    private BoatEntity.Location a1(BoatEntity instance) {
        return BoatEntity.Location.ON_LAND;
    }

    // **************** TESTING for the method above **************** \\

    @Redirect(method = "getUnderWaterLocation", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getFluidState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/fluid/FluidState;"))
    private FluidState b1(World instance, BlockPos pos) {
        return Fluids.WATER.getDefaultState();
    }

    @Redirect(method = "getUnderWaterLocation", at = @At(value = "INVOKE", target = "Lnet/minecraft/fluid/FluidState;isIn(Lnet/minecraft/registry/tag/TagKey;)Z"))
    private boolean b1(FluidState instance, TagKey<Fluid> tag) {
        return false;
    }

    @Redirect(method = "getUnderWaterLocation", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/vehicle/BoatEntity;getBoundingBox()Lnet/minecraft/util/math/Box;"))
    private Box b1(BoatEntity instance) {
        return new Box(new BlockPos(0, 0, 0));
    }






    // TODO This is an issue
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/vehicle/BoatEntity;move(Lnet/minecraft/entity/MovementType;Lnet/minecraft/util/math/Vec3d;)V"))
    private void test4(BoatEntity instance, MovementType movementType, Vec3d vec3d) {
    }

    // TODO This is an issue
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/vehicle/BoatEntity;checkBlockCollision()V"))
    private void test6(BoatEntity instance) {
    }
}
