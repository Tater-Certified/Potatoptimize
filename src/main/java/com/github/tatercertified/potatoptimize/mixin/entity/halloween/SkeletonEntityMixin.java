package com.github.tatercertified.potatoptimize.mixin.entity.halloween;

import com.github.tatercertified.potatoptimize.interfaces.IsHalloweenInterface;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractSkeletonEntity.class)
public abstract class SkeletonEntityMixin extends HostileEntity {

    protected SkeletonEntityMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "initialize", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/AbstractSkeletonEntity;setCanPickUpLoot(Z)V", shift = At.Shift.AFTER), cancellable = true)
    private void optimizedCheckHalloween(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, EntityData entityData, NbtCompound entityNbt, CallbackInfoReturnable<EntityData> cir) {
        if (((IsHalloweenInterface)this.getServer()).isHalloween() && random.nextFloat() < 0.25f) {
            this.equipStack(EquipmentSlot.HEAD, new ItemStack(random.nextFloat() < 0.1f ? Blocks.JACK_O_LANTERN : Blocks.CARVED_PUMPKIN));
            this.armorDropChances[EquipmentSlot.HEAD.getEntitySlotId()] = 0.0f;
        }
        cir.setReturnValue(entityData);
    }

}
