package tech.suitsnap.ibldisplay.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class GeneralUtil {
    public static String alphanumeric(String string) {
        String regex = "[^a-zA-Z0-9\\s,.?!:]+";
        return string.replaceAll(regex, "");
    }

    public static Text alphanumeric(Text text) {
        Style style = text.getStyle();
        return Text.literal(alphanumeric(text.getString())).setStyle(style);
    }
}
