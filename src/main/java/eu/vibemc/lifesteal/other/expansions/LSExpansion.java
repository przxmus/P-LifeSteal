package eu.vibemc.lifesteal.other.expansions;

import eu.vibemc.lifesteal.Main;
import eu.vibemc.lifesteal.bans.BanStorageUtil;
import eu.vibemc.lifesteal.bans.models.Ban;
import eu.vibemc.lifesteal.other.Config;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class LSExpansion extends PlaceholderExpansion {

    private final Main plugin;

    public LSExpansion(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "ls";
    }

    @Override
    public @NotNull String getAuthor() {
        return "devPrzemuS (P-LifeSteal)";
    }

    @Override
    public boolean persist() {
        return true; // This is required or else PlaceholderAPI will unregister the Expansion on reload
    }

    @Override
    public @NotNull String getVersion() {
        return Main.getInstance().getDescription().getVersion();
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (params.startsWith("hearts_")) {
            try {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(params.substring(7));
                // if offline player exists
                if (offlinePlayer != null) {
                    return String.valueOf((int) offlinePlayer.getPlayer().getMaxHealth() / 2);
                }
            } catch (NullPointerException ignored) {

            }

        }
        if (params.startsWith("health_")) {
            try {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(params.substring(7));
                // if offline player exists
                if (offlinePlayer != null) {
                    return String.valueOf((int) offlinePlayer.getPlayer().getMaxHealth());
                }
            } catch (NullPointerException ignored) {

            }

        }

        if (params.startsWith("banned_")) {
            try {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(params.substring(7));
                // if offline player exists
                if (offlinePlayer != null) {
                    if (BanStorageUtil.getBan(offlinePlayer.getUniqueId()) != null) {
                        return ChatColor.translateAlternateColorCodes('&', Config.getString("placeholder-api.banned-text"));
                    }
                    else {
                        return ChatColor.translateAlternateColorCodes('&', Config.getString("placeholder-api.not-banned-text"));
                    }
                }
            } catch (NullPointerException ignored) {
            } catch (IOException ignored) {
            }

        }

        return "notfound"; // Placeholder is unknown by the Expansion
    }
}
