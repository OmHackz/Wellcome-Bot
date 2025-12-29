package com.omhackz.wellcomebot;

import org.bukkit.configuration.file.FileConfiguration;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class DiscordWebhook {
    public static void postJoin(FileConfiguration cfg, String uuid, String username, int joins, long playSeconds) {
        boolean enabled = cfg.getBoolean("discord.enabled", false);
        String url = cfg.getString("discord.webhook_url", "");
        if (!enabled || url == null || url.isEmpty()) return;
        String title = cfg.getString("discord.embed_title", "Player Joined");
        int color = cfg.getInt("discord.color", 3447003);
        String prefix = cfg.getString("discord.content_prefix", "");
        String ordinal = ordinal(joins);
        String play = formatPlaytime(playSeconds);
        String iso = DateTimeFormatter.ISO_INSTANT.format(Instant.now().atOffset(ZoneOffset.UTC));
        String json = "{\"content\":\"" + escape(prefix) + "\",\"embeds\":[{\"title\":\"" + escape(title) + "\",\"color\":" + color + ",\"fields\":["
                + field("UUID", uuid, true) + ","
                + field("Username", username, true) + ","
                + field("Playtime", play, true) + ","
                + field("Login #", ordinal, true)
                + "],\"timestamp\":\"" + iso + "\"}]}";
        post(url, json);
    }
    public static void postPlayer(FileConfiguration cfg, String uuid, String username, int joins, long playSeconds) {
        boolean enabled = cfg.getBoolean("discord.enabled", false);
        String url = cfg.getString("discord.webhook_url", "");
        if (!enabled || url == null || url.isEmpty()) return;
        String title = "Player Data";
        int color = cfg.getInt("discord.color", 3447003);
        String prefix = cfg.getString("discord.content_prefix", "");
        String ordinal = ordinal(joins);
        String play = formatPlaytime(playSeconds);
        String iso = DateTimeFormatter.ISO_INSTANT.format(Instant.now().atOffset(ZoneOffset.UTC));
        String json = "{\"content\":\"" + escape(prefix) + "\",\"embeds\":[{\"title\":\"" + escape(title) + "\",\"color\":" + color + ",\"fields\":["
                + field("UUID", uuid, true) + ","
                + field("Username", username, true) + ","
                + field("Playtime", play, true) + ","
                + field("Login #", ordinal, true)
                + "],\"timestamp\":\"" + iso + "\"}]}";
        post(url, json);
    }
    private static String field(String name, String value, boolean inline) {
        return "{\"name\":\"" + escape(name) + "\",\"value\":\"" + escape(value) + "\",\"inline\":" + inline + "}";
    }
    private static String escape(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }
    private static void post(String webhookUrl, String json) {
        try {
            URL u = new URL(webhookUrl);
            HttpURLConnection conn = (HttpURLConnection) u.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(10000);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
            conn.setRequestProperty("Content-Length", String.valueOf(bytes.length));
            try (OutputStream os = conn.getOutputStream()) {
                os.write(bytes);
            }
            conn.getInputStream().close();
            conn.disconnect();
        } catch (Exception ignored) {}
    }
    private static String ordinal(int n) {
        int mod100 = n % 100;
        int mod10 = n % 10;
        String suffix = (mod100 - mod10 == 10) ? "th" : mod10 == 1 ? "st" : mod10 == 2 ? "nd" : mod10 == 3 ? "rd" : "th";
        return n + suffix;
    }
    private static String formatPlaytime(long seconds) {
        long h = seconds / 3600;
        long m = (seconds % 3600) / 60;
        long s = seconds % 60;
        return String.format("%02d:%02d:%02d", h, m, s);
    }
}
