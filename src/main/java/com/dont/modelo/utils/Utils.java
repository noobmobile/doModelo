package com.dont.modelo.utils;

import com.dont.modelo.Terminal;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.lang.reflect.Constructor;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class Utils {

    private static final Terminal main = Terminal.getPlugin(Terminal.class);
    private static final String prefix = ChatColor.GREEN + "[" + main.getName() + "] " + ChatColor.WHITE;
    public static final ItemStack EMPTY = new ItemComposer(Material.WEB).setName("§eNada encontrado").toItemStack(), BACK = new ItemComposer(Material.ARROW).setName("§7Voltar").toItemStack();
    private static final DecimalFormatSymbols DFS = new DecimalFormatSymbols(new Locale("pt", "BR"));
    public static final DecimalFormat FORMATTER = new DecimalFormat("###,###,###", DFS);
    public static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy 'as' HH:mm:ss");
    private final static Random RANDOM = new Random();

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("###,###.##");
    private static final String[] NUMBER_SUFFIX = new String[]{"K", "M", "B", "T", "Q", "QQ", "S", "SS", "O", "N", "D", "UN", "DD", "TR", "QT", "QN", "SD", "SPD", "OD", "ND", "VG", "UVG", "DVG", "TVG", "QTV"};

    public static boolean DEBUGGING = true;

    public static void debug(LogType type, String mensagem) {
        if (type == LogType.DEBUG && !DEBUGGING) return;
        Bukkit.getConsoleSender().sendMessage("[" + type.name() + "] " + prefix + mensagem);
    }


    public static String format(double value) {
        if (value < 1000) return ((int) value) + "";
        return format(value, 0);
    }

    private static String format(double n, int iteration) {
        double f = (n / 100D) / 10.0D;
        return f < 1000 || iteration >= NUMBER_SUFFIX.length - 1 ? DECIMAL_FORMAT.format(f) + NUMBER_SUFFIX[iteration] : format(f, iteration + 1);
    }

    public static long measureTime(Runnable runnable) {
        long before = System.currentTimeMillis();
        runnable.run();
        return System.currentTimeMillis() - before;
    }

    public static void measureTime(String mensagem, Runnable runnable, CommandSender sender) {
        sender.sendMessage(mensagem.replace("<tempo>", measureTime(runnable) + ""));
    }

    public static void measureTime(String mensagem, Runnable runnable) {
        measureTime(mensagem, runnable, Bukkit.getConsoleSender());
    }

    public static final List<Integer> ALLOWED = Arrays.asList(
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34);

    public static void setupItems(Inventory inventory, List<ItemStack> items) {
        setupItems(inventory, items, ALLOWED);
    }

    public static void setupItems(Inventory inventory, List<ItemStack> items, List<Integer> allowed) {
        int lastIndex = 0;
        for (int i = 0; i < 54; i++) {
            if (!allowed.contains(i))
                continue;
            if (lastIndex >= items.size())
                break;
            inventory.setItem(i, items.get(lastIndex));
            lastIndex++;
        }
    }

    public static void removeItemFromHand(Player player, int amount) {
        ItemStack item = player.getItemInHand();
        if (item.getAmount() > amount) {
            item.setAmount(item.getAmount() - amount);
        } else {
            player.setItemInHand(new ItemStack(Material.AIR));
        }
    }

    public static void removeItemFromHand(Player player) {
        removeItemFromHand(player, 1);
    }

    public static String millisToString(long millis) {
        return Time.of(millis)
                .day(i -> i + (i == 1 ? " dia" : " dias"))
                .hour(i -> i + (i == 1 ? " hora" : " horas"))
                .min(i -> i + (i == 1 ? " minuto" : " minutos"))
                .sec(i -> i + (i == 1 ? " segundo" : " segundos"))
                .and("e").get();
    }


    public static String getSerializedLocation(Location loc) {
        if (loc == null) return null;
        return loc.getX() + ";" + loc.getY() + ";" + loc.getZ() + ";" + loc.getYaw() + ";" + loc.getPitch()
                + ";" + loc.getWorld().getName();
    }

    public static Location getDeserializedLocation(String s) {
        if (s == null || !s.contains(";")) return null;
        String[] parts = s.split(";");
        double x = Double.parseDouble(parts[0]);
        double y = Double.parseDouble(parts[1]);
        double z = Double.parseDouble(parts[2]);
        float yaw = Float.parseFloat(parts[3]);
        float pitch = Float.parseFloat(parts[4]);
        World w = Bukkit.getServer().getWorld(parts[5]);
        return new Location(w, x, y, z, yaw, pitch);
    }

    public static ItemStack fromBase64(String data) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(new BigInteger(data, 32).toByteArray());
        DataInputStream dataInputStream = new DataInputStream(inputStream);

        ItemStack itemStack = null;
        try {
            Class<?> nbtTagCompoundClass = ReflectionUtils.getNMSClass("NBTTagCompound");
            Class<?> nmsItemStackClass = ReflectionUtils.getNMSClass("ItemStack");
            Object nbtTagCompound = ReflectionUtils.getNMSClass("NBTCompressedStreamTools").getMethod("a", DataInputStream.class).invoke(null, dataInputStream);
            Object craftItemStack = nmsItemStackClass.getMethod("createStack", nbtTagCompoundClass).invoke(null, nbtTagCompound);
            itemStack = (ItemStack) ReflectionUtils.getOBClass("inventory.CraftItemStack").getMethod("asBukkitCopy", nmsItemStackClass).invoke(null, craftItemStack);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return itemStack;
    }

    public static String toBase64(ItemStack item) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutput = new DataOutputStream(outputStream);

        try {
            Class<?> nbtTagCompoundClass = ReflectionUtils.getNMSClass("NBTTagCompound");
            Constructor<?> nbtTagCompoundConstructor = nbtTagCompoundClass.getConstructor();
            Object nbtTagCompound = nbtTagCompoundConstructor.newInstance();
            Object nmsItemStack = ReflectionUtils.getOBClass("inventory.CraftItemStack").getMethod("asNMSCopy", ItemStack.class).invoke(null, item);
            ReflectionUtils.getNMSClass("ItemStack").getMethod("save", nbtTagCompoundClass).invoke(nmsItemStack, nbtTagCompound);
            ReflectionUtils.getNMSClass("NBTCompressedStreamTools").getMethod("a", nbtTagCompoundClass, DataOutput.class).invoke(null, nbtTagCompound, (DataOutput) dataOutput);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return new BigInteger(1, outputStream.toByteArray()).toString(32);
    }

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
        List<String> commands = section.getStringList("Comandos");
        ItemStack itemStack = ITEM_ADAPTER.supply(section);
        return new CommandableItem(itemStack, commands);
    });
    private final static SectionBuilder.ListAdapter<ConfigurableItem> CONFIGURABLE_ITEM_LIST_ADAPTER = new SectionBuilder.ListAdapter<>(object -> {
        ConfigurationSection section = (ConfigurationSection) object;
        double chance = section.getDouble("Chance");
        List<String> commands = section.getStringList("Comandos");
        ItemStack itemStack = ITEM_ADAPTER.supply(section);
        return new ConfigurableItem(itemStack, chance, commands);
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
            getCommands().forEach(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("$player", player.getName())));
        }

        public List<String> getCommands() {
            return commands;
        }

    }

    public static class ConfigurableItem implements Commandable, Chanceable, Item {

        private ItemStack item;
        private double chance;
        private List<String> commands;

        public ConfigurableItem(ItemStack item, double chance, List<String> commands) {
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
            getCommands().forEach(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("$player", player.getName())));
        }
    }

    public enum LogType {
        INFO, DEBUG;
    }

}
