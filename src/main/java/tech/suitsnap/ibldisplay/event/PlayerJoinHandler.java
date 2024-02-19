package tech.suitsnap.ibldisplay.event;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import tech.suitsnap.ibldisplay.game.RoundManager;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static tech.suitsnap.ibldisplay.util.ScoreboardGetter.*;
import static tech.suitsnap.ibldisplay.util.TabListGetter.getTabList;
import static tech.suitsnap.ibldisplay.util.TabListGetter.teamMembers;

@Environment(EnvType.CLIENT)
public class PlayerJoinHandler implements ClientPlayConnectionEvents.Join {
    @Override
    public void onPlayReady(ClientPlayNetworkHandler handler, PacketSender sender, MinecraftClient client) {
        if (client.player == null)
            return;
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.schedule(() -> {
            if (getGame(client.player) != null) {
                if(!isPlobby(client.player)) {
                    RoundManager.reset();
                    return;
                }

                if (!isValid(client.player, false))
                    return;
                teamMembers.clear();
                getTabList();
            }
        }, 3, TimeUnit.SECONDS);
    }
}
