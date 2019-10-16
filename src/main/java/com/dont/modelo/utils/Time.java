package com.dont.modelo.utils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Time {

    private long milliseconds;
    private String and = "and";
    private final Map<int[], Function<Integer, String>> functions = new LinkedHashMap<>();

    private Time(long milliseconds) {
        this.milliseconds = milliseconds;
    }

    public static Time of(long milliseconds) {
        return new Time(Math.max(0, milliseconds));
    }

    public static Time of() {
        return of(0L);
    }

    public Time and(String and) {
        this.and = and;
        return this;
    }

    public Time millis(long millis) {
        this.milliseconds = millis;
        Map<int[], Function<Integer, String>> clone = new LinkedHashMap<>(functions);
        clone.forEach((k, v) -> {
            switch (k[0]) {
                case 3:
                    sec(v);
                    break;
                case 2:
                    min(v);
                    break;
                case 1:
                    hour(v);
                    break;
                case 0:
                    day(v);
                    break;
            }
        });

        return this;
    }

    public Time sec(Function<Integer, String> fun) {
        functions.keySet().removeIf(ints -> ints[0] == 3);
        functions.put(new int[]{3, (int) (milliseconds / 1000) % 60}, fun);
        return this;
    }

    public Time min(Function<Integer, String> fun) {
        functions.keySet().removeIf(ints -> ints[0] == 2);
        functions.put(new int[]{2, (int) ((milliseconds / (1000 * 60)) % 60)}, fun);
        return this;
    }

    public Time hour(Function<Integer, String> fun) {
        functions.keySet().removeIf(ints -> ints[0] == 1);
        functions.put(new int[]{1, (int) ((milliseconds / (1000 * 60 * 60)) % 24)}, fun);
        return this;
    }

    public Time day(Function<Integer, String> fun) {
        functions.keySet().removeIf(ints -> ints[0] == 0);
        functions.put(new int[]{0, (int) ((milliseconds / (1000 * 60 * 60 * 24)))}, fun);
        return this;
    }

    public String get() {
        int len = functions.entrySet().stream().filter(e -> e.getKey()[1] > 0).collect(Collectors.toList()).size();
        if (len == 0) return String.format("%.2f", (float) milliseconds) + "ms";

        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (Map.Entry<int[], Function<Integer, String>> e : functions.entrySet()) {
            Function<Integer, String> v = e.getValue();
            if (e.getKey()[1] == 0) continue; // fix zeros
            String f = v.apply(e.getKey()[1]);
            if (len == 1)
                sb.append(f).append(" ");
            else if (i == len - 1)
                sb.append(" ").append(and).append(" ").append(f);
            else {
                sb.append(f);
                if (i != len - 2) sb.append(",").append(" ");
            }
            i++;
        }

        return sb.toString();
    }

}