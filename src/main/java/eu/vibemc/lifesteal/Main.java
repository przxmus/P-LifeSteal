package eu.vibemc.lifesteal;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import eu.vibemc.lifesteal.events.PlayerDeath;
import eu.vibemc.lifesteal.events.PlayerInteract;
import eu.vibemc.lifesteal.other.Items;
import eu.vibemc.lifesteal.other.LootPopulator;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private static Main instance;

    public static Main getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        this.getConfig().options().copyDefaults();
        this.saveDefaultConfig();

        getServer().getPluginManager().registerEvents(new PlayerDeath(), this);
        getServer().getPluginManager().registerEvents(new PlayerInteract(), this);


        for (World world : Bukkit.getServer().getWorlds()) {
            world.getPopulators().removeIf(blockPopulator -> blockPopulator instanceof LootPopulator);
            world.getPopulators().add(new LootPopulator(this));
        }



        new CommandAPICommand("lifesteal")
                .withShortDescription("Main LifeSteal command.")
                .withAliases("ls", "pls", "p-ls", "plifesteal", "p-lifesteal")
                .executes((sender, args) -> {
                    sender.sendMessage("     ");
                    sender.sendMessage("   ");
                    sender.sendMessage("      ");
                    sender.sendMessage("§aP-LifeSteal");
                    sender.sendMessage("§aCreated by §6§ldevPrzemuS");
                    sender.sendMessage("§6§lhttps://youtube.com/devPrzemuS");
                    sender.sendMessage("§6§lhttps://github.com/dewPrzemuS");
                    sender.sendMessage("§6§lhttps://www.spigotmc.org/members/devprzemus.1445270/");
                    sender.sendMessage("     ");
                    sender.sendMessage("          ");
                    sender.sendMessage("               ");
                })
                .withSubcommand(new CommandAPICommand("reload")
                        .withPermission("lifesteal.reload")
                        .withShortDescription("Reloads config.")
                        .executes((sender, args) -> {
                            Main.getInstance().reloadConfig();
                        })
                ).register();
    }


    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
