package com.dont.modelo.config;

import com.dont.modelo.Terminal;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Settings {

    ;

    private String path;
    private Object value;
    Settings(String path){
        this.path = path;
    }

    private void load(Terminal main){
        this.value = main.getConfig().get(path);
    }

    public String asString(){
        String value = (String) this.value;
        return ChatColor.translateAlternateColorCodes('&', value);
    }

    public List<String> asList(){
        List<String> value = (List<String>) this.value;
        return value.stream().map(string -> ChatColor.translateAlternateColorCodes('&', string)).collect(Collectors.toList());
    }

    public <T extends Enum<T>> T asEnum(Class<T> enumClass){
        return Enum.valueOf(enumClass, (String) value);
    }

    public Material asMaterial(){
        return asEnum(Material.class);
    }

    public boolean asBoolean(){
        if (value instanceof String){
            String string = (String) value;
            return Boolean.parseBoolean(string);
        }
        return (boolean) value;
    }

    public int asInt(){
        if (value instanceof String){
            String string = (String) value;
            return Integer.parseInt(string);
        }
        return ((Number)value).intValue();
    }

    public double asDouble(){
        if (value instanceof String){
            String string = (String) value;
            return Double.parseDouble(string);
        }
        return ((Number)value).doubleValue();
    }

    public long asLong(){
        if (value instanceof String){
            String string = (String) value;
            return Long.parseLong(string);
        }
        return ((Number)value).longValue();
    }

    @Override
    public String toString() {
        return asString();
    }

    public static void setup(Terminal main){
        Arrays.stream(Settings.values()).forEach(s -> s.load(main));
    }

}
