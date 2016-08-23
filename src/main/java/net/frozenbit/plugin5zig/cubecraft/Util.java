package net.frozenbit.plugin5zig.cubecraft;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Util {

    public static String extractGroup(String string, Pattern pattern, int group) {
        Matcher matcher = pattern.matcher(string);
        return matcher.matches() ? matcher.group(group) : null;
    }

}
