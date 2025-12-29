package com.omhackz.wellcomebot;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.configuration.file.FileConfiguration;

public class JoinListener implements Listener {
    private final WellcomeBot plugin;
    private final PlayerDataStore dataStore;
    public JoinListener(WellcomeBot plugin, PlayerDataStore dataStore) {
        this.plugin = plugin;
        this.dataStore = dataStore;
    }
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        boolean isFirst = !dataStore.exists(p.getUniqueId());
        String msgKey = isFirst ? "messages.first_join" : "messages.return_join";
        String raw = plugin.getConfig().getString(msgKey, "&bWellcome %playername%");
        String colored = ChatColor.translateAlternateColorCodes('&', raw.replace("%playername%", p.getName()));
        p.sendMessage(colored);
        if (isFirst) {
            giveReward(p, "rewards.first_join.item", "rewards.first_join.amount", Material.DIAMOND, 5);
        } else {
            giveReward(p, "rewards.return_join.item", "rewards.return_join.amount", Material.IRON_INGOT, 1);
        }
        dataStore.recordLogin(p.getUniqueId(), p.getName(), System.currentTimeMillis());
        if (plugin.getConfig().getBoolean("discord.enabled", false) && plugin.getConfig().getBoolean("settings.post_on_join", true)) {
            int joins = dataStore.getJoins(p.getUniqueId());
            long play = dataStore.getPlaytimeSeconds(p.getUniqueId());
            DiscordWebhook.postJoin(plugin.getConfig(), p.getUniqueId().toString(), p.getName(), joins, play);
        }
    }
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        dataStore.recordQuit(p.getUniqueId(), System.currentTimeMillis());
    }
    private void giveReward(Player p, String itemPath, String amountPath, Material defMat, int defAmt) {
        FileConfiguration cfg = plugin.getConfig();
        String itemStr = cfg.getString(itemPath, defMat.name());
        int amt = Math.max(1, cfg.getInt(amountPath, defAmt));
        Material mat = Material.matchMaterial(itemStr);
        if (mat == null) mat = defMat;
        ItemStack stack = new ItemStack(mat, amt);
        p.getInventory().addItem(stack);
    }
}
