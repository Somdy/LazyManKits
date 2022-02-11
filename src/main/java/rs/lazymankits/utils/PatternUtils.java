package rs.lazymankits.utils;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternUtils {
    
    @NotNull
    public static Matcher Match(String regex, int flags, CharSequence input) {
        Pattern pattern = Pattern.compile(regex, flags);
        return pattern.matcher(input);
    }

    @NotNull
    public static Matcher Match(String regex, CharSequence input) {
        return Match(regex, Pattern.MULTILINE, input);
    }
}