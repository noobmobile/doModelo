package com.dont.modelo.utils;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ConfigItems {

    private static final Random RANDOM = new Random();
    private final static SectionBuilder.ItemAdapter ITEM_ADAPTER = new SectionBuilder.ItemAdapter();

    private final static SectionBuilder.ListAdapter<ItemStack> ITEM_LIST_ADAPTER = new SectionBuilder.ListAdapter<>(ITEM_ADAPTER);
    private final static SectionBuilder.ListAdapter<ChanceableItem> CHANCEABLE_ITEM_LIST_ADAPTER = new SectionBuilder.ListAdapter<>(object -> {
        ConfigurationSection section = (ConfigurationSection) object;
        double chance = section.getDouble("Chance");
        ItemStack itemStack = ITEM_ADAPTER.supply(section);
        return new ChanceableItem(itemStack, chance);
    });
    private final static SectionBuilder.ListAdapter<CommandableItem> COMMANDABLE_ITEM_LIST_ADAPTER = new SectionBuilder.ListAdapter<>(object -> {
        ConfigurationSection section = (ConfigurationSection) object;
        List<String> commands = !section.isSet("Comandos") ? new ArrayList<>() : section.getStringList("Comandos");
        ItemStack itemStack = ITEM_ADAPTER.supply(section);
        return new CommandableItem(itemStack, commands);
    });
    private final static SectionBuilder.ListAdapter<ConfigurableItem> CONFIGURABLE_ITEM_LIST_ADAPTER = new SectionBuilder.ListAdapter<>(object -> {
        ConfigurationSection section = (ConfigurationSection) object;
        String key = section.getParent().getParent().getName() + "." + section.getName();
        double chance = section.getDouble("Chance");
        List<String> commands = !section.isSet("Comandos") ? new ArrayList<>() : section.getStringList("Comandos");
        ItemStack itemStack = ITEM_ADAPTER.supply(section);
        return new ConfigurableItem(key, itemStack, chance, commands);
    });

    public static List<ItemStack> getItemsByConfigurationSection(ConfigurationSection section) {
        return ITEM_LIST_ADAPTER.supply(section);
    }

    public static List<ChanceableItem> getChanceableItemsByConfigurationSection(ConfigurationSection section) {
        return CHANCEABLE_ITEM_LIST_ADAPTER.supply(section);
    }

    public static List<CommandableItem> getCommandableItemsByConfigurationSection(ConfigurationSection section) {
        return COMMANDABLE_ITEM_LIST_ADAPTER.supply(section);
    }

    public static List<ConfigurableItem> getConfigurableItemsByConfigurationSection(ConfigurationSection section) {
        return CONFIGURABLE_ITEM_LIST_ADAPTER.supply(section);
    }

    public static ItemStack getItemByConfigurationSection(ConfigurationSection section) {
        return ITEM_ADAPTER.supply(section);
    }

    public static <T extends Chanceable> T getRandomItem(List<T> list) {
        double total = list.stream().mapToDouble(Chanceable::getChance).sum();
        double random = RANDOM.nextDouble() * total;
        double weight = 0D;
        for (T t : list) {
            weight += t.getChance();
            if (weight >= random) {
                return t;
            }
        }
        return null; // nunca vai acontecer
    }

    public static interface Item {
        public ItemStack getItem();

        public default void execute(Player player) {
            player.getInventory().addItem(getItem());
        }
    }

    public static interface Chanceable {
        public double getChance();
    }

    public static interface Commandable {
        public List<String> getCommands();

        public default void execute(Player player) {
            getCommands().forEach(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("$player", player.getName())));
        }
    }

    public static class ChanceableItem implements Chanceable, Item {

        private ItemStack item;
        private double chance;

        public ChanceableItem(ItemStack item, double chance) {
            this.item = item;
            this.chance = chance;
        }

        public ItemStack getItem() {
            return item;
        }

        @Override
        public double getChance() {
            return chance;
        }
    }

    public static class CommandableItem implements Item, Commandable {

        private ItemStack item;
        private List<String> commands;

        public CommandableItem(ItemStack item, List<String> commands) {
            this.item = item;
            this.commands = commands;
        }

        public ItemStack getItem() {
            return item;
        }

        @Override
        public void execute(Player player) {
            if (commands == null || commands.isEmpty()) {
                player.getInventory().addItem(item);
            } else {
                getCommands().forEach(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("$player", player.getName())));
            }
        }

        public List<String> getCommands() {
            return commands;
        }

    }

    public static class ConfigurableItem implements Commandable, Chanceable, Item {

        private String key;
        private ItemStack item;
        private double chance;
        private List<String> commands;

        public ConfigurableItem(String key, ItemStack item, double chance, List<String> commands) {
            this.key = key;
            this.item = item;
            this.chance = chance;
            this.commands = commands;
        }

        public ItemStack getItem() {
            return item;
        }

        @Override
        public double getChance() {
            return chance;
        }

        @Override
        public List<String> getCommands() {
            return commands;
        }

        @Override
        public void execute(Player player) {
            if (commands == null || commands.isEmpty()) {
                player.getInventory().addItem(item);
            } else {
                getCommands().forEach(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("$player", player.getName())));
            }
        }
    }

}
