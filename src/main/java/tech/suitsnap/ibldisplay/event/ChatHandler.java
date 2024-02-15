package tech.suitsnap.ibldisplay.event;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import tech.suitsnap.ibldisplay.game.RoundManager;

import static tech.suitsnap.ibldisplay.game.CombatManager.handleDeath;
import static tech.suitsnap.ibldisplay.game.CombatManager.handleKill;
import static tech.suitsnap.ibldisplay.game.RoundManager.*;
import static tech.suitsnap.ibldisplay.util.ScoreboardGetter.isValid;

@Environment(EnvType.CLIENT)
public class ChatHandler {

    public static void handleChat(String content) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        if (!isValid(player, false)) return;
        if ((!content.startsWith("[") || content.charAt(2) != ']' || content.contains("."))) return;
        String username = player.getName().getString();
        if (content.contains("You are facing")) {
            newRound(content);
            return;
        }
        if (content.contains("Game Over!")) {
            gameEnd();
            return;
        }
        String[] words = content.split(" ");
        if (words.length > 6) {
            switch (words[5]) {
                case "won":
                    handleRoundEnd(RoundManager.Result.WIN);
                    break;
                case "lost":
                    handleRoundEnd(RoundManager.Result.LOSS);
                    break;
                case "a":
                    if (words[6].equals("draw")) {
                        handleRoundEnd(RoundManager.Result.TIE);
                    }
                    break;
            }
        }
        if (!content.contains(username) || content.contains("survived")) return;
        boolean isKill = content.contains("[+");
        if (isKill) handleKill();
        else handleDeath();
    }

}
