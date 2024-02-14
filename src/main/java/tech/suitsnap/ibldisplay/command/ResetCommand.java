package tech.suitsnap.ibldisplay.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import tech.suitsnap.ibldisplay.IBLDisplayClient;

import static tech.suitsnap.ibldisplay.game.CombatManager.deaths;
import static tech.suitsnap.ibldisplay.game.CombatManager.kills;

@Environment(EnvType.CLIENT)
public class ResetCommand {

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("reset").executes(ResetCommand::execute));
    }

    public static int execute(CommandContext<FabricClientCommandSource> context) {
        if (IBLDisplayClient.serverTracker.isNotMCCIsland()) {
            context.getSource().sendFeedback(Text.literal("You aren't on MCC Island"));
            return Command.SINGLE_SUCCESS;
        }

        kills = 0;
        deaths = 0;

        return Command.SINGLE_SUCCESS;
    }
}
