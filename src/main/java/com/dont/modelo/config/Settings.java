package com.dont.modelo.config;

import com.dont.modelo.Terminal;
import org.bukkit.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Settings {

    ;

    private String path;
    private Object value;

    Settings(String path) {
        this.path = path;
    }

    private void load(Terminal main) {
        this.value = main.getConfig().get(path);
    }

    public String asString() {
        String value = this.value instanceof String ? (String) this.value : this.value.toString();
        return ChatColor.translateAlternateColorCodes('&', value);
    }

    public List<String> asList() {
        List<String> value = (List<String>) this.value;
        return value.stream().map(string -> ChatColor.translateAlternateColorCodes('&', string)).collect(Collectors.toList());
    }

    public <T extends Enum<T>> T asEnum(Class<T> enumClass) {
        return Enum.valueOf(enumClass, (String) value);
    }

    public Material asMaterial() {
        return asEnum(Material.class);
    }

    public boolean asBoolean() {
        if (value instanceof String) {
            String string = (String) value;
            return Boolean.parseBoolean(string);
        }
        return (boolean) value;
    }

    public int asInt() {
        if (value instanceof String) {
            String string = (String) value;
            return Integer.parseInt(string);
        }
        return ((Number) value).intValue();
    }

    public double asDouble() {
        if (value instanceof String) {
            String string = (String) value;
            return Double.parseDouble(string);
        }
        return ((Number) value).doubleValue();
    }

    public long asLong() {
        if (value instanceof String) {
            String string = (String) value;
            return Long.parseLong(string);
        }
        return ((Number) value).longValue();
    }

    public Location asLocation() {
        if (value instanceof Location) return (Location) value;
        if (!(value instanceof String)) return null;
        String s = (String) value;
        if (!s.contains(";")) return null;
        String[] parts = s.split(";");
        double x = Double.parseDouble(parts[0]);
        double y = Double.parseDouble(parts[1]);
        double z = Double.parseDouble(parts[2]);
        float yaw = Float.parseFloat(parts[3]);
        float pitch = Float.parseFloat(parts[4]);
        World w = Bukkit.getServer().getWorld(parts[5]);
        return new Location(w, x, y, z, yaw, pitch);
    }

    public void set(Object object) {
        this.value = object;
        if (value instanceof String) {
            String value = (String) this.value;
            main.getConfig().set(path, value.replace("§", "&"));
        } else if (value instanceof List) {
            List<String> value = (List<String>) this.value;
            main.getConfig().set(path, value.stream().map(string -> string.replace("§", "&")).collect(Collectors.toList()));
        } else if (value instanceof Location) {
            Location loc = (Location) this.value;
            String value = loc.getX() + ";" + loc.getY() + ";" + loc.getZ() + ";" + loc.getYaw() + ";" + loc.getPitch() + ";" + loc.getWorld().getName();
            main.getConfig().set(path, value);
        } else {
            main.getConfig().set(path, value);
        }
        main.saveConfig();
    }

    private static Terminal main;

    public static void setup(Terminal main) {
        Settings.main = main;
        Arrays.stream(Settings.values()).forEach(s -> s.load(main));
    }

    @Override
    public String toString() {
        return value.toString();
    }


}
