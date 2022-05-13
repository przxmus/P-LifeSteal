package eu.vibemc.lifesteal.events;

import eu.vibemc.lifesteal.other.Items;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteract implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Action action = e.getAction();
        try {
            if (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR) {
                if (Items.Heart.isHeart(player.getInventory().getItemInMainHand())) {
                    Items.Heart.useHeart(player, player.getInventory().getItemInMainHand());
                }
            }
        } catch (NullPointerException ignored) {
        }


    }
}
