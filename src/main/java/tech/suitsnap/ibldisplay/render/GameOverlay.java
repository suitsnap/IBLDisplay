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
import static tech.suitsnap.ibldisplay.util.ScoreboardGetter.isValid;

@Environment(EnvType.CLIENT)
public class GameOverlay implements HudRenderCallback {
    public static final Identifier KILL_OVERLAY = new Identifier(IBLDisplayClient.MODID, "assets/kill_overlay.png");
    public static final Identifier MAP_OVERLAY = new Identifier(IBLDisplayClient.MODID, "assets/map_overlay.png");

    public static void renderKillOverlay(DrawContext drawContext, int x, int y) {
        MinecraftClient client = MinecraftClient.getInstance();
        int killOffset = client.textRenderer.getWidth(Text.literal(String.valueOf(kills))) / 2;
        int deathOffset = client.textRenderer.getWidth(Text.literal(String.valueOf(deaths))) / 2;

        drawContext.drawTexture(KILL_OVERLAY, x - 96, y - 100, -10, 0, 0, 128, 13, 128, 13);
        drawContext.drawCenteredTextWithShadow(tr[0], Text.literal(String.valueOf(kills)), x - 13 - killOffset, y - 93, 0xFFFFFF);
        drawContext.drawCenteredTextWithShadow(tr[0], Text.literal(String.valueOf(deaths)), x + 13 - deathOffset, y - 93, 0xFFFFFF);
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
        List<Boolean> returnReasons = List.of(player == null, tr[0] == null, serverTracker.isNotMCCIsland(), !isValid(client.player, false));

        if (returnReasons.contains(true)) return;

        int x, y;
        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();
        x = width / 2;
        y = height;


        drawContext.drawTexture(MAP_OVERLAY, x - 64, y - 130, -10, 0, 0, 128, 26, 128, 26);
        renderKillOverlay(drawContext, x, y);
    }
}
