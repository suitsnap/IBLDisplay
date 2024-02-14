package tech.suitsnap.ibldisplay.game;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import static tech.suitsnap.ibldisplay.game.RoundManager.roundStarted;

@Environment(EnvType.CLIENT)
public class CombatManager {
    public static int kills = 0;
    public static int roundKills = 0;
    public static int deaths = 0;
    public static int roundDeaths = 0;

    public static void handleKill() {
        kills++;
        if (roundStarted) roundKills++;
    }

    public static void handleDeath() {
        deaths++;
        if (roundStarted) roundDeaths++;
    }

}
