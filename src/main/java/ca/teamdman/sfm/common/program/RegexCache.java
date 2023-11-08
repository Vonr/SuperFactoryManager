package ca.teamdman.sfm.common.program;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;

// Having this logic inside ResourceIdentifier.java causes classloading issues lol
public class RegexCache {
    private static final Map<String, Predicate<String>> patternCache = new Object2ObjectOpenHashMap<>();

    static {
        // we want to make common match-all patterns fast
        // resource names are lowercase alphanumeric with underscores
        String[] matchAny = new String[]{
                ".",
                "[a-z0-9/._-]",
                };
        String[] suffixes = new String[]{"+", "*"};
        for (String s : matchAny) {
            for (String suffix : suffixes) {
                patternCache.put(s + suffix, s1 -> true);
                patternCache.put("^" + s + suffix, s1 -> true);
                patternCache.put("^" + s + suffix + "$", s1 -> true);
                patternCache.put(s + suffix + "$", s1 -> true);
            }
        }
    }

    public static Predicate<String> buildPredicate(String possiblePattern) {
        return isRegexPattern(possiblePattern)
               ? patternCache.computeIfAbsent(possiblePattern, x -> Pattern.compile(x).asMatchPredicate())
               : possiblePattern::equals;
    }

    public static boolean isRegexPattern(String pattern) {
        String specialChars = ".?*+^$[](){}|\\";
        for (int i = 0; i < pattern.length(); i++) {
            if (specialChars.indexOf(pattern.charAt(i)) >= 0) {
                return true;
            }
        }
        return false;
    }
}
