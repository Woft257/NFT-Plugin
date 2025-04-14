package com.minecraft.nftplugin.commands;

import com.minecraft.nftplugin.NFTPlugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Optional;

public class NFTInfoCommand implements CommandExecutor {

    private final NFTPlugin plugin;

    public NFTInfoCommand(NFTPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getConfigManager().getMessage("prefix") + "§cThis command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;

        // Check if the player has permission
        if (!player.hasPermission("nftplugin.nftinfo")) {
            player.sendMessage(plugin.getConfigManager().getMessage("prefix") + "§cYou don't have permission to use this command.");
            return true;
        }

        // Check if the player has a registered wallet in SolanaLogin
        if (!plugin.getSolanaLoginIntegration().hasWalletConnected(player.getUniqueId())) {
            player.sendMessage(plugin.getConfigManager().getMessage("no_wallet"));
            return true;
        }

        // Get the player's wallet address from SolanaLogin
        Optional<String> walletAddressOpt = plugin.getSolanaLoginIntegration().getWalletAddress(player.getUniqueId());
        String walletAddress = walletAddressOpt.orElse("Unknown");

        // Display wallet information
        player.sendMessage("§8§m-----------------------------------------------------");
        player.sendMessage("§6§lYour NFT Information");
        player.sendMessage("§8§m-----------------------------------------------------");
        player.sendMessage("§7Wallet Address: §f" + walletAddress);

        // Check if player is holding an item
        if (player.getInventory().getItemInMainHand() != null && !player.getInventory().getItemInMainHand().getType().isAir()) {
            // Check if the item is an NFT item
            if (plugin.getItemManager().isNftItem(player.getInventory().getItemInMainHand())) {
                // Get the NFT ID from the item
                String nftId = plugin.getItemManager().getNftId(player.getInventory().getItemInMainHand());
                String achievementKey = plugin.getItemManager().getAchievementKey(player.getInventory().getItemInMainHand());

                if (nftId != null && achievementKey != null) {
                    // Get achievement name and details
                    String achievementName = getFormattedAchievementName(achievementKey);
                    String description = plugin.getConfigManager().getNftDescription(achievementKey);
                    String imageUrl = plugin.getConfigManager().getNftImageUrl(achievementKey);

                    // Get item details
                    ItemStack item = player.getInventory().getItemInMainHand();
                    String itemName = item.hasItemMeta() && item.getItemMeta().hasDisplayName() ?
                                     item.getItemMeta().getDisplayName() : item.getType().toString();

                    // Display detailed NFT information
                    player.sendMessage("§8§m-----------------------------------------------------");
                    player.sendMessage("§e§lNFT Details: §r§6" + achievementName);
                    player.sendMessage("§8§m-----------------------------------------------------");
                    player.sendMessage("§7Item: §6" + itemName + " §7(§aNFT Item§7)");
                    player.sendMessage("§7Description: §f" + description);
                    player.sendMessage("§7Transaction ID: §f" + nftId);
                    player.sendMessage("§7Achievement: §f" + achievementName);
                    player.sendMessage("§7Network: §fSolana DevNet");

                    // Display enchantments if any
                    if (!item.getEnchantments().isEmpty()) {
                        player.sendMessage("§7Enchantments: §f" + formatEnchantments(item));
                    }

                    // Display Solana Explorer link
                    player.sendMessage("§7View on Solana Explorer: §f§nhttps://explorer.solana.com/address/" + nftId + "?cluster=devnet");

                    // Display image link
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        player.sendMessage("§7Image: §f§n" + imageUrl);
                    }
                }
            } else {
                player.sendMessage("§7You are not holding an NFT item.");
                player.sendMessage("§7Hold an NFT item to see its information.");
            }
        } else {
            player.sendMessage("§7You are not holding any item.");
            player.sendMessage("§7Hold an NFT item to see its information.");
        }

        player.sendMessage("§8§m-----------------------------------------------------");

        return true;
    }

    /**
     * Format achievement key to a readable name
     * @param achievementKey The achievement key
     * @return The formatted achievement name
     */
    private String getFormattedAchievementName(String achievementKey) {
        if (achievementKey == null) {
            return "Unknown";
        }

        switch (achievementKey) {
            case "anh_sang_vi_dai":
                return "Great Light";
            case "ancient_scroll":
                return "Ancient Scroll";
            case "diamond_sword":
                return "Diamond Sword of Power";
            default:
                // Convert snake_case to Title Case
                String[] words = achievementKey.split("_");
                StringBuilder result = new StringBuilder();
                for (String word : words) {
                    if (!word.isEmpty()) {
                        result.append(Character.toUpperCase(word.charAt(0)))
                              .append(word.substring(1).toLowerCase())
                              .append(" ");
                    }
                }
                return result.toString().trim();
        }
    }

    /**
     * Format enchantments to a readable string
     * @param item The item with enchantments
     * @return The formatted enchantments string
     */
    private String formatEnchantments(ItemStack item) {
        if (item == null || item.getEnchantments().isEmpty()) {
            return "None";
        }

        StringBuilder result = new StringBuilder();
        Map<Enchantment, Integer> enchantments = item.getEnchantments();

        int i = 0;
        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
            Enchantment enchantment = entry.getKey();
            int level = entry.getValue();

            // Get enchantment name
            String name = enchantment.getKey().getKey();
            name = name.replace("_", " ");

            // Capitalize first letter of each word
            String[] words = name.split(" ");
            StringBuilder enchName = new StringBuilder();
            for (String word : words) {
                if (!word.isEmpty()) {
                    enchName.append(Character.toUpperCase(word.charAt(0)))
                           .append(word.substring(1).toLowerCase())
                           .append(" ");
                }
            }

            // Add to result
            result.append(enchName.toString().trim())
                  .append(" ")
                  .append(formatRomanNumeral(level));

            // Add comma if not last
            if (i < enchantments.size() - 1) {
                result.append(", ");
            }

            i++;
        }

        return result.toString();
    }

    /**
     * Convert a number to Roman numeral
     * @param number The number to convert
     * @return The Roman numeral
     */
    private String formatRomanNumeral(int number) {
        if (number <= 0) {
            return "";
        }

        switch (number) {
            case 1: return "I";
            case 2: return "II";
            case 3: return "III";
            case 4: return "IV";
            case 5: return "V";
            case 6: return "VI";
            case 7: return "VII";
            case 8: return "VIII";
            case 9: return "IX";
            case 10: return "X";
            default: return String.valueOf(number);
        }
    }
}
