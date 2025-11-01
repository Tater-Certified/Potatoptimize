package com.github.tatercertified.vanilla.mixin.blockentity.ticking.sign;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

/**
 * This optimization disables ticking signs since they do not need to be ticked.
 * This should maintain vanilla parity.
 * @author PaperMC - Patch 0974 TODO Find the exact source link
 * @since 2.0.0
 * @version 1.0.0
 */
@Mixin(SignBlockEntity.class)
public abstract class SignBlockEntityMixin extends BlockEntity {
    @Shadow @Nullable private UUID playerWhoMayEdit;

    @Shadow protected abstract void clearInvalidPlayerWhoMayEdit(SignBlockEntity blockEntity, Level world, UUID uuid);

    public SignBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "getPlayerWhoMayEdit", at = @At("HEAD"))
    private void checkEditor(CallbackInfoReturnable<UUID> cir) {
        if (this.hasLevel() && this.playerWhoMayEdit != null) {
            this.clearInvalidPlayerWhoMayEdit((SignBlockEntity)(Object)this, this.getLevel(), this.playerWhoMayEdit);
        }
    }
}
