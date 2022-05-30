package eu.vibemc.lifesteal.bans;

import com.google.gson.Gson;
import eu.vibemc.lifesteal.Main;
import eu.vibemc.lifesteal.bans.models.Ban;
import eu.vibemc.lifesteal.other.Config;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class BanStorageUtil {

    private static ArrayList<Ban> bans = new ArrayList<>();

    public static Ban createBan(Player player) throws IOException {
        if (getBan(player.getUniqueId()) != null) {
            return null;
        }
        Ban ban = new Ban(player.getUniqueId());
        bans.add(ban);
        saveBans();
        // bukkit wait 2 seconds
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
            if (player.isOnline()) {
                if (Config.getBoolean("banOn0Hearts")) {
                    player.kickPlayer(Config.getMessage("noMoreHeartsBan"));
                    if (Config.getBoolean("broadcastBanFrom0Hearts")) {
                        player.getServer().broadcastMessage(Config.getMessage("bannedNoMoreHeartsBroadcast").replace("${player}", player.getName()));
                    }
                }
            }
        }, 10L);

        return ban;
    }


    public static OfflinePlayer getOfflinePlayerByBan(Ban ban) {
        return Main.getInstance().getServer().getOfflinePlayer(ban.getPlayerUUID());
    }

    public static Ban getBan(UUID uuid) {
        for (Ban ban : bans) {
            if (ban.getPlayerUUID().equals(uuid)) {
                return ban;
            }
        }
        return null;
    }

    public static boolean deleteBan(UUID uuid) throws IOException {
        if (getBan(uuid) == null) {
            return false;
        }
        bans.remove(getBan(uuid));
        saveBans();
        return true;
    }

    public static void saveBans() throws IOException {
        Gson gson = new Gson();
        File file = new File(Main.getInstance().getDataFolder().getAbsolutePath() + "/bans.json");
        file.getParentFile().mkdir();
        file.createNewFile();
        Writer writer = null;
        writer = new FileWriter(file, false);
        gson.toJson(bans, writer);
        writer.flush();
        writer.close();
    }

    public static void loadBans() throws IOException {

        Gson gson = new Gson();
        File file = new File(Main.getInstance().getDataFolder().getAbsolutePath() + "/bans.json");
        if (file.exists()) {
            Reader reader = new FileReader(file);
            Ban[] b = gson.fromJson(reader, Ban[].class);
            bans = new ArrayList<>(Arrays.asList(b));
        }

    }

    public static List<Ban> findAllBans() {
        return bans;
    }
}
