package com.minecraft.nftplugin.listeners;

import com.minecraft.nftplugin.NFTPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryListener implements Listener {

    private final NFTPlugin plugin;

    public InventoryListener(NFTPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Prevent players from dropping NFT items
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        ItemStack item = event.getItemDrop().getItemStack();

        if (plugin.getItemManager().isNftItem(item)) {
            event.setCancelled(true);
            Player player = event.getPlayer();
            player.sendMessage(plugin.getConfigManager().getMessage("prefix") + "§cYou cannot drop this NFT item!");
        }
    }

    /**
     * Allow players to move NFT items to chests but prevent other inventory movements
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        ItemStack cursorItem = event.getCursor();
        ItemStack currentItem = event.getCurrentItem();

        // Check if the cursor item is an NFT item
        if (cursorItem != null && plugin.getItemManager().isNftItem(cursorItem)) {
            // Allow placing into CHEST, ENDER_CHEST, SHULKER_BOX, BARREL
            if (event.getClickedInventory() != null &&
                event.getClickedInventory().getType() != InventoryType.PLAYER &&
                !isChestLikeInventory(event.getClickedInventory().getType())) {
                event.setCancelled(true);
                player.sendMessage(plugin.getConfigManager().getMessage("prefix") + "§cYou can only place NFT items in chests, ender chests, shulker boxes, or barrels!");
                return;
            }
        }

        // Check if the current item is an NFT item
        if (currentItem != null && plugin.getItemManager().isNftItem(currentItem)) {
            // Allow taking from CHEST, ENDER_CHEST, SHULKER_BOX, BARREL
            if (event.getClickedInventory() != null &&
                event.getClickedInventory().getType() != InventoryType.PLAYER &&
                isChestLikeInventory(event.getClickedInventory().getType())) {
                // This is allowed - taking NFT from a chest
                return;
            }

            // Allow moving within player inventory
            if (event.getClickedInventory() != null &&
                event.getClickedInventory().getType() == InventoryType.PLAYER &&
                event.getView().getTopInventory().getType() == InventoryType.CRAFTING) {
                // This is allowed - moving within player inventory
                return;
            }

            // Allow placing into CHEST, ENDER_CHEST, SHULKER_BOX, BARREL
            if (event.getClickedInventory() != null &&
                event.getClickedInventory().getType() == InventoryType.PLAYER &&
                isChestLikeInventory(event.getView().getTopInventory().getType())) {
                // This is allowed - moving from player to chest
                return;
            }

            // Prevent all other movements
            event.setCancelled(true);
            player.sendMessage(plugin.getConfigManager().getMessage("prefix") + "§cYou can only place NFT items in chests, ender chests, shulker boxes, or barrels!");
        }
    }

    /**
     * Allow players to drag NFT items to chests but prevent other inventory movements
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        ItemStack item = event.getOldCursor();

        if (plugin.getItemManager().isNftItem(item)) {
            // Check if any of the slots are in the top inventory
            boolean draggedToTop = event.getRawSlots().stream()
                    .anyMatch(slot -> slot < event.getView().getTopInventory().getSize());

            if (draggedToTop && !isChestLikeInventory(event.getView().getTopInventory().getType())) {
                event.setCancelled(true);
                Player player = (Player) event.getWhoClicked();
                player.sendMessage(plugin.getConfigManager().getMessage("prefix") + "§cYou can only place NFT items in chests, ender chests, shulker boxes, or barrels!");
            }
        }
    }

    /**
     * Allow hoppers and other inventory movers to move NFT items only to/from chests
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryMoveItem(InventoryMoveItemEvent event) {
        ItemStack item = event.getItem();

        if (plugin.getItemManager().isNftItem(item)) {
            // Allow movement only if both source and destination are chest-like
            if (!isChestLikeInventory(event.getSource().getType()) ||
                !isChestLikeInventory(event.getDestination().getType())) {
                event.setCancelled(true);
            }
        }
    }

    /**
     * Check if an inventory type is a chest-like container
     * @param type The inventory type
     * @return True if the inventory is chest-like
     */
    private boolean isChestLikeInventory(InventoryType type) {
        return type == InventoryType.CHEST ||
               type == InventoryType.ENDER_CHEST ||
               type == InventoryType.SHULKER_BOX ||
               type == InventoryType.BARREL;
    }
}
