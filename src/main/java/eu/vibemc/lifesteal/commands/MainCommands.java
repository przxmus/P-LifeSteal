package eu.vibemc.lifesteal.commands;

import dev.jorel.commandapi.CommandAPICommand;
import eu.vibemc.lifesteal.Main;
import eu.vibemc.lifesteal.other.Config;
import eu.vibemc.lifesteal.other.LootPopulator;
import org.bukkit.Bukkit;
import org.bukkit.World;

import static eu.vibemc.lifesteal.other.Items.Recipes.registerRecipes;
import static eu.vibemc.lifesteal.other.Items.Recipes.unregisterRecipes;

public class MainCommands {
    public static CommandAPICommand getMainCommands() {
        return new CommandAPICommand("lifesteal")
                .withShortDescription("Main LifeSteal command.")
                .withAliases("ls", "pls", "p-ls", "plifesteal", "p-lifesteal")
                .executes((sender, args) -> {
                    sender.sendMessage("§aP-LifeSteal");
                    sender.sendMessage("§aCreated by §6§ldevPrzemuS");
                    sender.sendMessage("§6§lhttps://github.com/dewPrzemuS/P-LifeSteal");
                    sender.sendMessage("§6§lhttps://www.spigotmc.org/resources/p-lifesteal.101967/");
                })
                .withSubcommand(getHelpCommand())
                .withSubcommand(getReloadCommand());
    }

    private static CommandAPICommand getHelpCommand() {
        return new CommandAPICommand("help")
                .withShortDescription("Help command.")
                .executes((sender, args) -> {
                    sender.sendMessage("§aAnswers to most questions can be found in the documentation:");
                    sender.sendMessage("§6§lhttps://ls.przemus.xyz/");
                    sender.sendMessage("§aIf you §c§ldon't §r§afind an answer to your question there, ask for help on discord:");
                    sender.sendMessage("§6§lhttps://discord.gg/8sjwaQTHGC/");
                });
    }

    private static CommandAPICommand getReloadCommand() {
        return new CommandAPICommand("reload")
                .withPermission("lifesteal.reload")
                .withShortDescription("Reloads config.")
                .executes((sender, args) -> {
                    unregisterRecipes();
                    for (World world : Bukkit.getServer().getWorlds()) {
                        world.getPopulators().removeIf(blockPopulator -> blockPopulator instanceof LootPopulator);
                    }
                    Main.getInstance().reloadConfig();
                    sender.sendMessage(Config.getMessage("configReloaded"));
                    registerRecipes();
                    sender.sendMessage(Config.getMessage("recipesReloaded"));
                    if (Config.getBoolean("loot.enabled")) {
                        for (World world : Bukkit.getServer().getWorlds()) {
                            world.getPopulators().add(new LootPopulator(Main.getInstance()));
                        }
                    }
                    sender.sendMessage(Config.getMessage("lootReloaded"));

                });
    }
}
