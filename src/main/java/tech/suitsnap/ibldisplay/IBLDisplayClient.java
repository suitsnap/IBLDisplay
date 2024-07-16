package tech.suitsnap.ibldisplay;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.font.TextRenderer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tech.suitsnap.ibldisplay.command.PositionCommand;
import tech.suitsnap.ibldisplay.command.ResetCommand;
import tech.suitsnap.ibldisplay.event.ChatHandler;
import tech.suitsnap.ibldisplay.event.PlayerJoinHandler;
import tech.suitsnap.ibldisplay.render.GameOverlay;
import tech.suitsnap.ibldisplay.util.ServerTracker;

import java.io.IOException;

@Environment(EnvType.CLIENT)
public class IBLDisplayClient implements ClientModInitializer {
    public final static ServerTracker serverTracker = new ServerTracker();
    public final static String MODID = "ibldisplay";
    public static final TextRenderer[] tr = new TextRenderer[1];
    static String MODNAME = "IBL Display";
    public static Logger LOGGER = LogManager.getLogger(MODNAME);

    @Override
    public void onInitializeClient() {

        final boolean[] loaded = {false};

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.world != null && !loaded[0] && client.getResourceManager() != null) {
                try {
                    tr[0] = GameOverlay.getTr();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                loaded[0] = true;
            }
        });
        LOGGER.info("Initializing {}", MODNAME);

        HudRenderCallback.EVENT.register(new GameOverlay());

        ClientReceiveMessageEvents.GAME.register((text, b) -> ChatHandler.handleChat(text.getString()));

        ClientPlayConnectionEvents.JOIN.register(new PlayerJoinHandler());

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            ResetCommand.register(dispatcher);
            PositionCommand.register(dispatcher);
        });

        LOGGER.info("Initialized {}", MODNAME);
    }

}
