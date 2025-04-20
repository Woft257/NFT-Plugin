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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

/**
 * Command to display a virtual inventory of NFTs
 */
public class NFTInventoryCommand implements CommandExecutor, Listener {

    private final NFTPlugin plugin;
    private final String INVENTORY_TITLE = ChatColor.DARK_PURPLE + "NFT Inventory";
    private final int ITEMS_PER_PAGE = 45; // 9x5 grid, leaving bottom row for navigation
    private final Map<String, Integer> playerPages = new HashMap<>();
    private final Map<UUID, List<NFTData>> playerNFTs = new HashMap<>();

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

        // Load player's NFTs
        loadPlayerNFTs(player);

        // Open inventory
        int page = 0;
        if (args.length > 0) {
            try {
                page = Integer.parseInt(args[0]) - 1;
                if (page < 0) {
                    page = 0;
                }
            } catch (NumberFormatException e) {
                player.sendMessage(plugin.getConfigManager().getMessage("prefix") + ChatColor.RED + "Invalid page number.");
                return true;
            }
        }

        openInventoryPage(player, page);
        return true;
    }

    /**
     * Load a player's NFTs from the database
     * @param player The player
     */
    private void loadPlayerNFTs(Player player) {
        List<NFTData> nfts = plugin.getDatabaseManager().getPlayerNFTs(player.getUniqueId());

        // Debug: Print NFT count
        plugin.getLogger().info("Loaded " + nfts.size() + " NFTs for player " + player.getName());

        // Store in map
        playerNFTs.put(player.getUniqueId(), nfts);
    }

    /**
     * Open a specific page of the NFT inventory for a player
     * @param player The player
     * @param page The page number (0-based)
     */
    private void openInventoryPage(Player player, int page) {
        List<NFTData> nfts = playerNFTs.get(player.getUniqueId());
        if (nfts == null) {
            nfts = new ArrayList<>();
            playerNFTs.put(player.getUniqueId(), nfts);
        }

        if (nfts.isEmpty()) {
            player.sendMessage(plugin.getConfigManager().getMessage("prefix") + ChatColor.YELLOW + "You don't have any NFTs yet.");
            // Still open an empty inventory
        }

        // Calculate total pages
        int totalPages = (int) Math.ceil((double) nfts.size() / ITEMS_PER_PAGE);

        // Validate page number
        if (page < 0) {
            page = 0;
        } else if (page >= totalPages) {
            page = totalPages - 1;
        }

        // Update player's current page
        playerPages.put(player.getName(), page);

        // Create inventory with 54 slots (6 rows)
        Inventory inventory = Bukkit.createInventory(player, 54, INVENTORY_TITLE + " - Page " + (page + 1) + "/" + totalPages);

        // Calculate start and end indices for this page
        int startIndex = page * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, nfts.size());

        // Add NFT items to inventory
        int slot = 0;
        for (int i = startIndex; i < endIndex; i++) {
            NFTData nft = nfts.get(i);

            // Create NFT item
            ItemStack item = createNFTItem(nft);

            // Add to inventory
            inventory.setItem(slot, item);
            slot++;
        }

        // Add navigation buttons in the bottom row
        addNavigationButtons(inventory, page, totalPages);

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
            lore.add(ChatColor.YELLOW + "NFT ID: " + ChatColor.WHITE + nft.getNftId());
            lore.add(ChatColor.YELLOW + "Acquired: " + ChatColor.WHITE + nft.getFormattedTimestamp());
            lore.add("");
            lore.add(ChatColor.YELLOW + "Click to view details");

            meta.setLore(lore);

            // Store NFT ID in persistent data container
            PersistentDataContainer container = meta.getPersistentDataContainer();
            container.set(plugin.getItemManager().getNftIdKey(), PersistentDataType.STRING, nft.getNftId());

            item.setItemMeta(meta);
        }

        return item;
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
     * Add navigation buttons to the inventory
     * @param inventory The inventory
     * @param currentPage The current page
     * @param totalPages The total number of pages
     */
    private void addNavigationButtons(Inventory inventory, int currentPage, int totalPages) {
        // Add info button in the middle
        ItemStack infoButton = new ItemStack(Material.BOOK);
        ItemMeta infoMeta = infoButton.getItemMeta();
        if (infoMeta != null) {
            infoMeta.setDisplayName(ChatColor.YELLOW + "NFT Information");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Page: " + ChatColor.WHITE + (currentPage + 1) + "/" + totalPages);

            // Get total NFTs count from the owner of the inventory
            int totalNFTs = 0;
            if (inventory.getHolder() instanceof Player) {
                Player owner = (Player) inventory.getHolder();
                List<NFTData> nfts = playerNFTs.get(owner.getUniqueId());
                if (nfts != null) {
                    totalNFTs = nfts.size();
                }
            } else if (!inventory.getViewers().isEmpty()) {
                // Fallback to first viewer if holder is not a player
                UUID viewerUUID = inventory.getViewers().get(0).getUniqueId();
                List<NFTData> nfts = playerNFTs.get(viewerUUID);
                if (nfts != null) {
                    totalNFTs = nfts.size();
                }
            }

            lore.add(ChatColor.GRAY + "Total NFTs: " + ChatColor.GOLD + totalNFTs);
            lore.add("");
            lore.add(ChatColor.YELLOW + "Click on an NFT to view details");
            infoMeta.setLore(lore);
            infoButton.setItemMeta(infoMeta);
        }
        inventory.setItem(49, infoButton);

        // Add previous page button if not on first page
        if (currentPage > 0) {
            ItemStack prevButton = new ItemStack(Material.ARROW);
            ItemMeta prevMeta = prevButton.getItemMeta();
            if (prevMeta != null) {
                prevMeta.setDisplayName(ChatColor.GREEN + "Previous Page");
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Go to page " + currentPage);
                prevMeta.setLore(lore);
                prevButton.setItemMeta(prevMeta);
            }
            inventory.setItem(45, prevButton);
        } else {
            // Disabled previous button
            ItemStack disabledButton = new ItemStack(Material.ARROW);
            ItemMeta disabledMeta = disabledButton.getItemMeta();
            if (disabledMeta != null) {
                disabledMeta.setDisplayName(ChatColor.GRAY + "Previous Page");
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "You are on the first page");
                disabledMeta.setLore(lore);
                disabledButton.setItemMeta(disabledMeta);
            }
            inventory.setItem(45, disabledButton);
        }

        // Add next page button if not on last page
        if (currentPage < totalPages - 1) {
            ItemStack nextButton = new ItemStack(Material.ARROW);
            ItemMeta nextMeta = nextButton.getItemMeta();
            if (nextMeta != null) {
                nextMeta.setDisplayName(ChatColor.GREEN + "Next Page");
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Go to page " + (currentPage + 2));
                nextMeta.setLore(lore);
                nextButton.setItemMeta(nextMeta);
            }
            inventory.setItem(53, nextButton);
        } else {
            // Disabled next button
            ItemStack disabledButton = new ItemStack(Material.ARROW);
            ItemMeta disabledMeta = disabledButton.getItemMeta();
            if (disabledMeta != null) {
                disabledMeta.setDisplayName(ChatColor.GRAY + "Next Page");
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "You are on the last page");
                disabledMeta.setLore(lore);
                disabledButton.setItemMeta(disabledMeta);
            }
            inventory.setItem(53, disabledButton);
        }
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
        if (title.startsWith(INVENTORY_TITLE)) {
            event.setCancelled(true); // Prevent taking items

            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || clickedItem.getType() == Material.AIR) {
                return;
            }

            // Get current page
            Integer currentPage = playerPages.get(player.getName());
            if (currentPage == null) {
                currentPage = 0;
            }

            // Check if clicked on previous page button
            if (event.getSlot() == 45 && clickedItem.getType() == Material.ARROW) {
                // Debug: Print current page
                plugin.getLogger().info("Clicked previous button. Current page: " + currentPage);

                if (currentPage > 0) {
                    plugin.getLogger().info("Going to page " + (currentPage - 1));
                    openInventoryPage(player, currentPage - 1);
                } else {
                    plugin.getLogger().info("Already on first page");
                }
                return;
            }

            // Check if clicked on next page button
            if (event.getSlot() == 53 && clickedItem.getType() == Material.ARROW) {
                // Debug: Print current page
                plugin.getLogger().info("Clicked next button. Current page: " + currentPage);

                List<NFTData> nfts = playerNFTs.get(player.getUniqueId());
                if (nfts != null) {
                    int totalPages = (int) Math.ceil((double) nfts.size() / ITEMS_PER_PAGE);
                    plugin.getLogger().info("Total pages: " + totalPages);

                    if (currentPage < totalPages - 1) {
                        plugin.getLogger().info("Going to page " + (currentPage + 1));
                        openInventoryPage(player, currentPage + 1);
                    } else {
                        plugin.getLogger().info("Already on last page");
                    }
                }
                return;
            }

            // Handle click on NFT item
            ItemMeta meta = clickedItem.getItemMeta();
            if (meta != null) {
                // Get NFT ID from persistent data container
                PersistentDataContainer container = meta.getPersistentDataContainer();
                String nftId = container.get(plugin.getItemManager().getNftIdKey(), PersistentDataType.STRING);

                if (nftId != null) {
                    // Display detailed NFT information
                    player.closeInventory();
                    displayNFTDetails(player, nftId);
                } else {
                    // Since we no longer store NFT ID in lore, just inform the player
                    player.sendMessage(plugin.getConfigManager().getMessage("prefix") + ChatColor.RED + "Could not retrieve NFT information from this item.");
                    player.closeInventory();
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
        if (title.startsWith(INVENTORY_TITLE)) {
            // Clean up resources
            Player player = (Player) event.getPlayer();
            playerPages.remove(player.getName());
        }
    }

    /**
     * Display detailed information about an NFT
     * @param player The player
     * @param nftId The NFT ID
     */
    private void displayNFTDetails(Player player, String nftId) {
        // Get NFT data from database
        NFTData nftData = plugin.getDatabaseManager().getNFTById(nftId);
        if (nftData == null) {
            player.sendMessage(plugin.getConfigManager().getMessage("prefix") + ChatColor.RED + "NFT not found: " + nftId);
            return;
        }

        // Get achievement details
        String achievementKey = nftData.getAchievementKey();
        String achievementName = getFormattedAchievementName(achievementKey);
        String description = plugin.getConfigManager().getNftDescription(achievementKey);

        // Display NFT information
        player.sendMessage(ChatColor.GOLD + "===== NFT Details =====");
        player.sendMessage(ChatColor.YELLOW + "Name: " + ChatColor.WHITE + achievementName);
        player.sendMessage(ChatColor.YELLOW + "Description: " + ChatColor.WHITE + description);
        player.sendMessage(ChatColor.YELLOW + "NFT ID: " + ChatColor.WHITE + nftId);
        player.sendMessage(ChatColor.YELLOW + "Acquired: " + ChatColor.WHITE + nftData.getFormattedTimestamp());

        // Display Solana Explorer link if available
        String mintAddress = nftData.getMintAddress();
        if (mintAddress != null && !mintAddress.isEmpty()) {
            String explorerUrl = plugin.getConfigManager().getSolanaExplorerUrl() + "/address/" + mintAddress;
            player.sendMessage(ChatColor.YELLOW + "Solana Explorer: " + ChatColor.AQUA + explorerUrl);
        }
    }
}
