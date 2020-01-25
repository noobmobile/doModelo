package com.dont.modelo.utils;

import com.dont.modelo.Terminal;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Utils {

    public enum LogType {
        INFO, DEBUG;
    }

    private static final Terminal main = Terminal.getPlugin(Terminal.class);
    private static final String prefix = ChatColor.GREEN + "[" + main.getName()+"] " + ChatColor.WHITE;;

    public static boolean DEBUGGING = true;
    public static void debug(LogType type, String mensagem){
        if (type == LogType.DEBUG && !DEBUGGING) return;
        Bukkit.getConsoleSender().sendMessage("["+type.name()+"] "+ prefix + mensagem);
    }


    private static final DecimalFormatSymbols DFS = new DecimalFormatSymbols(new Locale("pt", "BR"));
    public static final DecimalFormat FORMATTER = new DecimalFormat("###,###,###", DFS);
    public static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy 'as' HH:mm:ss");

    public static long measureTime(Runnable runnable){
        long before = System.currentTimeMillis();
        runnable.run();
        return System.currentTimeMillis() - before;
    }

    public static void measureTime(String mensagem, Runnable runnable, CommandSender sender){
        sender.sendMessage(mensagem.replace("<tempo>", measureTime(runnable) + ""));
    }

    public static void measureTime(String mensagem, Runnable runnable){
        measureTime(mensagem, runnable, Bukkit.getConsoleSender());
    }

    public static final List<Integer> ALLOWED = Arrays.asList(
            10,11,12,13,14,15,16,
            19,20,21,22,23,24,25,
            28,29,30,31,32,33,34);

    public static void setupItems(Inventory inventory, List<ItemStack> items){
        setupItems(inventory,items,ALLOWED);
    }

    public static void setupItems(Inventory inventory, List<ItemStack> items, List<Integer> allowed){
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

    public static List<ItemStack> getItemsByConfigurationSection(ConfigurationSection section) {
        List<ItemStack> items = new ArrayList<>();
        for (String key : section.getKeys(false)) {
            String mkey = key + ".";
            Material material = Material.valueOf(section.getString(mkey + "Material"));
            int data = section.getInt(mkey + "Data");
            int quantidade = section.getInt(mkey + "Quantidade");
            String name = ChatColor.translateAlternateColorCodes('&', section.getString(mkey + "Name"));
            List<String> lore = new ArrayList<>();
            for (String s : section.getStringList(mkey + "Lore")) {
                lore.add(ChatColor.translateAlternateColorCodes('&', s));
            }
            HashMap<Enchantment, Integer> enchants = new HashMap<Enchantment, Integer>();
            for (String s : section.getStringList(mkey + "Enchants")) {
                if (s.equalsIgnoreCase("nulo")) break;
                String[] splited = s.split(":");
                enchants.put(Enchantment.getByName(splited[0]), Integer.valueOf(splited[1]));
            }
            items.add(new ItemComposer(material, quantidade, data).setName(name).setLore(lore).addEnchantments(enchants).toItemStack());
        }
        return items;
    }

    public static String converterTempo(long millis) {
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
                + ";" + loc.getWorld().getUID();
    }

    public static Location getDeserializedLocation(String s) {
        if (s == null || !s.contains(";")) return null;
        String [] parts = s.split(";");
        double x = Double.parseDouble(parts[0]);
        double y = Double.parseDouble(parts[1]);
        double z = Double.parseDouble(parts[2]);
        float yaw = Float.parseFloat(parts[3]);
        float pitch = Float.parseFloat(parts[4]);
        UUID u = UUID.fromString(parts[5]);
        World w = Bukkit.getServer().getWorld(u);
        return new Location(w, x, y, z, yaw, pitch);
    }

}
