package eu.vibemc.lifesteal.other;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class Items {
    public static class Heart {
        public static ItemStack getHeartItem(int chance) {
            ItemStack extraHeart = new ItemStack(Material.getMaterial(Config.getString("heartItem.material")));
            extraHeart.setAmount(1);
            ItemMeta paperMeta = extraHeart.getItemMeta();
            paperMeta.setDisplayName(Config.translateHexCodes(Config.getString("heartItem.name")));
            ArrayList<String> lore = new ArrayList<>();
            List<String> configLoreList = Config.getStringList("heartItem.lore");
            // set lore from config
            for (String loreLine : configLoreList) {
                lore.add(Config.translateHexCodes(loreLine).replace("${chance}", String.valueOf(chance)));
            }
            paperMeta.setLore(lore);
            paperMeta.getPersistentDataContainer().set(new NamespacedKey("lifesteal", "chance"), PersistentDataType.INTEGER, chance);
            extraHeart.setItemMeta(paperMeta);
            return extraHeart;
        }

        private static int getChance(ItemStack item) {
            return item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey("lifesteal", "chance"), PersistentDataType.INTEGER);
        }

        public static boolean isHeart(ItemStack item) {
            return item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey("lifesteal", "chance"), PersistentDataType.INTEGER);
        }

        public static void useHeart(Player player, ItemStack item) {
            if (!Config.getBoolean("heartItem.enabled")) {
                player.sendActionBar(Config.getMessage("itemDisabled"));
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 100, 1);
                return;
            }
            // get chance
            int chance = getChance(item);
            item.setAmount(item.getAmount() - 1);
            // generate random number between 0 and 100 and check if it is less than the chance
            int random = (int) (Math.random() * 100);
            if (random <= chance) {
                player.setMaxHealth(player.getMaxHealth() + 2);
                player.sendActionBar(Config.getMessage("heartReceived"));
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 100, 1);
            } else {
                // create another chance
                int secondRandom = (int) (Math.random() * 100);
                if (secondRandom <= Config.getInt("heartItem.loseChance")) {
                    player.sendActionBar(Config.getMessage("heartFailure"));
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 100, 1);
                } else {
                    if (player.getMaxHealth() - 2 <= 0) {
                        if (Config.getBoolean("banOn0Hearts")) {
                            // ban player
                            player.banPlayerIP(Config.getMessage("noMoreHeartsBan"));
                            if (Config.getBoolean("broadcastBanFrom0Hearts")) {
                                // send message to all players
                                player.getServer().broadcastMessage(Config.getMessage("bannedNoMoreHeartsBroadcast"));
                            }
                        }
                    } else {
                        player.setMaxHealth(player.getMaxHealth() - 2);
                        player.sendActionBar(Config.getMessage("heartLost"));
                        player.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 100, 2);
                    }
                }
            }
        }
    }
}
