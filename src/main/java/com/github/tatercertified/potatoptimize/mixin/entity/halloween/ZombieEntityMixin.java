package com.github.tatercertified.potatoptimize.mixin.entity.halloween;

import com.github.tatercertified.potatoptimize.interfaces.IsHalloweenInterface;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ZombieEntity.class)
public abstract class ZombieEntityMixin extends HostileEntity {

    protected ZombieEntityMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Redirect(method = "initialize", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z"))
    private boolean cancelHalloweenCheck(ItemStack instance) {
        if (((IsHalloweenInterface)this.getServer()).isHalloween() && random.nextFloat() < 0.25f) {
            this.equipStack(EquipmentSlot.HEAD, new ItemStack(random.nextFloat() < 0.1f ? Blocks.JACK_O_LANTERN : Blocks.CARVED_PUMPKIN));
            this.armorDropChances[EquipmentSlot.HEAD.getEntitySlotId()] = 0.0f;
        }
        return false;
    }
}
