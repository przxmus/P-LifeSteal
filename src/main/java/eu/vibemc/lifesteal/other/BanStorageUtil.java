package eu.vibemc.lifesteal.other;

import com.google.gson.Gson;
import eu.vibemc.lifesteal.Main;
import eu.vibemc.lifesteal.models.Ban;
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
        if (player.isOnline()) {
            if (Config.getBoolean("banOn0Hearts")) {
                player.kickPlayer(Config.getMessage("noMoreHeartsBan"));
                if (Config.getBoolean("broadcastBanFrom0Hearts")) {
                    player.getServer().broadcastMessage(Config.getMessage("bannedNoMoreHeartsBroadcast").replace("${player}", player.getName()));
                }
            }
        }
        return ban;
    }

    public static Ban getBan(UUID uuid) {
        for (Ban ban : bans) {
            if (ban.getPlayerUUID().equals(uuid)) {
                return ban;
            }
        }
        return null;
    }

    public static void deleteBan(UUID uuid) throws IOException {
        bans.remove(getBan(uuid));
        saveBans();
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

    public static void loadNotes() throws IOException {

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
