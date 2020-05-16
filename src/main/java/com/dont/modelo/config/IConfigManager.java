package com.dont.modelo.config;

import com.dont.modelo.Terminal;
import com.dont.modelo.utils.SectionBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

public abstract class IConfigManager {

    protected Terminal main;
    protected final SectionBuilder.Adapter<ItemStack> ITEM_ADAPTER = new SectionBuilder.ItemAdapter();
    protected final SectionBuilder.Adapter<Location> LOCATION_ADAPTER = new SectionBuilder.LocationAdapter();
    protected final SectionBuilder.Adapter<Sound> SOUND_ADAPTER = new SectionBuilder.EnumAdapter<>(Sound.class);
    protected final SectionBuilder.Adapter<EntityType> ENTITY_ADAPTER = new SectionBuilder.EnumAdapter<>(EntityType.class);

    public IConfigManager(Terminal main) {
        this.main = main;
    }

    protected <T> T get(String path) {
        Object object = main.getConfig().get(path);

        if (object instanceof String) object = ChatColor.translateAlternateColorCodes('&', object.toString());
        else if (object instanceof List)
            return (T) ((List<String>) object).stream().map(string -> ChatColor.translateAlternateColorCodes('&', string)).collect(Collectors.toList());

        return (T) object;
    }

    protected <T> T get(String path, SectionBuilder.Adapter<T> adapter) {
        return adapter.supply(main.getConfig().get(path));
    }

    public void set(String path, Object object) {
        if (object instanceof String) {
            String value = (String) object;
            main.getConfig().set(path, value.replace("§", "&"));
        } else if (object instanceof List) {
            List list = (List) object;
            if (list.isEmpty()) {
                main.getConfig().set(path, list);
            } else {
                Class<?> clazz = list.get(0).getClass();
                if (String.class.isAssignableFrom(clazz)) {
                    List<String> value = (List<String>) object;
                    main.getConfig().set(path, value.stream().map(string -> string.replace("§", "&")).collect(Collectors.toList()));
                } else if (Location.class.isAssignableFrom(clazz)) {
                    List<Location> value = (List<Location>) object;
                    main.getConfig().set(path, value.stream().map(loc -> loc.getX() + ";" + loc.getY() + ";" + loc.getZ() + ";" + loc.getYaw() + ";" + loc.getPitch() + ";" + loc.getWorld().getName()).collect(Collectors.toList()));
                } else {
                    main.getConfig().set(path, list);
                }
            }
        } else if (object instanceof Location) {
            Location loc = (Location) object;
            String value = loc.getX() + ";" + loc.getY() + ";" + loc.getZ() + ";" + loc.getYaw() + ";" + loc.getPitch() + ";" + loc.getWorld().getName();
            main.getConfig().set(path, value);
        } else {
            main.getConfig().set(path, object);
        }
        main.saveConfig();
    }

}