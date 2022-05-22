package eu.vibemc.lifesteal.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.io.IOException;

public class InventoryClick implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) throws IOException {
//        final Inventory clickedInventory = e.getClickedInventory();
//        final Player player = (Player) e.getWhoClicked();
//        final InventoryView playerOpenedInventory = player.getOpenInventory();
//        if (clickedInventory == null) return;
//        if (playerOpenedInventory == null) return;
//        if (e.getCurrentItem() == null || e.getCurrentItem().getType().isAir()) return;
//        if (playerOpenedInventory.getTopInventory() == clickedInventory) {
//            if (playerOpenedInventory.getTitle().equals(ChatColor.translateAlternateColorCodes('&', Config.getString("reviveBook.inventory-title")))) {
//                e.setCancelled(true);
//                final ItemStack item = e.getCurrentItem();
//                final OfflinePlayer target = Main.getInstance().getServer().getOfflinePlayer(item.getItemMeta().getDisplayName().substring(4));
//                if (BanStorageUtil.deleteBan(target.getUniqueId())) {
//                    player.sendMessage(Config.getMessage("playerRevived").replace("${player}", target.getName()));
//                    player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 100, 1);
//                    player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
//                    player.updateInventory();
//                    player.closeInventory();
//                } else {
//                    player.sendMessage(Config.getMessage("playerNotDead"));
//                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 100, 1);
//                }
//            }
//        }

    }
}
