package com.dont.modelo.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class Formatter {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("###,###.##");
    private static final String[] NUMBER_SUFFIX = new String[]{"K", "M", "B", "T", "Q", "QQ", "S", "SS", "O", "N", "D", "UN", "DD", "TR", "QT", "QN", "SD", "SPD", "OD", "ND", "VG", "UVG", "DVG", "TVG", "QTV"};
    private static final DecimalFormat FORMATTER = new DecimalFormat("###,###,###", new DecimalFormatSymbols(new Locale("pt", "BR")));

    public static String formatShortening(double value) {
        if (value < 1000) return ((int) value) + "";
        return format(value, 0);
    }

    public static String formatPunctuating(double value) {
        return FORMATTER.format(value);
    }

    private static String format(double n, int iteration) {
        double f = (n / 100D) / 10.0D;
        return f < 1000 || iteration >= NUMBER_SUFFIX.length - 1 ? DECIMAL_FORMAT.format(f) + NUMBER_SUFFIX[iteration] : format(f, iteration + 1);
    }

}
