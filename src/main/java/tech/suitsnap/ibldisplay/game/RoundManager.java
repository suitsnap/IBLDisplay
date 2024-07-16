package tech.suitsnap.ibldisplay.game;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import tech.suitsnap.ibldisplay.IBLDisplayClient;

import static tech.suitsnap.ibldisplay.game.CombatManager.*;
import static tech.suitsnap.ibldisplay.util.ScoreboardGetter.isPlobby;
import static tech.suitsnap.ibldisplay.util.TabListGetter.teamMembers;

@Environment(EnvType.CLIENT)
public class RoundManager {
    public static int mapWins = 0;
    public static int mapLosses = 0;

    public static int roundWins = 0;
    public static int roundLosses = 0;

    public static boolean roundStarted = false;

    public static Result roundResult;

    public static void newRound(String string) {
        if (!isPlobby(MinecraftClient.getInstance().player))
            return;
        String[] words = string.split(" ");
        if (words.length < 6)
            return;
        String opponentTeam = words[6];
        MinecraftClient client = MinecraftClient.getInstance();
        assert client.player != null;
        client.player.sendMessage(Text.of("New round against: " + teamMembers.get(opponentTeam).toString()));
        roundStarted = true;
        roundKills = 0;
        roundDeaths = 0;
    }


    public static void gameEnd() {
        if (!isPlobby(MinecraftClient.getInstance().player))
            return;
        teamMembers.clear();
        if (roundWins >= 4) {
            mapWins++;
            resetRound();
        }
        if (roundLosses >= 4) {
            mapLosses++;
            resetRound();
        }
    }

    public static void handleRoundEnd(Result result) {
        if (!isPlobby(MinecraftClient.getInstance().player))
            return;
        roundResult = result;
        switch (result) {
            case TIE:
                handleTie();
                break;
            case WIN:
                handleWin();
                break;
            case LOSS:
                handleLoss();
                break;
            default:
                IBLDisplayClient.LOGGER.info("Invalid result type in handleRoundEnd()");
        }

        roundStarted = false;
    }

    public static void handleTie() {
        kills -= roundKills;
        deaths -= roundDeaths;
    }

    public static void handleWin() {
        roundWins++;
    }

    public static void handleLoss() {
        roundLosses++;
    }

    public static void resetAll() {
        mapWins = 0;
        mapLosses = 0;
        roundWins = 0;
        roundLosses = 0;
        roundStarted = false;
        roundResult = null;
    }
    public static void resetRound() {
        roundWins = 0;
        roundLosses = 0;
        roundStarted = false;
        roundResult = null;
    }

    public enum Result {
        WIN,
        LOSS,
        TIE,
    }
}
