package eu.vibemc.lifesteal.events;

import eu.vibemc.lifesteal.other.Config;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeath implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();
        Player killer = player.getKiller();
        if (Config.getBoolean("removeHeartOnlyIfKilledByPlayer")) {
            if (killer != null) {
                if (player.getMaxHealth() - 2 <= 0) {
                    if (Config.getBoolean("banOn0Hearts")) {
                        // ban player
                        player.banPlayerIP(Config.getMessage("noMoreHeartsBan"));
                        if (Config.getBoolean("broadcastBanFrom0Hearts")) {
                            // send message to all players
                            player.getServer().broadcastMessage(Config.getMessage("bannedNoMoreHeartsBroadcast"));
                        }
                    }
                } else {
                    // remove 2 from max health of killed player
                    player.setMaxHealth(player.getMaxHealth() - 2);
                    // send actionbar to killed player
                    player.sendActionBar(Config.getMessage("heartLost"));
                    // send thunder sound to killed player
                    player.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 100, 2);
                }
                // add 2 to max health of killer
                killer.setMaxHealth(killer.getMaxHealth() + 2);
                // send actionbar to killer
                player.sendActionBar(Config.getMessage("heartGained"));
                // send level up sound to killer
                killer.playSound(killer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 100, 1);
            } else {
                return;
            }
        } else {
            if (player.getMaxHealth() - 2 <= 0) {
                if (Config.getBoolean("banOn0Hearts")) {
                    // ban player
                    player.banPlayerIP(Config.getMessage("noMoreHeartsBan"));
                    if (Config.getBoolean("broadcastBanFrom0Hearts")) {
                        // send message to all players
                        player.getServer().broadcastMessage(Config.getMessage("bannedNoMoreHeartsBroadcast"));
                    }
                }
            } else {
                // remove 2 from max health of killed player
                player.setMaxHealth(player.getMaxHealth() - 2);
                // send actionbar to killed player
                player.sendActionBar(Config.getMessage("heartLost"));
                // send thunder sound to killed player
                player.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 100, 2);
            }
        }


    }
}
