package eu.vibemc.lifesteal.events;

import eu.vibemc.lifesteal.Main;
import eu.vibemc.lifesteal.other.Config;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.IOException;

public class PlayerQuit implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) throws IOException {
        Player player = e.getPlayer();
        if (Config.getBoolean("recipe.enabled")) {
            Main.getInstance().getConfig().getConfigurationSection("recipe.recipes").getKeys(false).forEach(recipe -> {
                if (Config.getBoolean("recipe.recipes." + recipe + ".recipe-enabled")) {
                    if (Config.getBoolean("recipe.recipes." + recipe + ".discover")) {
                        String itemName = Config.getString("recipe.recipes." + recipe + ".item");
                        player.undiscoverRecipe(new NamespacedKey("lifesteal", itemName + recipe));
                    }
                }
            });
        }
    }
}