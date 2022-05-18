package eu.vibemc.lifesteal;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandAPIConfig;
import dev.jorel.commandapi.arguments.*;
import eu.vibemc.lifesteal.events.AsyncPlayerPreLogin;
import eu.vibemc.lifesteal.events.PlayerDeath;
import eu.vibemc.lifesteal.events.PlayerInteract;
import eu.vibemc.lifesteal.models.Ban;
import eu.vibemc.lifesteal.other.*;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public final class Main extends JavaPlugin {

    private static Main instance;

    public static Main getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        instance = this;
        try {
            BanStorageUtil.loadNotes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        CommandAPI.onLoad(new CommandAPIConfig());

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
                                        BanStorageUtil.deleteBan(player.getUniqueId());
                                        if (player.getName() != null) {
                                            sender.sendMessage(Config.getMessage("banRemoved").replace("${player}", player.getName()));
                                        } else {
                                            sender.sendMessage(Config.getMessage("playerNotFound"));
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
                        .withArguments(new StringArgument("item")
                                .replaceSuggestions(ArgumentSuggestions.strings("extra_life")), new PlayerArgument("player"), new IntegerArgument("chance_of_success"), new IntegerArgument("amount")).executes((sender, args) -> {
                            String item = (String) args[0];
                            Player player = (Player) args[1];
                            int chance = (int) args[2];
                            int amount = (int) args[3];
                            if (item.equalsIgnoreCase("extra_life")) {
                                if (!sender.hasPermission("lifesteal.give.extraheart")) {
                                    return;
                                }
                                // loop amount times
                                for (int i = 0; i < amount; i++) {
                                    player.getInventory().addItem(Items.Heart.getHeartItem(chance));
                                    player.updateInventory();
                                }
                            }
                        })).register();
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        new UpdateChecker(this).getVersion(version -> {
            if (this.getDescription().getVersion().equals(version)) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "--------P-LifeSteal-" + this.getDescription().getVersion() + "--------");
                if (this.getDescription().getVersion().contains("Alpha") || this.getDescription().getVersion().contains("Beta")) {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "- DO NOT USE THIS PLUGIN IN PRODUCTION!");
                    Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "- SOME FEATURES ARE NOT FINISHED YET!");
                }
                Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GREEN + "- You are up to date.");
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "- Thank you for using my plugin!");
                Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "--------P-LifeSteal-" + this.getDescription().getVersion() + "--------");

            } else {
                Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "--------P-LifeSteal-" + this.getDescription().getVersion() + "--------");
                if (this.getDescription().getVersion().contains("Alpha") || this.getDescription().getVersion().contains("Beta")) {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "- DO NOT USE THIS PLUGIN IN PRODUCTION!");
                    Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "- SOME FEATURES ARE NOT FINISHED YET!");
                }
                Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "- There is a newer version than yours! (" + version + ")");
                Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "- Please download new version from SpigotMC or Github.");
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "- Thank you for using my plugin!");
                Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "--------P-LifeSteal-" + this.getDescription().getVersion() + "--------");
            }
        });

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

        // do every 30 minutes
        getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> {

            new UpdateChecker(this).getVersion(version -> {
                if (!this.getDescription().getVersion().equals(version)) {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "--------P-LifeSteal-" + this.getDescription().getVersion() + "--------");
                    Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "- A NEW UPDATE HAS BEEN RELEASED! (" + version + ")");
                    Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "- Please download new version from SpigotMC or Github.");
                    Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "--------P-LifeSteal-" + this.getDescription().getVersion() + "--------");
                }
            });
        }, 0L, 36000L); //36000L

    }

    private void registerRecipes() {
        if (Config.getBoolean("recipe.enabled")) {
            Main.getInstance().getConfig().getConfigurationSection("recipe.recipes").getKeys(false).forEach(recipe -> {
                if (Config.getBoolean("recipe.recipes." + recipe + ".recipe-enabled")) {
                    if (Config.getBoolean("recipe.recipes." + recipe + ".shaped")) {
                        ShapedRecipe shapedRecipe = new ShapedRecipe(new NamespacedKey("lifesteal", "extraheartrecipe" + recipe), Items.Heart.getHeartItem(Config.getInt("recipe.recipes." + recipe + ".extraHeartItemUseSuccess")));
                        shapedRecipe.shape("ABC", "DEF", "GHI");
                        AtomicInteger itemId = new AtomicInteger(0);
                        Config.getStringList("recipe.recipes." + recipe + ".items").forEach(item -> {
                            itemId.addAndGet(1);
                            if (itemId.get() == 1) {
                                shapedRecipe.setIngredient('A', Material.getMaterial(item));
                            }
                            if (itemId.get() == 2) {
                                shapedRecipe.setIngredient('B', Material.getMaterial(item));
                            }
                            if (itemId.get() == 3) {
                                shapedRecipe.setIngredient('C', Material.getMaterial(item));
                            }
                            if (itemId.get() == 4) {
                                shapedRecipe.setIngredient('D', Material.getMaterial(item));
                            }
                            if (itemId.get() == 5) {
                                shapedRecipe.setIngredient('E', Material.getMaterial(item));
                            }
                            if (itemId.get() == 6) {
                                shapedRecipe.setIngredient('F', Material.getMaterial(item));
                            }
                            if (itemId.get() == 7) {
                                shapedRecipe.setIngredient('G', Material.getMaterial(item));
                            }
                            if (itemId.get() == 8) {
                                shapedRecipe.setIngredient('H', Material.getMaterial(item));
                            }
                            if (itemId.get() == 9) {
                                shapedRecipe.setIngredient('I', Material.getMaterial(item));
                            }
                        });
                        Main.getInstance().getServer().addRecipe(shapedRecipe);
                    } else {
                        ShapelessRecipe shapelessRecipe = new ShapelessRecipe(new NamespacedKey("lifesteal", "extraheartrecipe" + recipe), Items.Heart.getHeartItem(Config.getInt("recipe.recipes." + recipe + ".extraHeartItemUseSuccess")));
                        Config.getStringList("recipe.recipes." + recipe + ".items").forEach(item -> {
                            shapelessRecipe.addIngredient(Material.getMaterial(item));
                        });
                        Main.getInstance().getServer().addRecipe(shapelessRecipe);
                    }
                }

            });
        }
    }

    private void unregisterRecipes() {
        Main.getInstance().getConfig().getConfigurationSection("recipe.recipes").getKeys(false).forEach(recipe -> {
            if (Config.getBoolean("recipe.recipes." + recipe + ".recipe-enabled")) {
                Main.getInstance().getServer().removeRecipe(new NamespacedKey("lifesteal", "extraheartrecipe" + recipe));
            }
        });
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
