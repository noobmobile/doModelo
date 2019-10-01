package com.dont.modelo.utils;

import com.dont.modelo.Terminal;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
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
    private static final String prefix = ChatColor.AQUA + "[" + main.getName()+"] " + ChatColor.WHITE;;

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
            items.add(new ItemBuilder(material, quantidade, data).setName(name).setLore(lore).addEnchantments(enchants).toItemStack());
        }
        return items;
    }

    public static String converterTempo(long millis) {
        long secondsIn = millis / 1000l;
        long dayCount = TimeUnit.SECONDS.toDays(secondsIn);
        long secondsCount = secondsIn - TimeUnit.DAYS.toSeconds(dayCount);
        long hourCount = TimeUnit.SECONDS.toHours(secondsCount);
        secondsCount -= TimeUnit.HOURS.toSeconds(hourCount);
        long minutesCount = TimeUnit.SECONDS.toMinutes(secondsCount);
        secondsCount -= TimeUnit.MINUTES.toSeconds(minutesCount);
        StringBuilder sb = new StringBuilder();
        if (dayCount!=0) sb.append(String.format("%d %s, ", dayCount, (dayCount == 1) ? "dia"
                : "dias"));
        if (hourCount!=0) sb.append(String.format("%d %s, ", hourCount, (hourCount == 1) ? "hora"
                : "horas"));
        if (minutesCount!=0) sb.append(String.format("%d %s e ", minutesCount,
                (minutesCount == 1) ? "minuto" : "minutos"));
        if (secondsCount!=0) sb.append(String.format("%d %s.", secondsCount,
                (secondsCount == 1) ? "segundo" : "segundos"));
        return sb.toString();
    }


    public static boolean DEBUGGING = true;
    public static void debug(LogType type, String mensagem){
        if (type == LogType.DEBUG && !DEBUGGING) return;
        Bukkit.getConsoleSender().sendMessage(prefix + mensagem);
    }

    public static String getSerializedLocation(Location loc) {
        return loc.getX() + ";" + loc.getY() + ";" + loc.getZ() + ";" + loc.getYaw() + ";" + loc.getPitch()
                + ";" + loc.getWorld().getUID();
    }

    public static Location getDeserializedLocation(String s) {
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
