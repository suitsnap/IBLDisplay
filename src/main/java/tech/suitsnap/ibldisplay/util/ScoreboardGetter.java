package tech.suitsnap.ibldisplay.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static tech.suitsnap.ibldisplay.game.RoundManager.*;
import static tech.suitsnap.ibldisplay.util.GeneralUtil.alphanumeric;

@Environment(EnvType.CLIENT)
public class ScoreboardGetter {

    public static Text getScoreboardTitle(ClientPlayerEntity player) {
        assert player != null;

        Scoreboard scoreboard = player.getScoreboard();
        ScoreboardObjective objective = scoreboard.getObjectiveForSlot(Scoreboard.SIDEBAR_DISPLAY_SLOT_ID);
        if (objective == null) return null;
        List<Text> objectiveName = objective.getDisplayName().getSiblings();

        StringBuilder objectiveNameFormatted = new StringBuilder();
        objectiveName.forEach(text -> objectiveNameFormatted.append(text.getString()));
        return Text.literal(objectiveNameFormatted.toString());
    }

    public static boolean isPlobby(ClientPlayerEntity player) {
        assert player != null;

        Text currentScoreboardTitle = getScoreboardTitle(player);
        return currentScoreboardTitle != null && currentScoreboardTitle.getString().contains("Plobby");
    }

    public static Text getGame(ClientPlayerEntity player) {
        assert player != null;

        Text currentTitle = getScoreboardTitle(player);
        if (currentTitle == null || currentTitle.getString().length() < 5) return null;
        return Text.of(currentTitle.getString().substring(5).trim());
    }

    public static boolean isValid(ClientPlayerEntity player, boolean debug) {
        assert player != null;

        Text game = getGame(player);
        if (game == null) {
            if (debug) player.sendMessage(Text.of("Game is null"));
            return false;
        }
        if (debug) player.sendMessage(Text.of(game.getString()));
        return (game.getString().contains("BATTLE BOX")) && ((isPlobby(player)) || roundLosses + roundWins + mapLosses + mapWins != 0);
    }

    public static Collection<String> getScoreboardRows(ClientPlayerEntity player) {
        assert player != null;

        Scoreboard scoreboard = player.getScoreboard();
        ScoreboardObjective objective = scoreboard.getObjectiveForSlot(Scoreboard.SIDEBAR_DISPLAY_SLOT_ID);
        if (objective == null) return null;
        List<String> players = scoreboard.getKnownPlayers().stream().toList();
        ArrayList<String> prefixes = new ArrayList<>();
        for (int i = players.size(); i > 0; i--) {
            Team team = scoreboard.getPlayerTeam(players.get(i - 1));
            String prefix = team == null ? "" : alphanumeric(team.getPrefix().getString());
            prefixes.add(prefix);
        }
        return prefixes;
    }

    public static String getMap(ClientPlayerEntity player) {
        assert player != null;

        Collection<String> scoreboardRows = getScoreboardRows(player);
        if (scoreboardRows == null) return null;
        return scoreboardRows.stream().filter(row -> row.contains("MAP:")).toList().stream().findFirst().orElse("");
    }
}
