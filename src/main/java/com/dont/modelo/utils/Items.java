package com.dont.modelo.utils;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Items {

    private static Items instance;

    public static Items getInstance() {
        if (instance == null) {
            instance = new Items(Configs.ITENS.getConfig());
        }
        return instance;
    }

    private FileConfiguration config;
    private SectionBuilder.Adapter<MenuItem> adapter;
    private Map<String, MenuItem> items;

    private Items(FileConfiguration config) {
        this.config = config;
        this.items = new HashMap<>();
        final SectionBuilder.ItemAdapter itemAdapter = new SectionBuilder.ItemAdapter();
        this.adapter = section -> {
            ConfigurationSection configurationSection = (ConfigurationSection) section;
            int slot = configurationSection.getInt("Slot");
            return new MenuItem(itemAdapter.supply(configurationSection), slot);
        };
    }

    public MenuItem getItem(String key) {
        MenuItem menuItem = items.get(key);
        if (menuItem == null) {
            menuItem = adapter.supply(config.getConfigurationSection(key));
            items.put(key, menuItem);
        }
        return menuItem;
    }

    public MenuItem getItem(String key, Object... placeholders) {
        if (placeholders.length % 2 != 0) {
            throw new RuntimeException("o numero de placeholders tem que ser par");
        }
        return getItem(key).edit(meta -> {
            if (meta.hasDisplayName()) {
                meta.setDisplayName(replace(meta.getDisplayName(), placeholders));
            }
            if (meta.hasLore()) {
                meta.setLore(meta.getLore().stream()
                        .map(lore -> replace(lore, placeholders))
                        .collect(Collectors.toList()));
            }
            if (meta instanceof SkullMeta) {
                SkullMeta skullMeta = (SkullMeta) meta;
                if (skullMeta.hasOwner()) {
                    skullMeta.setOwner(replace(skullMeta.getOwner(), placeholders));
                }
            }
        });
    }

    public static class MenuItem {

        private ItemStack item;
        private int slot;

        public MenuItem(ItemStack item, int slot) {
            this.item = item;
            this.slot = slot;
        }

        public ItemStack getItem() {
            return item;
        }

        public int getSlot() {
            return slot;
        }

        public MenuItem edit(Consumer<ItemMeta> editor) {
            ItemStack clone = this.item.clone();
            ItemMeta meta = clone.getItemMeta();
            editor.accept(meta);
            clone.setItemMeta(meta);
            return new MenuItem(clone, this.slot);
        }

    }

    private String replace(String string, Object... placeholders) {
        String temp = string;
        for (int i = 0; i < placeholders.length; i += 2) {
            temp = fastReplace(temp, placeholders[i].toString(), placeholders[i + 1].toString());
        }
        return temp;
    }

    private String fastReplace(String str, String target, String replacement) {
        int targetLength = target.length();
        if (targetLength == 0) {
            return str;
        }
        int idx2 = str.indexOf(target);
        if (idx2 < 0) {
            return str;
        }
        StringBuilder buffer = new StringBuilder(targetLength > replacement.length() ? str.length() : str.length() * 2);
        int idx1 = 0;
        do {
            buffer.append(str, idx1, idx2);
            buffer.append(replacement);
            idx1 = idx2 + targetLength;
            idx2 = str.indexOf(target, idx1);
        } while (idx2 > 0);
        buffer.append(str, idx1, str.length());
        return buffer.toString();
    }

}
