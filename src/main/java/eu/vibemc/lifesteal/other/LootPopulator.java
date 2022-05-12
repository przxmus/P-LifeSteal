package eu.vibemc.lifesteal.other;

import eu.vibemc.lifesteal.Main;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

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
                    if (chunk.getWorld().getEnvironment() == World.Environment.NORMAL) {
//                        if (random <= 1) {
//                            inventory.addItem(Items.Heart.getHeartItem(47));
//                            return;
//                        }
//                        if (random <= 5) {
//                            inventory.addItem(Items.Heart.getHeartItem(30));
//                            return;
//                        }
//                        if (random <= 45) {
//                            inventory.addItem(Items.Heart.getHeartItem(5));
//                            return;
//                        }
                    } else if (chunk.getWorld().getEnvironment() == World.Environment.NETHER) {
//                        if (random <= 1) {
//                            inventory.addItem(Items.Heart.getHeartItem(70));
//                            return;
//                        }
//                        if (random <= 15) {
//                            inventory.addItem(Items.Heart.getHeartItem(50));
//                            return;
//                        }
//                        if (random <= 35) {
//                            inventory.addItem(Items.Heart.getHeartItem(30));
//                            return;
//                        }
//                        if (random <= 70) {
//                            inventory.addItem(Items.Heart.getHeartItem(5));
//                            return;
//                        }
                    } else if (chunk.getWorld().getEnvironment() == World.Environment.THE_END) {
//                        if (random <= 2) {
//                            inventory.addItem(Items.Heart.getHeartItem(100));
//                        }
//                        if (random <= 20) {
//                            inventory.addItem(Items.Heart.getHeartItem(70));
//                            return;
//                        }
//                        if (random <= 35) {
//                            inventory.addItem(Items.Heart.getHeartItem(50));
//                            return;
//                        }
//                        if (random <= 65) {
//                            inventory.addItem(Items.Heart.getHeartItem(30));
//                            return;
//                        }
//                        if (random <= 80) {
//                            inventory.addItem(Items.Heart.getHeartItem(5));
//                            return;
//                        }
                    }
                }
            }, 1L);
        }
        catch (IllegalPluginAccessException e) {
            return;
        }

    }

}
