package tech.suitsnap.ibldisplay.event;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import tech.suitsnap.ibldisplay.game.CombatManager;
import tech.suitsnap.ibldisplay.game.RoundManager;

import static tech.suitsnap.ibldisplay.game.RoundManager.mapLosses;
import static tech.suitsnap.ibldisplay.game.RoundManager.mapWins;

@Environment(EnvType.CLIENT)
public class PlayerDisconnectHandler implements ClientPlayConnectionEvents.Disconnect {
    @Override
    public void onPlayDisconnect(ClientPlayNetworkHandler handler, MinecraftClient client) {
        if (client.player == null)
            return;
        if (mapLosses == 2 || mapWins == 2) {
            RoundManager.reset();
            CombatManager.reset();
        }
    }


}
