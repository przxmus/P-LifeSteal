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
        if (BanStorageUtil.getBan(player.getUniqueId()) != null) {
            return null;
        }
        Ban createdBan;
        if (Config.getInt("banTime") > 0) {
            int banTime = Config.getInt("banTime") * 60;
            long unixTime = System.currentTimeMillis() / 1000L + banTime;
            Ban ban = new Ban(player.getUniqueId(), unixTime);
            BanStorageUtil.bans.add(ban);
            BanStorageUtil.saveBans();
            createdBan = ban;
        } else {
            Ban ban = new Ban(player.getUniqueId(), 5283862620L);
            BanStorageUtil.bans.add(ban);
            BanStorageUtil.saveBans();
            createdBan = ban;
        }
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

        return createdBan;
    }


    public static OfflinePlayer getOfflinePlayerByBan(Ban ban) {
        return Main.getInstance().getServer().getOfflinePlayer(ban.getPlayerUUID());
    }

    public static Ban getBan(UUID uuid) throws IOException {
        for (Ban ban : BanStorageUtil.bans) {
            if (ban.getPlayerUUID().equals(uuid)) {
                // check if ban is still valid
                if (ban.getUnbanTime() > System.currentTimeMillis() / 1000L) {
                    return ban;
                } else {
                    BanStorageUtil.bans.remove(ban);
                    BanStorageUtil.saveBans();
                    return null;
                }
            }
        }
        return null;
    }

    public static boolean deleteBan(UUID uuid) throws IOException {
        if (BanStorageUtil.getBan(uuid) == null) {
            return false;
        }
        BanStorageUtil.bans.remove(BanStorageUtil.getBan(uuid));
        BanStorageUtil.saveBans();
        return true;
    }

    public static void saveBans() throws IOException {
        Gson gson = new Gson();
        File file = new File(Main.getInstance().getDataFolder().getAbsolutePath() + "/bans.json");
        file.getParentFile().mkdir();
        file.createNewFile();
        Writer writer = null;
        writer = new FileWriter(file, false);
        gson.toJson(BanStorageUtil.bans, writer);
        writer.flush();
        writer.close();
    }

    public static void loadBans() throws IOException {
        Gson gson = new Gson();
        File file = new File(Main.getInstance().getDataFolder().getAbsolutePath() + "/bans.json");
        if (file.exists()) {
            Reader reader = new FileReader(file);
            Ban[] b = gson.fromJson(reader, Ban[].class);
            BanStorageUtil.bans = new ArrayList<>(Arrays.asList(b));
        }
    }

    public static List<Ban> findAllBans() {
        return BanStorageUtil.bans;
    }
}
