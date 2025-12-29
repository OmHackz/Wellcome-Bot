package com.omhackz.wellcomebot;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class PlayerDataStore {
    private final WellcomeBot plugin;
    private final File file;
    private FileConfiguration config;
    public PlayerDataStore(WellcomeBot plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "data.yml");
    }
    public void load() {
        if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ignored) {}
        }
        config = YamlConfiguration.loadConfiguration(file);
    }
    public void save() {
        try {
            if (config != null) config.save(file);
        } catch (IOException ignored) {}
    }
    public boolean exists(UUID uuid) {
        return config.getConfigurationSection("players." + uuid) != null;
    }
    public int getJoins(UUID uuid) {
        return config.getInt("players." + uuid + ".joins", 0);
    }
    public long getPlaytimeSeconds(UUID uuid) {
        return config.getLong("players." + uuid + ".playtime_seconds", 0L);
    }
    public void recordLogin(UUID uuid, String name, long millis) {
        String base = "players." + uuid + ".";
        config.set(base + "name", name);
        int joins = getJoins(uuid) + 1;
        config.set(base + "joins", joins);
        config.set(base + "last_login_at", millis);
        save();
    }
    public void recordQuit(UUID uuid, long millis) {
        String base = "players." + uuid + ".";
        long lastLogin = config.getLong(base + "last_login_at", millis);
        long session = Math.max(0L, (millis - lastLogin) / 1000L);
        long total = getPlaytimeSeconds(uuid) + session;
        config.set(base + "playtime_seconds", total);
        config.set(base + "last_logout_at", millis);
        save();
    }
}
