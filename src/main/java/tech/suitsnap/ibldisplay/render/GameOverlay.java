package tech.suitsnap.ibldisplay.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.*;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.commons.compress.utils.Lists;
import tech.suitsnap.ibldisplay.IBLDisplayClient;

import java.io.IOException;
import java.util.List;

import static tech.suitsnap.ibldisplay.IBLDisplayClient.serverTracker;
import static tech.suitsnap.ibldisplay.IBLDisplayClient.tr;
import static tech.suitsnap.ibldisplay.game.CombatManager.deaths;
import static tech.suitsnap.ibldisplay.game.CombatManager.kills;
import static tech.suitsnap.ibldisplay.game.RoundManager.*;
import static tech.suitsnap.ibldisplay.util.ScoreboardGetter.getMap;
import static tech.suitsnap.ibldisplay.util.ScoreboardGetter.isValid;

@Environment(EnvType.CLIENT)
public class GameOverlay implements HudRenderCallback {
    public static final Identifier KILL = new Identifier(IBLDisplayClient.MODID, "assets/killdeath/kill.png");
    public static final Identifier DEATH = new Identifier(IBLDisplayClient.MODID, "assets/killdeath/death.png");
    public static final Identifier ROUND_FULL = new Identifier(IBLDisplayClient.MODID, "assets/winloss/small_win.png");
    public static final Identifier ROUND_EMPTY = new Identifier(IBLDisplayClient.MODID, "assets/winloss/small_empty.png");
    public static final Identifier FINAL_FULL = new Identifier(IBLDisplayClient.MODID, "assets/winloss/final_win.png");
    public static final Identifier FINAL_EMPTY = new Identifier(IBLDisplayClient.MODID, "assets/winloss/final_empty.png");


    //Config - TODO: Add ModMenu support
    public static Position overlayPosition = Position.BOTTOM_CENTER;
    public static boolean isOverlayEnabled = true;
    public static boolean killsOnRight = true;

    public static void renderOverlay(DrawContext drawContext, int x, int y) {
        if (!isOverlayEnabled) return;
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        assert player != null;
        String returnedMap = getMap(player) == null ? "MAP: LOBBY" : getMap(player);
        assert returnedMap != null;
        Text map = returnedMap.length() > 5 ? Text.of(returnedMap.substring(5).strip()) : Text.of("LOBBY");
        int mapWidth = tr[0].getWidth(map);
        int mapStart = x - mapWidth / 2 - 5;
        int mapEnd = mapStart + mapWidth + 10;

        drawContext.drawTexture(KILL, x - (killsOnRight ? 11 : 26), y - 78, 10, 0, 0, 9, 9, 9, 9);
        drawContext.drawTexture(DEATH, x + 15, y - 78, 10, 0, 0, 9, 9, 9, 9);

        drawContext.fill(mapStart, y - 135, mapEnd, y - 123, 0x55000000);
        drawContext.fill(x - 64, y - 120, x + 64, y - 104, 0x55000000);
        drawContext.fill(x - 64, y - 100, x + 64, y - 84, 0x55000000);
        drawContext.fill(x - 32, y - 80, x + 32, y - 67, 0x55000000);

        Text killText = Text.literal(String.valueOf(kills));
        Text deathText = Text.literal(String.valueOf(deaths));

        int killOffset = tr[0].getWidth(killText) / 2;
        int deathOffset = tr[0].getWidth(deathText) / 2;
        drawContext.drawCenteredTextWithShadow(tr[0], killText, x - 13 - killOffset, y - 73, 0xFFFFFF);
        drawContext.drawCenteredTextWithShadow(tr[0], deathText, x + (killsOnRight ? 13 : 11) - deathOffset, y - 73, 0xFFFFFF);

        drawContext.drawCenteredTextWithShadow(tr[0], map, x, y - 128, 0xFFFFFF);

        int iconWidth = 10, iconHeight = 10;
        int totalWidth = 3 * 10 + 2 * (5);
        int roundStartX = x - totalWidth - 15;
        int roundY = y - 117;

        for (int i = 0; i < 3; i++) {
            int iconX = roundStartX + i * iconWidth;

            Identifier texture;
            if (i < 2) {
                texture = roundWins > i ? ROUND_FULL : ROUND_EMPTY;
                drawContext.drawTexture(texture, iconX, roundY, 0, 0, iconWidth, iconHeight, iconWidth, iconHeight);
            } else {
                texture = roundWins > 2 ? FINAL_FULL : FINAL_EMPTY;
                drawContext.drawTexture(texture, iconX, roundY, 0, 0, iconWidth + 2, iconHeight, iconWidth + 2, iconHeight);
            }
            roundStartX += iconWidth - 5;
        }

        roundStartX = x + 15;

        for (int i = 0; i < 3; i++) {
            int iconX = roundStartX + i * iconWidth;

            Identifier texture;
            switch (i) {
                case 0 -> {
                    texture = roundLosses == 3 ? FINAL_FULL : FINAL_EMPTY;
                    drawContext.drawTexture(texture, iconX - 2, roundY, 0, 0, iconWidth + 2, iconHeight, iconWidth + 2, iconHeight);
                }
                case 1 -> {
                    texture = roundLosses >= 2 ? ROUND_FULL : ROUND_EMPTY;
                    drawContext.drawTexture(texture, iconX, roundY, 0, 0, iconWidth, iconHeight, iconWidth, iconHeight);
                }
                default -> {
                    texture = roundLosses >= 1 ? ROUND_FULL : ROUND_EMPTY;
                    drawContext.drawTexture(texture, iconX, roundY, 0, 0, iconWidth, iconHeight, iconWidth, iconHeight);
                }
            }
            roundStartX += iconWidth - 5;
        }
        
        int mapStartX = x - totalWidth;
        int mapY = y - 97;

        for (int i = 0; i < 2; i++) {
            int iconX = mapStartX + i * iconWidth;
            Identifier texture;
            if (i == 0) {
                texture = mapWins > 0 ? ROUND_FULL : ROUND_EMPTY;
                drawContext.drawTexture(texture, iconX, mapY, 0, 0, iconWidth, iconHeight, iconWidth, iconHeight);
            } else {
                texture = mapWins == 2 ? FINAL_FULL : FINAL_EMPTY;
                drawContext.drawTexture(texture, iconX, mapY, 0, 0, iconWidth + 2, iconHeight, iconWidth + 2, iconHeight);
            }
            mapStartX += iconWidth - 5;
        }

        mapStartX = x + 15;

        for (int i = 0; i < 2; i++) {
            int iconX = mapStartX + i * iconWidth;
            Identifier texture;
            if (i == 0) {
                texture = mapLosses == 2 ? FINAL_FULL : FINAL_EMPTY;
                drawContext.drawTexture(texture, iconX - 2, mapY, 0, 0, iconWidth + 2, iconHeight, iconWidth + 2, iconHeight);
            } else {
                texture = mapLosses > 0 ? ROUND_FULL : ROUND_EMPTY;
                drawContext.drawTexture(texture, iconX, mapY, 0, 0, iconWidth, iconHeight, iconWidth, iconHeight);
            }
            mapStartX += iconWidth - 5;
        }
    }

    public static TextRenderer getTr() throws IOException {
        MinecraftClient mc = MinecraftClient.getInstance();
        List<Font> list = Lists.newArrayList();
        TrueTypeFontLoader loader = new TrueTypeFontLoader(new Identifier(IBLDisplayClient.MODID, "hud.ttf"), 7, 4, TrueTypeFontLoader.Shift.NONE, "");
        FontLoader.Loadable loadable = loader.build().orThrow();
        Font font = loadable.load(mc.getResourceManager());
        list.add(font);
        FontStorage storage = new FontStorage(mc.getTextureManager(), new Identifier(IBLDisplayClient.MODID, "tr"));
        storage.setFonts(list);
        return new TextRenderer(id -> storage, true);
    }

    @Override
    public void onHudRender(DrawContext drawContext, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;

        if (player == null || tr[0] == null || serverTracker.isNotMCCIsland() || !isValid(client.player, false)) {
            return;
        }

        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();
        int x;
        int y;

        switch (overlayPosition) {
            case TOP_LEFT -> {
                x = width / 5;
                y = height / 3;
            }
            case TOP_CENTER -> {
                x = width / 2;
                y = height / 3;
            }
            case TOP_RIGHT -> {
                x = width / 5 * 4;
                y = height / 3;
            }
            case BOTTOM_CENTER -> {
                x = width / 2 - 20;
                y = height;
            }
            default -> throw new IllegalStateException("Unexpected value: " + overlayPosition);
        }

        renderOverlay(drawContext, x, y);
    }
}
