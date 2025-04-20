package com.minecraft.nftplugin.commands;

import com.minecraft.nftplugin.NFTPlugin;
import com.minecraft.nftplugin.database.NFTData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Command to display a real inventory for storing NFTs
 */
public class NFTInventoryCommand implements CommandExecutor, Listener {

    private final NFTPlugin plugin;
    private final String INVENTORY_TITLE = ChatColor.DARK_PURPLE + "NFT Inventory";  // Title for the inventory
    private final int INVENTORY_SIZE = 54; // 6 rows of 9 slots

    public NFTInventoryCommand(NFTPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getConfigManager().getMessage("prefix") + ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;
        openNFTInventory(player);
        return true;
    }

    /**
     * Open the NFT inventory for a player
     * @param player The player
     */
    private void openNFTInventory(Player player) {
        // Create inventory with 54 slots (6 rows)
        Inventory inventory = Bukkit.createInventory(player, INVENTORY_SIZE, INVENTORY_TITLE);

        // Load NFTs from inventory database
        Map<Integer, NFTData> inventoryNFTs = plugin.getDatabaseManager().getPlayerInventoryNFTs(player.getUniqueId());

        // Add NFT items to inventory
        for (Map.Entry<Integer, NFTData> entry : inventoryNFTs.entrySet()) {
            int slot = entry.getKey();
            NFTData nft = entry.getValue();

            // Create NFT item
            ItemStack item = createNFTItem(nft);

            // Add to inventory
            inventory.setItem(slot, item);
        }

        // Open inventory for player
        player.openInventory(inventory);
    }

    /**
     * Create an item representing an NFT
     * @param nft The NFT data
     * @return The item stack
     */
    private ItemStack createNFTItem(NFTData nft) {
        // Get achievement details
        String achievementKey = nft.getAchievementKey();
        String achievementName = getFormattedAchievementName(achievementKey);
        String description = plugin.getConfigManager().getNftDescription(achievementKey);

        // Try to get material from metadata
        Material material = getMaterialFromMetadata(achievementKey);
        if (material == null) {
            // Fallback to default material
            material = plugin.getConfigManager().getNftItemMaterial();
        }

        // Create item
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            // Set display name
            meta.setDisplayName(ChatColor.GOLD + achievementName);

            // Set lore
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + description);
            lore.add("");

            // Add buff information if it's a Lucky Charm
            if (achievementKey.startsWith("lucky_charm_")) {
                try {
                    int buffValue = Integer.parseInt(achievementKey.substring("lucky_charm_".length()));
                    lore.add(ChatColor.AQUA + "Luck: " + ChatColor.WHITE + "+" + buffValue + "%");
                    lore.add("");
                } catch (NumberFormatException e) {
                    // Ignore parsing errors
                }
            }

            // Add enchantment information
            if (achievementKey.contains("explosion")) {
                int level = 1; // Default level
                if (achievementKey.contains("_")) {
                    try {
                        level = Integer.parseInt(achievementKey.substring(achievementKey.lastIndexOf("_") + 1));
                    } catch (NumberFormatException e) {
                        // Ignore parsing errors
                    }
                }
                int size = level + 2; // New formula for size (level 1 = 3 blocks)
                lore.add(ChatColor.LIGHT_PURPLE + "Explosion " + getRomanNumeral(level) + ": " +
                         ChatColor.WHITE + "Break blocks in a " + size + "x" + size + " area");
                lore.add("");
            } else if (achievementKey.contains("laser")) {
                int level = 1; // Default level
                if (achievementKey.contains("_")) {
                    try {
                        level = Integer.parseInt(achievementKey.substring(achievementKey.lastIndexOf("_") + 1));
                    } catch (NumberFormatException e) {
                        // Ignore parsing errors
                    }
                }
                int depth = level + 1; // Simple formula for depth
                lore.add(ChatColor.LIGHT_PURPLE + "Laser " + getRomanNumeral(level) + ": " +
                         ChatColor.WHITE + "Break blocks up to " + depth + " blocks deep");
                lore.add("");
            }

            // Add NFT details
            lore.add(ChatColor.YELLOW + "NFT ID: " + ChatColor.WHITE + nft.getNftId());
            lore.add(ChatColor.YELLOW + "Acquired: " + ChatColor.WHITE + nft.getFormattedTimestamp());

            meta.setLore(lore);

            // Store NFT ID in persistent data container
            PersistentDataContainer container = meta.getPersistentDataContainer();
            container.set(plugin.getItemManager().getNftIdKey(), PersistentDataType.STRING, nft.getNftId());

            item.setItemMeta(meta);
        }

        return item;
    }

    /**
     * Convert a number to Roman numeral
     * @param number The number to convert
     * @return The Roman numeral
     */
    private String getRomanNumeral(int number) {
        switch (number) {
            case 1: return "I";
            case 2: return "II";
            case 3: return "III";
            case 4: return "IV";
            case 5: return "V";
            default: return String.valueOf(number);
        }
    }

    /**
     * Get a formatted achievement name from the achievement key
     * @param achievementKey The achievement key
     * @return The formatted name
     */
    private String getFormattedAchievementName(String achievementKey) {
        // Convert snake_case to Title Case
        String[] parts = achievementKey.split("_");
        StringBuilder nameBuilder = new StringBuilder();
        for (String part : parts) {
            if (!part.isEmpty()) {
                nameBuilder.append(part.substring(0, 1).toUpperCase())
                        .append(part.substring(1).toLowerCase())
                        .append(" ");
            }
        }
        return nameBuilder.toString().trim();
    }

    /**
     * Get the material for an NFT from its metadata
     * @param achievementKey The achievement key
     * @return The material, or null if not found
     */
    private Material getMaterialFromMetadata(String achievementKey) {
        try {
            String materialName = plugin.getConfigManager().getNftItemMaterialName(achievementKey);
            if (materialName != null && !materialName.isEmpty()) {
                return Material.valueOf(materialName.toUpperCase());
            }
        } catch (IllegalArgumentException e) {
            // Invalid material name
        }
        return null;
    }

    /**
     * Handle inventory click events
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();

        // Check if the clicked inventory is an NFT inventory
        if (title.equals(INVENTORY_TITLE)) {
            // Allow players to move NFT items within the inventory
            if (event.getClickedInventory() != null && event.getClickedInventory().getType() == InventoryType.CHEST) {
                // If clicking in the NFT inventory
                ItemStack clickedItem = event.getCurrentItem();
                if (clickedItem != null && clickedItem.getType() != Material.AIR) {
                    // Check if it's an NFT item
                    ItemMeta meta = clickedItem.getItemMeta();
                    if (meta != null) {
                        PersistentDataContainer container = meta.getPersistentDataContainer();
                        String nftId = container.get(plugin.getItemManager().getNftIdKey(), PersistentDataType.STRING);
                        if (nftId == null) {
                            // Not an NFT item, cancel the event
                            event.setCancelled(true);
                            player.sendMessage(plugin.getConfigManager().getMessage("prefix") +
                                ChatColor.RED + "Only NFT items can be placed in this inventory.");
                        } else {
                            // It's an NFT item, allow the move
                            // Save the new position when inventory closes
                        }
                    } else {
                        // No meta, cancel the event
                        event.setCancelled(true);
                    }
                }
            } else if (event.getClickedInventory() != null && event.getClickedInventory().getType() == InventoryType.PLAYER) {
                // If clicking in the player inventory
                ItemStack clickedItem = event.getCurrentItem();
                if (clickedItem != null && clickedItem.getType() != Material.AIR) {
                    // Check if it's an NFT item
                    ItemMeta meta = clickedItem.getItemMeta();
                    if (meta != null) {
                        PersistentDataContainer container = meta.getPersistentDataContainer();
                        String nftId = container.get(plugin.getItemManager().getNftIdKey(), PersistentDataType.STRING);
                        if (nftId != null) {
                            // It's an NFT item, allow the move
                            // Save the new position when inventory closes
                        }
                    }
                }
            }
        }
    }

    /**
     * Handle inventory drag events
     */
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        String title = event.getView().getTitle();
        if (title.equals(INVENTORY_TITLE)) {
            // Check if any of the slots are in the top inventory
            boolean affectsTopInventory = false;
            for (int slot : event.getRawSlots()) {
                if (slot < INVENTORY_SIZE) {
                    affectsTopInventory = true;
                    break;
                }
            }

            if (affectsTopInventory) {
                // Check if it's an NFT item
                ItemStack draggedItem = event.getOldCursor();
                if (draggedItem != null && draggedItem.getType() != Material.AIR) {
                    ItemMeta meta = draggedItem.getItemMeta();
                    if (meta != null) {
                        PersistentDataContainer container = meta.getPersistentDataContainer();
                        String nftId = container.get(plugin.getItemManager().getNftIdKey(), PersistentDataType.STRING);
                        if (nftId == null) {
                            // Not an NFT item, cancel the event
                            event.setCancelled(true);
                            Player player = (Player) event.getWhoClicked();
                            player.sendMessage(plugin.getConfigManager().getMessage("prefix") +
                                ChatColor.RED + "Only NFT items can be placed in this inventory.");
                        }
                    } else {
                        // No meta, cancel the event
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    /**
     * Handle inventory close events
     */
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }

        String title = event.getView().getTitle();
        if (title.equals(INVENTORY_TITLE)) {
            // Save the inventory contents to the database
            Player player = (Player) event.getPlayer();
            Inventory inventory = event.getInventory();

            // Get player UUID
            UUID playerUUID = player.getUniqueId();

            // Get current NFTs in inventory from database
            Map<Integer, NFTData> currentInventory = plugin.getDatabaseManager().getPlayerInventoryNFTs(playerUUID);
            Set<Integer> usedSlots = new HashSet<>();

            // First, collect all NFTs from the current inventory view
            Map<Integer, String> newInventoryContents = new HashMap<>();
            for (int slot = 0; slot < INVENTORY_SIZE; slot++) {
                ItemStack item = inventory.getItem(slot);
                if (item != null && item.getType() != Material.AIR) {
                    ItemMeta meta = item.getItemMeta();
                    if (meta != null) {
                        PersistentDataContainer container = meta.getPersistentDataContainer();
                        String nftId = container.get(plugin.getItemManager().getNftIdKey(), PersistentDataType.STRING);
                        if (nftId != null) {
                            newInventoryContents.put(slot, nftId);
                            usedSlots.add(slot);
                        }
                    }
                }
            }

            // Log what we found
            plugin.getLogger().info("Found " + newInventoryContents.size() + " NFTs in inventory view for player " + player.getName());

            // Now update the database - first remove NFTs from slots that are now empty
            for (Map.Entry<Integer, NFTData> entry : currentInventory.entrySet()) {
                int slot = entry.getKey();
                if (!usedSlots.contains(slot)) {
                    // This slot is now empty, remove the NFT
                    plugin.getDatabaseManager().removeNFTFromInventory(playerUUID, slot);
                    plugin.getLogger().info("Removed NFT from slot " + slot + " for player " + player.getName());
                }
            }

            // Then add or update NFTs in slots that now have NFTs
            for (Map.Entry<Integer, String> entry : newInventoryContents.entrySet()) {
                int slot = entry.getKey();
                String nftId = entry.getValue();

                // Add to database
                boolean success = plugin.getDatabaseManager().addNFTToInventory(playerUUID, nftId, slot);
                if (success) {
                    plugin.getLogger().info("Added/Updated NFT " + nftId + " to slot " + slot + " for player " + player.getName());
                } else {
                    plugin.getLogger().warning("Failed to add NFT " + nftId + " to slot " + slot + " for player " + player.getName());
                }
            }
        }
    }


}
