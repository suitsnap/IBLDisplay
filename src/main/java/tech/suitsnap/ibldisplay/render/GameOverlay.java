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
import java.util.Objects;

import static tech.suitsnap.ibldisplay.IBLDisplayClient.serverTracker;
import static tech.suitsnap.ibldisplay.IBLDisplayClient.tr;
import static tech.suitsnap.ibldisplay.game.CombatManager.deaths;
import static tech.suitsnap.ibldisplay.game.CombatManager.kills;
import static tech.suitsnap.ibldisplay.util.ScoreboardGetter.getMap;
import static tech.suitsnap.ibldisplay.util.ScoreboardGetter.isValid;

@Environment(EnvType.CLIENT)
public class GameOverlay implements HudRenderCallback {
    public static final Identifier KILL_OVERLAY = new Identifier(IBLDisplayClient.MODID, "assets/kill_overlay.png");
    public static final Identifier MAP_OVERLAY = new Identifier(IBLDisplayClient.MODID, "assets/map_overlay.png");

    public static void renderOverlay(DrawContext drawContext, int x, int y) {
        drawContext.drawTexture(MAP_OVERLAY, x - 64, y - 143, -1, 0, 0, 128, 39, 128, 39);
        drawContext.drawTexture(KILL_OVERLAY, x - 96, y - 100, -1, 0, 0, 128, 13, 128, 13);
        drawContext.drawCenteredTextWithShadow(tr[0], Text.of(Objects.requireNonNull(getMap(MinecraftClient.getInstance().player)).substring(5)), x, y - 137, 0xFFFFFF);

        Text killText = Text.literal(String.valueOf(kills));
        Text deathText = Text.literal(String.valueOf(deaths));

        int killOffset = tr[0].getWidth(killText) / 2;
        int deathOffset = tr[0].getWidth(deathText) / 2;
        drawContext.drawCenteredTextWithShadow(tr[0], killText, x - 13 - killOffset, y - 93, 0xFFFFFF);
        drawContext.drawCenteredTextWithShadow(tr[0], deathText, x + 13 - deathOffset, y - 93, 0xFFFFFF);
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
        int x = width / 2;

        renderOverlay(drawContext, x, height);
    }
}
