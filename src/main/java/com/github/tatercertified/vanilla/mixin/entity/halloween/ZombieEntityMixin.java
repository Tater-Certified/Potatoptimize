package com.github.tatercertified.vanilla.mixin.entity.halloween;

import com.github.tatercertified.vanilla.utils.interfaces.HalloweenInterface;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Zombie.class)
public abstract class ZombieEntityMixin extends Monster {

    protected ZombieEntityMixin(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world);
    }

    @Redirect(method = "finalizeSpawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z"))
    private boolean cancelHalloweenCheck(ItemStack instance, @Local(ordinal = 0, argsOnly = true) ServerLevelAccessor serverLevelAccessor) {
        if (((HalloweenInterface)serverLevelAccessor.getServer()).isHalloween() && random.nextFloat() < 0.25f) {
            this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(random.nextFloat() < 0.1f ? Blocks.JACK_O_LANTERN : Blocks.CARVED_PUMPKIN));
            this.setDropChance(EquipmentSlot.HEAD, 0.0F);
        }
        return false;
    }
}
