package com.omhackz.wellcomebot;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class WellcomeBot extends JavaPlugin {
    private static WellcomeBot instance;
    private PlayerDataStore dataStore;
    public static WellcomeBot get() {
        return instance;
    }
    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        dataStore = new PlayerDataStore(this);
        dataStore.load();
        Bukkit.getPluginManager().registerEvents(new JoinListener(this, dataStore), this);
        if (getCommand("wellcomebot") != null) {
            WellcomeBotCommand cmd = new WellcomeBotCommand(this, dataStore);
            getCommand("wellcomebot").setExecutor(cmd);
            getCommand("wellcomebot").setTabCompleter(cmd);
        }
    }
    @Override
    public void onDisable() {
        if (dataStore != null) dataStore.save();
    }
}
