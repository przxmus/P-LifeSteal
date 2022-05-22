package eu.vibemc.lifesteal;

import com.samjakob.spigui.SpiGUI;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandAPIConfig;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.OfflinePlayerArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import eu.vibemc.lifesteal.bans.BanStorageUtil;
import eu.vibemc.lifesteal.bans.models.Ban;
import eu.vibemc.lifesteal.events.AsyncPlayerPreLogin;
import eu.vibemc.lifesteal.events.InventoryClick;
import eu.vibemc.lifesteal.events.PlayerDeath;
import eu.vibemc.lifesteal.events.PlayerInteract;
import eu.vibemc.lifesteal.other.Config;
import eu.vibemc.lifesteal.other.Items;
import eu.vibemc.lifesteal.other.LootPopulator;
import eu.vibemc.lifesteal.other.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

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

        new CommandAPICommand("lifesteal")
                .withShortDescription("Main LifeSteal command.")
                .withAliases("ls", "pls", "p-ls", "plifesteal", "p-lifesteal")
                .executes((sender, args) -> {
                    sender.sendMessage("§aP-LifeSteal");
                    sender.sendMessage("§aCreated by §6§ldevPrzemuS");
                    sender.sendMessage("§6§lhttps://github.com/dewPrzemuS/P-LifeSteal");
                    sender.sendMessage("§6§lhttps://www.spigotmc.org/resources/p-lifesteal.101967/");
                })
                .withSubcommand(new CommandAPICommand("bans")
                        .withPermission("lifesteal.bans")
                        .executes((sender, args) -> {
                            sender.sendMessage("§6§lBans:");
                            for (Ban ban : BanStorageUtil.findAllBans()) {
                                sender.sendMessage("§c" + getServer().getOfflinePlayer(ban.getPlayerUUID()).getName());
                            }
                        })
                        .withSubcommand(new CommandAPICommand("remove")
                                .withPermission("lifesteal.bans.remove")
                                .withArguments(new OfflinePlayerArgument("player"))
                                .executes((sender, args) -> {
                                    OfflinePlayer player = (OfflinePlayer) args[0];
                                    try {
                                        if (player.getName() == null) {
                                            sender.sendMessage(Config.getMessage("playerNotFound"));
                                        } else {
                                            if (BanStorageUtil.deleteBan(player.getUniqueId())) {
                                                sender.sendMessage(Config.getMessage("banRemoved").replace("${player}", player.getName()));

                                            } else {
                                                sender.sendMessage(Config.getMessage("playerNotBanned"));
                                            }
                                        }
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                })))
                .withSubcommand(new CommandAPICommand("help")
                        .withShortDescription("Help command.")
                        .executes((sender, args) -> {
                            sender.sendMessage("§aAnswers to most questions can be found in the documentation:");
                            sender.sendMessage("§6§lhttps://ls.przemus.xyz/");
                            sender.sendMessage("§aIf you §c§ldon't §r§afind an answer to your question there, ask for help on discord:");
                            sender.sendMessage("§6§lhttps://discord.gg/8sjwaQTHGC/");
                        }))
                .withSubcommand(new CommandAPICommand("hearts")
                        .withShortDescription("Command to manage hearts.")
                        .withSubcommand(new CommandAPICommand("check")
                                .withPermission("lifesteal.heart.check")
                                .withShortDescription("Check how many hearts player have.")
                                .withArguments(new PlayerArgument("player"))
                                .executes((sender, args) -> {
                                    Player player = (Player) args[0];
                                    int amount = (int) player.getMaxHealth();
                                    sender.sendMessage(Config.getMessage("heartCheck").replace("${amount}", String.valueOf(amount)).replace("${player}", player.getName()));
                                }))
                        .withSubcommand(new CommandAPICommand("add")
                                .withPermission("lifesteal.heart.manage")
                                .withArguments(new PlayerArgument("player"), new IntegerArgument("amount"))
                                .withShortDescription("Add hearts to player.")
                                .executes((sender, args) -> {
                                    Player player = (Player) args[0];
                                    int amount = (int) args[1];
                                    player.setMaxHealth(player.getMaxHealth() + amount);
                                    player.sendMessage(Config.getMessage("heartAdded").replace("${amount}", String.valueOf(amount)));
                                    sender.sendMessage(Config.getMessage("heartAddedAdmin").replace("${amount}", String.valueOf(amount)).replace("${player}", player.getName()));
                                }))
                        .withSubcommand(new CommandAPICommand("remove")
                                .withPermission("lifesteal.heart.manage")
                                .withArguments(new PlayerArgument("player"), new IntegerArgument("amount"))
                                .withShortDescription("Removes hearts from player.")
                                .executes((sender, args) -> {
                                    Player player = (Player) args[0];
                                    int amount = (int) args[1];
                                    player.setMaxHealth(player.getMaxHealth() - amount);
                                    player.sendMessage(Config.getMessage("heartRemoved").replace("${amount}", String.valueOf(amount)));
                                    sender.sendMessage(Config.getMessage("heartRemovedAdmin").replace("${amount}", String.valueOf(amount)).replace("${player}", player.getName()));

                                }))
                        .withSubcommand(new CommandAPICommand("set")
                                .withPermission("lifesteal.heart.manage")
                                .withArguments(new PlayerArgument("player"), new IntegerArgument("amount"))
                                .withShortDescription("Sets amount of player's hearts.")
                                .executes((sender, args) -> {
                                    Player player = (Player) args[0];
                                    int amount = (int) args[1];
                                    player.setMaxHealth(amount);
                                    player.sendMessage(Config.getMessage("heartSetted").replace("${amount}", String.valueOf(amount)));
                                    sender.sendMessage(Config.getMessage("heartSettedAdmin").replace("${amount}", String.valueOf(amount)).replace("${player}", player.getName()));

                                }))

                )
//                .withSubcommand(new CommandAPICommand("debug")
//                        .executes((sender, args) -> {
//
//                        }))
                .withSubcommand(new CommandAPICommand("reload")
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
                                    world.getPopulators().add(new LootPopulator(this));
                                }
                            }
                            sender.sendMessage(Config.getMessage("lootReloaded"));

                        }))
                .withSubcommand(new CommandAPICommand("give")
                        .withShortDescription("Gives you specified item.")
                        .withSubcommand(new CommandAPICommand("extra_heart")
                                .withPermission("lifesteal.give.extraheart")
                                .withArguments(new PlayerArgument("player"), new IntegerArgument("chance_of_success"), new IntegerArgument("amount"))).executes((sender, args) -> {
                            Player player = (Player) args[0];
                            int chance = (int) args[1];
                            int amount = (int) args[2];
                            // loop amount times
                            for (int i = 0; i < amount; i++) {
                                player.getInventory().addItem(Items.ExtraHeart.getExtraHeart(chance));
                                player.updateInventory();
                            }
                        }).withSubcommand(new CommandAPICommand("revive_book")
                                .withPermission("lifesteal.give.revivebook")
                                .withArguments(new PlayerArgument("player"), new IntegerArgument("amount"))).executes((sender, args) -> {
                            Player player = (Player) args[0];
                            int amount = (int) args[1];
                            // loop amount times
                            for (int i = 0; i < amount; i++) {
                                player.getInventory().addItem(Items.ReviveBook.getReviveBook());
                                player.updateInventory();
                            }
                        })).register();
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
        getServer().getPluginManager().registerEvents(new InventoryClick(), this);


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
