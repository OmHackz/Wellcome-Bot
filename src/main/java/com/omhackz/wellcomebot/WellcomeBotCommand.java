package com.omhackz.wellcomebot;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class WellcomeBotCommand implements CommandExecutor, TabCompleter {
    private final WellcomeBot plugin;
    private final PlayerDataStore dataStore;
    public WellcomeBotCommand(WellcomeBot plugin, PlayerDataStore dataStore) {
        this.plugin = plugin;
        this.dataStore = dataStore;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.AQUA + "WellcomeBot " + plugin.getDescription().getVersion());
            return true;
        }
        String sub = args[0].toLowerCase();
        if (sub.equals("version")) {
            sender.sendMessage(ChatColor.AQUA + "WellcomeBot " + plugin.getDescription().getVersion());
            return true;
        }
        if (sub.equals("reload")) {
            plugin.reloadConfig();
            sender.sendMessage(ChatColor.GREEN + "WellcomeBot reloaded");
            return true;
        }
        if (sub.equals("post")) {
            if (!plugin.getConfig().getBoolean("discord.enabled", false)) {
                sender.sendMessage(ChatColor.RED + "Discord webhook is disabled");
                return true;
            }
            String targetName = null;
            if (args.length >= 2) {
                targetName = args[1];
            } else if (sender instanceof Player) {
                targetName = ((Player) sender).getName();
            } else {
                sender.sendMessage(ChatColor.RED + "Usage: /" + label + " post <playername>");
                return true;
            }
            OfflinePlayer op = Bukkit.getOfflinePlayer(targetName);
            UUID uuid = op.getUniqueId();
            int joins = dataStore.getJoins(uuid);
            long play = dataStore.getPlaytimeSeconds(uuid);
            if (joins <= 0) {
                sender.sendMessage(ChatColor.RED + "No data for " + targetName);
                return true;
            }
            DiscordWebhook.postPlayer(plugin.getConfig(), uuid.toString(), op.getName() == null ? targetName : op.getName(), joins, play);
            sender.sendMessage(ChatColor.GREEN + "Posted data for " + targetName);
            return true;
        }
        sender.sendMessage(ChatColor.RED + "Unknown subcommand");
        return true;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) return Arrays.asList("version", "reload", "post");
        if (args.length == 2 && args[0].equalsIgnoreCase("post")) {
            List<String> names = new ArrayList<>();
            for (Player p : Bukkit.getOnlinePlayers()) names.add(p.getName());
            return names;
        }
        return new ArrayList<>();
    }
}
