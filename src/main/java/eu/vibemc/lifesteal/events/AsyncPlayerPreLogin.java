package eu.vibemc.lifesteal.events;

import eu.vibemc.lifesteal.other.BanStorageUtil;
import eu.vibemc.lifesteal.other.Config;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.UUID;

public class AsyncPlayerPreLogin implements Listener {

    @EventHandler
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent e) throws IOException {
        @NotNull UUID uuid = e.getUniqueId();
        if (BanStorageUtil.getBan(uuid) != null) {
            if (Config.getBoolean("banOn0Hearts")) {
                e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, Config.textComponentFromString(Config.getMessage("noMoreHeartsBan")));
            }
        }
    }


}