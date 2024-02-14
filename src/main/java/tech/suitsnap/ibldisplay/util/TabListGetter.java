package tech.suitsnap.ibldisplay.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;

import java.util.*;

import static tech.suitsnap.ibldisplay.util.GeneralUtil.alphanumeric;

@Environment(EnvType.CLIENT)
public class TabListGetter {
    private static final Map<String, String> teamColours = Map.of(
            "red", "Red",
            "gold", "Orange",
            "yellow", "Yellow",
            "green", "Lime",
            "#00AA33", "Green",
            "#5588FF", "Blue",
            "#8833FF", "Purple",
            "#00BB99", "Cyan",
            "light_purple", "Pink",
            "#55DDFF", "Aqua");

    public static Map<String, List<String>> teamMembers = new HashMap<>();

    public static Map<String, List<String>> getTabList() {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        assert player != null;
        Collection<PlayerListEntry> players = player.networkHandler.getListedPlayerListEntries();


        for (PlayerListEntry tabListPlayer : players) {
            if (tabListPlayer.getDisplayName() == null)
                continue;
            List<Text> displayNameSegments = tabListPlayer.getDisplayName().getSiblings();
            if (displayNameSegments.isEmpty())
                continue;
            displayNameSegments.forEach(segment -> {
                if (segment.getSiblings().isEmpty())
                    return;
                Text subsegment = segment.getSiblings().get(0);
                if (subsegment.getString().isEmpty())
                    return;
                TextColor color = subsegment.getStyle().getColor();
                if (color == null)
                    return;
                String content = subsegment.getString();
                int length = content.length();
                if (content.contains("Team") || length > 20 || length < 3 || content.contains("."))
                    return;
                String teamName = teamColours.get(color.toString());
                if (teamMembers.containsKey(teamName)) {
                    teamMembers.get(teamName).add(alphanumeric(subsegment.getString()));
                } else {
                    List<String> newList = new ArrayList<>();
                    newList.add(alphanumeric(subsegment.getString()));
                    teamMembers.put(teamName, newList);
                }
            });
        }
        player.sendMessage(Text.of(teamMembers.toString()));
        return teamMembers;
    }
}