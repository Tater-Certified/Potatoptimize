package com.github.tatercertified.potatoptimize.mixin.memory.reduce_alloc;

import com.github.tatercertified.potatoptimize.utils.ArrayConstants;
import net.minecraft.world.WorldSaveHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.io.File;

@Mixin(WorldSaveHandler.class)
public class WorldSaveHandlerMixin {
    @Shadow @Final private File playerDataDir;

    /**
     * @author QPCrummer
     * @reason Reduce Allocations
     */
    @Overwrite
    public String[] getSavedPlayerIds() {
        String[] strings = this.playerDataDir.list();
        if (strings == null) {
            strings = ArrayConstants.emptyStringArray;
        }

        for(int i = 0; i < strings.length; ++i) {
            if (strings[i].endsWith(".dat")) {
                strings[i] = strings[i].substring(0, strings[i].length() - 4);
            }
        }

        return strings;
    }
}
