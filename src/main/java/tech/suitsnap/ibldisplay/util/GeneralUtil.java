package tech.suitsnap.ibldisplay.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class GeneralUtil {
    public static List<String> reverse(List<String> list) {
        ArrayList<String> reversed = new ArrayList<>(list.size());

        for (int i = list.size() - 1; i >= 0; i--) {
            reversed.add(list.get(i));
        }
        return reversed.stream().toList();
    }

    public static String alphanumeric(String string) {
        String regex = "[^a-zA-Z0-9\\s,.?!:]+";
        return string.replaceAll(regex, "");
    }

    public static Text alphanumeric(Text text) {
        Style style = text.getStyle();
        return Text.literal(alphanumeric(text.getString())).setStyle(style);
    }
}
