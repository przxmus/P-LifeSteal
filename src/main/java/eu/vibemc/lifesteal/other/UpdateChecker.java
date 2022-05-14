package eu.vibemc.lifesteal.other;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

// From: https://www.spigotmc.org/wiki/creating-an-update-checker-that-checks-for-updates
public class UpdateChecker {

    private final JavaPlugin plugin;

    public UpdateChecker(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void getVersion(final Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            // make api get request to https://api.spigotmc.org/simple/0.2/index.php?action=getResource&id=101967 then get response json
            try {
                URL url = new URL("https://api.github.com/repos/dewPrzemuS/P-LifeSteal/releases");
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                String response = new String(connection.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                JSONParser parser = new JSONParser();
                JSONArray array = (JSONArray) parser.parse(response);
                JSONObject json = (JSONObject) array.get(0);
                consumer.accept(String.valueOf(json.get("tag_name")));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
    }
}
 