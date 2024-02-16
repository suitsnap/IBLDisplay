package tech.suitsnap.ibldisplay.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import tech.suitsnap.ibldisplay.IBLDisplayClient;
import tech.suitsnap.ibldisplay.render.Position;

import java.util.Arrays;

import static tech.suitsnap.ibldisplay.render.GameOverlay.overlayPosition;

@Environment(EnvType.CLIENT)
public class PositionCommand {

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("position")
                .then(ClientCommandManager.argument("position", StringArgumentType.word())
                        .suggests((context, builder) -> CommandSource.suggestMatching(Arrays.stream(Position.values()).map(Enum::name), builder))
                        .executes(context -> execute(context, StringArgumentType.getString(context, "position"))))
                .executes(context -> execute(context, null)));
    }

    public static int execute(CommandContext<FabricClientCommandSource> context, @Nullable String positionName) throws IllegalArgumentException {
        if (IBLDisplayClient.serverTracker.isNotMCCIsland()) {
            context.getSource().sendFeedback(Text.literal("You aren't on MCC Island"));
        }

        Position[] values = Position.values();
        if (positionName == null) {
            int next = (overlayPosition.ordinal() + 1) % values.length;
            overlayPosition = values[next];
        } else {
            try {
                overlayPosition = Position.valueOf(positionName.toUpperCase());
            } catch (IllegalArgumentException e) {
                context.getSource().sendFeedback(Text.of("Invalid position: " + positionName));
                return Command.SINGLE_SUCCESS;
            }
        }
        context.getSource().sendFeedback(Text.of("Overlay position set to: " + overlayPosition.name()));

        return Command.SINGLE_SUCCESS;
    }
}