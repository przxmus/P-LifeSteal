package eu.vibemc.lifesteal;

import com.samjakob.spigui.SpiGUI;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIConfig;
import eu.vibemc.lifesteal.bans.BanStorageUtil;
import eu.vibemc.lifesteal.events.AsyncPlayerPreLogin;
import eu.vibemc.lifesteal.events.PlayerDeath;
import eu.vibemc.lifesteal.events.PlayerInteract;
import eu.vibemc.lifesteal.other.Config;
import eu.vibemc.lifesteal.other.LootPopulator;
import eu.vibemc.lifesteal.other.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

import static eu.vibemc.lifesteal.commands.CommandsManager.loadCommands;
import static eu.vibemc.lifesteal.other.Items.Recipes.registerRecipes;
import static eu.vibemc.lifesteal.other.Items.Recipes.unregisterRecipes;

public final class Main extends JavaPlugin {

    public static SpiGUI spiGUI;
    private static Main instance;

    public static Main getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        instance = this;
        CommandAPI.onLoad(new CommandAPIConfig().silentLogs(false));
        try {
            BanStorageUtil.loadBans();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        loadCommands();
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        Main.spiGUI = new SpiGUI(this);
        CommandAPI.onEnable(this);
        Metrics metrics = new Metrics(this, 15176);
        this.getConfig().options().copyDefaults();
        this.saveDefaultConfig();

        getServer().getPluginManager().registerEvents(new PlayerDeath(), this);
        getServer().getPluginManager().registerEvents(new PlayerInteract(), this);
        getServer().getPluginManager().registerEvents(new AsyncPlayerPreLogin(), this);

        if (Config.getBoolean("loot.enabled")) {
            for (World world : Bukkit.getServer().getWorlds()) {
                world.getPopulators().removeIf(blockPopulator -> blockPopulator instanceof LootPopulator);
                world.getPopulators().add(new LootPopulator(this));
            }
        }

        registerRecipes();
    }


    @Override
    public void onDisable() {
        // Plugin shutdown logic
        unregisterRecipes();
        for (World world : Bukkit.getServer().getWorlds()) {
            world.getPopulators().removeIf(blockPopulator -> blockPopulator instanceof LootPopulator);
        }
    }
}
