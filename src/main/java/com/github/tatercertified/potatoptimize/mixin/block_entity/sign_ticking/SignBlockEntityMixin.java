package com.github.tatercertified.potatoptimize.mixin.block_entity.sign_ticking;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

/**
 * Credit to PaperMC for the original optimization
 * Patch 0974 from PaperMC
 */
@Mixin(SignBlockEntity.class)
public abstract class SignBlockEntityMixin extends BlockEntity {
    @Shadow @Nullable private UUID editor;

    @Shadow protected abstract void tryClearInvalidEditor(SignBlockEntity blockEntity, World world, UUID uuid);

    public SignBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "getEditor", at = @At("HEAD"))
    private void checkEditor(CallbackInfoReturnable<UUID> cir) {
        if (this.hasWorld() && this.editor != null) {
            this.tryClearInvalidEditor((SignBlockEntity)(Object)this, this.getWorld(), this.editor);
        }
    }
}
