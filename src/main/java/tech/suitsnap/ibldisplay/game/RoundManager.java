package tech.suitsnap.ibldisplay.game;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import tech.suitsnap.ibldisplay.IBLDisplayClient;

import java.util.List;

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
    public static boolean isFillRound = false;

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
        List<String> opponents = teamMembers.get(opponentTeam);
        if (opponents == null || opponents.isEmpty()) {
            client.player.sendMessage(Text.of("Fill round"));
            isFillRound = true;
        }
        client.player.sendMessage(Text.of("New round against: " + teamMembers.get(opponentTeam).toString()));
        roundStarted = true;
        roundKills = 0;
        roundDeaths = 0;
    }


    public static void gameEnd() {
        if (!isPlobby(MinecraftClient.getInstance().player))
            return;
        teamMembers.clear();
        if (roundWins >= 3) {
            mapWins++;
            roundWins = 0;
        }
        if (roundLosses >= 3) {
            mapLosses++;
            roundLosses = 0;
        }
    }

    public static void handleRoundEnd(Result result) {
        if (!isPlobby(MinecraftClient.getInstance().player))
            return;
        roundResult = result;
        if (isFillRound) result = Result.FILL;
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
            case FILL:
                break;
            default:
                IBLDisplayClient.LOGGER.info("Invalid result type in handleRoundEnd()");
        }

        MinecraftClient client = MinecraftClient.getInstance();
        assert client.player != null;
        client.player.sendMessage(Text.of(roundWins + " - " + roundLosses));
        roundStarted = false;
        isFillRound = false;
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

    public static void reset() {
        mapWins = 0;
        mapLosses = 0;
        roundWins = 0;
        roundLosses = 0;
        roundStarted = false;
        isFillRound = false;
        roundResult = null;
    }

    public enum Result {
        WIN,
        LOSS,
        TIE,
        FILL,
    }
}
