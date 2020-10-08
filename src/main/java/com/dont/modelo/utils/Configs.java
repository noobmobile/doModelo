package com.dont.modelo.utils;

import com.dont.modelo.Terminal;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.nio.charset.StandardCharsets;

public enum Configs {

    MENSAGENS, ITENS;

    private RawConfig config;

    Configs() {
        this.config = new RawConfig(Terminal.getInstance(), this.name().toLowerCase() + ".yml");
    }

    /**
     * @return the config
     */

    public static void setup() {
        for (Configs cfg : Configs.values()) {
            cfg.getRawConfig().saveDefaultConfig();
        }
    }

    public RawConfig getRawConfig() {
        return config;
    }

    public FileConfiguration getConfig() {
        return config.getConfig();
    }

    public void saveConfig() {
        this.config.saveConfig();
    }

    public class RawConfig {

        private Plugin plugin;
        private String configName;
        private File configFile;
        private FileConfiguration config;

        public RawConfig(Plugin plugin, String fileName) {
            this.plugin = plugin;
            this.configName = fileName;
            File dataFolder = plugin.getDataFolder();
            this.configFile = new File(dataFolder.toString() + File.separatorChar + this.configName);
        }

        public void reloadConfig() {
            try {
                this.config = YamlConfiguration.loadConfiguration(new InputStreamReader(new FileInputStream(this.configFile), StandardCharsets.UTF_8));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            InputStream is = this.plugin.getResource(this.configName);
            if (is != null) {
                YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(is);
                this.config.setDefaults(defConfig);
            }
        }

        public FileConfiguration getConfig() {
            if (this.config == null) {
                reloadConfig();
            }
            return this.config;
        }

        public void saveConfig() {
            if ((this.config == null) || (this.configFile == null)) {
                return;
            }
            try {
                getConfig().save(this.configFile);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        public void saveDefaultConfig() {
            if (!this.configFile.exists()) {
                this.plugin.saveResource(this.configName, false);
            }
        }
    }

}