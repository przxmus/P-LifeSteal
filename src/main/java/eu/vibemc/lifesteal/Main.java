package eu.vibemc.lifesteal;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import eu.vibemc.lifesteal.events.PlayerDeath;
import eu.vibemc.lifesteal.events.PlayerInteract;
import eu.vibemc.lifesteal.other.Config;
import eu.vibemc.lifesteal.other.Items;
import eu.vibemc.lifesteal.other.LootPopulator;
import eu.vibemc.lifesteal.other.Metrics;
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
        Metrics metrics = new Metrics(this, 15176);
        instance = this;
        this.getConfig().options().copyDefaults();
        this.saveDefaultConfig();

        getServer().getPluginManager().registerEvents(new PlayerDeath(), this);
        getServer().getPluginManager().registerEvents(new PlayerInteract(), this);


        if (Config.getBoolean("loot.enabled")) {
            for (World world : Bukkit.getServer().getWorlds()) {
                world.getPopulators().removeIf(blockPopulator -> blockPopulator instanceof LootPopulator);
                world.getPopulators().add(new LootPopulator(this));
            }
        }


        new CommandAPICommand("lifesteal")
                .withShortDescription("Main LifeSteal command.")
                .withAliases("ls", "pls", "p-ls", "plifesteal", "p-lifesteal")
                .executes((sender, args) -> {
                    sender.sendMessage("      ");
                    sender.sendMessage("§aP-LifeSteal");
                    sender.sendMessage("§aCreated by §6§ldevPrzemuS");
                    sender.sendMessage("§6§lhttps://youtube.com/devPrzemuS");
                    sender.sendMessage("§6§lhttps://github.com/dewPrzemuS");
                    sender.sendMessage("§6§lhttps://www.spigotmc.org/resources/p-lifesteal.101967/");
                    sender.sendMessage("     ");
                })
                .withSubcommand(new CommandAPICommand("reload")
                        .withPermission("lifesteal.reload")
                        .withShortDescription("Reloads config.")
                        .executes((sender, args) -> {
                            Main.getInstance().reloadConfig();
                            sender.sendMessage(Config.getMessage("configReloaded"));
                        }))
                .withSubcommand(new CommandAPICommand("give")
                        .withShortDescription("Gives you specified item.")
                        .withArguments(new StringArgument("item").replaceSuggestions(ArgumentSuggestions.strings("extra_life")), new PlayerArgument("player"), new IntegerArgument("chance_of_success"), new IntegerArgument("amount"))
                        .executes((sender, args) -> {
                            String item = (String) args[0];
                            Player player = (Player) args[1];
                            int chance = (int) args[2];
                            int amount = (int) args[3];
                            if (item.equalsIgnoreCase("extra_life")) {
                                if (!player.hasPermission("lifesteal.give.extraheart")) {
                                    return;
                                }
                                // loop amount times
                                for (int i = 0; i < amount; i++) {
                                    player.getInventory().addItem(Items.Heart.getHeartItem(chance));
                                    player.updateInventory();
                                }
                            }
                        })
                ).register();
    }


    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
