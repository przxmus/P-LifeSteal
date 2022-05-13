package eu.vibemc.lifesteal.other;

import eu.vibemc.lifesteal.Main;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class LootPopulator extends BlockPopulator {

    private final Main plugin;

    public LootPopulator(@NotNull final Main plugin) {
        this.plugin = plugin;
    }

    public void populate(@NotNull final World world,
                         @NotNull final Random random,
                         @NotNull final Chunk chunk) {
        for (Entity entity : chunk.getEntities()) {
            if (!(entity instanceof StorageMinecart minecart)) {
                continue;
            }
            modifyInventory(minecart.getInventory(), chunk);
        }

        for (BlockState state : chunk.getTileEntities()) {
            Block block = state.getBlock();
            if (!(block.getState() instanceof Chest chestState)) {
                continue;
            }
            Inventory inventory = chestState.getBlockInventory();
            modifyInventory(inventory, chunk);
        }
    }

    public void modifyInventory(@NotNull final Inventory inventory,
                                @NotNull final Chunk chunk) {

        try {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (chunk.isLoaded()) {
                    int random = (int) (Math.random() * 100);
                    Main.getInstance().getConfig().getConfigurationSection("loot.worlds").getKeys(false).forEach(worldName -> {
                        if (worldName.equals(chunk.getWorld().getName())) {
                            if (random <= Config.getInt("loot.worlds." + worldName + ".chanceForHeartToGenerate")) {
                                inventory.addItem(Items.Heart.getHeartItem(Config.getInt("loot.worlds." + worldName + ".heartAddChance")));
                            }
                        }
                    });
                }
            }, 1L);
        } catch (IllegalPluginAccessException e) {
            return;
        }

    }

}
