package team.tnt.collectoralbum.util;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.TreeMap;

public final class TextHelper {

    private static final TreeMap<Integer, String> ROMAN_NUMBER_MAP = new TreeMap<>();

    public static String toRomanNumberString(int value) {
        int floor = ROMAN_NUMBER_MAP.floorKey(value);
        if (value == floor) {
            return ROMAN_NUMBER_MAP.get(value);
        }
        return ROMAN_NUMBER_MAP.get(floor) + toRomanNumberString(value - floor);
    }

    public static String splitAndCapitalizeFirstWords(String raw, String regex) {
        return Arrays.stream(raw.split(regex))
                .map(w -> w.substring(0, 1).toUpperCase(Locale.ROOT) + w.substring(1).toLowerCase(Locale.ROOT))
                .reduce((s1, s2) -> s1 + " " + s2)
                .orElse("NULL");
    }

    public static <T> int nullSortFirst(T t1, T t2) {
        if (Objects.equals(t1, t2)) {
            return 0;
        }
        if (t1 == null) {
            return -1;
        }
        if (t2 == null) {
            return 1;
        }
        return 0;
    }

    static {
        ROMAN_NUMBER_MAP.put(1000, "M");
        ROMAN_NUMBER_MAP.put(900, "CM");
        ROMAN_NUMBER_MAP.put(500, "D");
        ROMAN_NUMBER_MAP.put(400, "CD");
        ROMAN_NUMBER_MAP.put(100, "C");
        ROMAN_NUMBER_MAP.put(90, "XC");
        ROMAN_NUMBER_MAP.put(50, "L");
        ROMAN_NUMBER_MAP.put(40, "XL");
        ROMAN_NUMBER_MAP.put(10, "X");
        ROMAN_NUMBER_MAP.put(9, "IX");
        ROMAN_NUMBER_MAP.put(5, "V");
        ROMAN_NUMBER_MAP.put(4, "IV");
        ROMAN_NUMBER_MAP.put(1, "I");
    }
}
