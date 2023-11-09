package com.github.tatercertified.potatoptimize.mixin.memory.reduce_alloc;

import com.github.tatercertified.potatoptimize.utils.ArrayConstants;
import net.minecraft.network.encryption.PacketEncryptionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PacketEncryptionManager.class)
public class PacketEncryptionManagerMixin {
    @Shadow private byte[] encryptionBuffer = ArrayConstants.emptyByteArray;
    @Shadow private byte[] conversionBuffer = ArrayConstants.emptyByteArray;
}
