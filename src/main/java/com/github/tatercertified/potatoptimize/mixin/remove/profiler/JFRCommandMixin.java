package com.github.tatercertified.potatoptimize.mixin.remove.profiler;

import net.minecraft.server.command.JfrCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(JfrCommand.class)
public class JFRCommandMixin {
    /**
     * @author QPCrummer
     * @reason Removal
     */
    @Overwrite
    private static int executeStart(ServerCommandSource source) {
        source.sendFeedback(() -> Text.literal("JFR has been removed by Potatoptimize; Use Spark instead!"), false);
        return 0;
    }

    /**
     * @author QPCrummer
     * @reason Removal
     */
    @Overwrite
    private static int executeStop(ServerCommandSource source) {
        source.sendFeedback(() -> Text.literal("JFR has been removed by Potatoptimize; Use Spark instead!"), false);
        return 0;
    }
}
