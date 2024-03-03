package com.github.tatercertified.potatoptimize.mixin.memory.reduce_alloc;

import com.github.tatercertified.potatoptimize.utils.Constants;
import net.minecraft.network.encryption.PacketEncryptionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PacketEncryptionManager.class)
public class PacketEncryptionManagerMixin {
    @Shadow private byte[] encryptionBuffer = Constants.emptyByteArray;
    @Shadow private byte[] conversionBuffer = Constants.emptyByteArray;
}
