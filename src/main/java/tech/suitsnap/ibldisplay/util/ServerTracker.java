package tech.suitsnap.ibldisplay.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;

@Environment(EnvType.CLIENT)
public class ServerTracker {

    public String currentServer;
    String MCC_ISLAND_DOMAIN = "mccisland.net";
    MinecraftClient client = MinecraftClient.getInstance();

    public ServerTracker() {
        if (client.getCurrentServerEntry() != null) {
            currentServer = client.getCurrentServerEntry().address;
        } else {
            currentServer = "";
        }
    }

    public boolean isNotMCCIsland() {
        getCurrentServer();
        return !currentServer.endsWith(MCC_ISLAND_DOMAIN);
    }

    public void getCurrentServer() {
        if (client.getCurrentServerEntry() != null) {
            currentServer = client.getCurrentServerEntry().address;
        } else {
            currentServer = ":shrug:";
        }
    }
}
