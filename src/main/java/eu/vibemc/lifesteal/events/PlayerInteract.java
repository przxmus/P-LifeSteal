package eu.vibemc.lifesteal.events;

import eu.vibemc.lifesteal.other.Items;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.io.IOException;

public class PlayerInteract implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) throws IOException {
        Player player = e.getPlayer();
        Action action = e.getAction();
        try {
            if (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR) {
                if (Items.ExtraHeart.isExtraHeart(player.getInventory().getItemInMainHand())) {
                    Items.ExtraHeart.useExtraHeart(player, player.getInventory().getItemInMainHand());
                } else if (Items.ReviveBook.isReviveBook(player.getInventory().getItemInMainHand())) {
                    Items.ReviveBook.useReviveBook(player, player.getInventory().getItemInMainHand());
                }
            }
        } catch (NullPointerException ignored) {
        }

    }
}
