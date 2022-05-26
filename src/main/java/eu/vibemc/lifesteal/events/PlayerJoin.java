package eu.vibemc.lifesteal.events;

import eu.vibemc.lifesteal.Main;
import eu.vibemc.lifesteal.other.Config;
import eu.vibemc.lifesteal.other.UpdateChecker;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.IOException;

public class PlayerJoin implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) throws IOException {
        Player player = e.getPlayer();
        if (player.hasPermission("lifesteal.update") || player.isOp()) {
            new UpdateChecker(Main.getInstance()).getVersion(version -> {
                player.sendMessage("§a§lP-LifeSteal §7§l> §c§lA NEW UPDATE HAS BEEN RELEASED! §6(" + version + ")");
            });
        }
        if (Config.getBoolean("recipe.enabled")) {
            Main.getInstance().getConfig().getConfigurationSection("recipe.recipes").getKeys(false).forEach(recipe -> {
                if (Config.getBoolean("recipe.recipes." + recipe + ".recipe-enabled")) {
                    if (Config.getBoolean("recipe.recipes." + recipe + ".discover")) {
                        String itemName = Config.getString("recipe.recipes." + recipe + ".item");
                        player.discoverRecipe(new NamespacedKey("lifesteal", itemName + recipe));
                    }
                }
            });
        }
    }
}