package com.minecraft.nftplugin.utils;

import com.minecraft.nftplugin.NFTPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {

    private final NFTPlugin plugin;
    private final FileConfiguration config;

    // Cache for frequently accessed config values
    private final Map<String, String> messageCache = new HashMap<>();

    public ConfigManager(NFTPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        cacheMessages();
    }

    /**
     * Cache messages for faster access
     */
    private void cacheMessages() {
        ConfigurationSection messagesSection = config.getConfigurationSection("messages");
        if (messagesSection != null) {
            for (String key : messagesSection.getKeys(false)) {
                messageCache.put(key, ChatColor.translateAlternateColorCodes('&', messagesSection.getString(key, "")));
            }
        }
    }

    /**
     * Get a message from the config
     * @param key The message key
     * @return The formatted message
     */
    public String getMessage(String key) {
        return messageCache.getOrDefault(key, "");
    }

    /**
     * Get a message from the config with placeholders
     * @param key The message key
     * @param placeholders The placeholders to replace (format: %placeholder%)
     * @param values The values to replace the placeholders with
     * @return The formatted message with placeholders replaced
     */
    public String getMessage(String key, String[] placeholders, String[] values) {
        if (placeholders.length != values.length) {
            plugin.getLogger().warning("Placeholder and value arrays must be the same length!");
            return getMessage(key);
        }

        String message = getMessage(key);
        for (int i = 0; i < placeholders.length; i++) {
            message = message.replace(placeholders[i], values[i]);
        }

        return message;
    }

    /**
     * Get the database host
     * @return The database host
     */
    public String getDatabaseHost() {
        return config.getString("database.host", "localhost");
    }

    /**
     * Get the database port
     * @return The database port
     */
    public int getDatabasePort() {
        return config.getInt("database.port", 3306);
    }

    /**
     * Get the database name
     * @return The database name
     */
    public String getDatabaseName() {
        return config.getString("database.database", "minecraft");
    }

    /**
     * Get the database username
     * @return The database username
     */
    public String getDatabaseUsername() {
        return config.getString("database.username", "minecraft");
    }

    /**
     * Get the database password
     * @return The database password
     */
    public String getDatabasePassword() {
        return config.getString("database.password", "password");
    }

    /**
     * Get the database table prefix
     * @return The database table prefix
     */
    public String getDatabaseTablePrefix() {
        return config.getString("database.table-prefix", "nftplugin_");
    }

    /**
     * Get the Solana network
     * @return The Solana network
     */
    public String getSolanaNetwork() {
        return config.getString("solana.network", "devnet");
    }

    /**
     * Get the Solana RPC URL
     * @return The Solana RPC URL
     */
    public String getSolanaRpcUrl() {
        return config.getString("solana.rpc_url", "https://api.devnet.solana.com");
    }

    /**
     * Get the Solana server wallet private key
     * @return The Solana server wallet private key
     */
    public String getSolanaServerWalletPrivateKey() {
        // First try to get from environment variable
        String envKey = System.getenv("SOLANA_PRIVATE_KEY");
        if (envKey != null && !envKey.isEmpty()) {
            plugin.getLogger().info("Using Solana private key from environment variable");
            return envKey;
        }

        // Try to get from .env file
        File envFile = new File(plugin.getDataFolder(), "solana-backend/.env");
        if (envFile.exists()) {
            try {
                java.nio.file.Path envPath = envFile.toPath();
                java.util.List<String> lines = java.nio.file.Files.readAllLines(envPath);

                for (String line : lines) {
                    if (line.startsWith("SOLANA_PRIVATE_KEY=") && !line.contains("your_private_key_here")) {
                        String key = line.substring("SOLANA_PRIVATE_KEY=".length()).trim();
                        if (!key.isEmpty()) {
                            plugin.getLogger().info("Using Solana private key from .env file");
                            return key;
                        }
                    }
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to read .env file: " + e.getMessage());
            }
        }

        // Fallback to config (not recommended)
        String configKey = config.getString("solana.server_wallet_private_key", "");
        if (configKey != null && !configKey.isEmpty()) {
            plugin.getLogger().info("Using Solana private key from config.yml (not recommended for security reasons)");
            return configKey;
        }

        // No key found
        return "";
    }

    /**
     * Get the Solana mint fee
     * @return The Solana mint fee
     */
    public double getSolanaMintFee() {
        return config.getDouble("solana.mint_fee", 0.01);
    }

    /**
     * Get the required blocks for an achievement
     * @param achievementKey The achievement key
     * @return The required blocks
     */
    public int getRequiredBlocks(String achievementKey) {
        return config.getInt("achievements." + achievementKey + ".required_blocks", 50);
    }

    /**
     * Get the block type for an achievement
     * @param achievementKey The achievement key
     * @return The block type
     */
    public Material getBlockType(String achievementKey) {
        String materialName = config.getString("achievements." + achievementKey + ".block_type", "OAK_LOG");
        try {
            return Material.valueOf(materialName);
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid material name in config: " + materialName);
            return Material.OAK_LOG;
        }
    }

    /**
     * Get the NFT name for an achievement
     * @param achievementKey The achievement key
     * @return The NFT name
     */
    public String getNftName(String achievementKey) {
        // Use the achievement key as the default name if not specified in config
        String defaultName = achievementKey.equals("great_light") ? "Great Light" :
                            achievementKey.equals("ancient_scroll") ? "Ancient Scroll" :
                            achievementKey;

        return config.getString("achievements." + achievementKey + ".nft_name", defaultName);
    }

    /**
     * Get the NFT description for an achievement
     * @param achievementKey The achievement key
     * @return The NFT description
     */
    public String getNftDescription(String achievementKey) {
        // Use appropriate default descriptions based on achievement key
        String defaultDesc = achievementKey.equals("great_light") ? "A mystical wand containing the power of the great light" :
                            achievementKey.equals("ancient_scroll") ? "A mysterious scroll containing ancient knowledge" :
                            "A special NFT earned by completing the " + achievementKey + " achievement";

        return config.getString("achievements." + achievementKey + ".nft_description", defaultDesc);
    }

    /**
     * Get the NFT image URL for an achievement
     * @param achievementKey The achievement key
     * @return The NFT image URL
     */
    public String getNftImageUrl(String achievementKey) {
        // Check if we should use metadata image URL
        boolean useMetadataImageUrl = config.getBoolean("solana.use_metadata_image_url", true);

        if (useMetadataImageUrl) {
            // Try to get image URL from metadata
            String imageUrl = plugin.getMetadataManager().getNftImageUrl(achievementKey);
            if (imageUrl != null && !imageUrl.isEmpty() && !imageUrl.equals("https://example.com/default.png")) {
                return imageUrl;
            }
        }

        // Try to get from config
        String configImageUrl = config.getString("achievements." + achievementKey + ".nft_image_url", null);
        if (configImageUrl != null && !configImageUrl.isEmpty()) {
            return configImageUrl;
        }

        // Use default from config
        String defaultImageUrl = config.getString("solana.default_image_url",
            "https://cyan-perfect-clam-972.mypinata.cloud/ipfs/bafkreifri6u3f3ww7u6v2gkkcfsol2ijqbno5qmc77n5h57hytebvtr6n4");

        return defaultImageUrl;
    }

    /**
     * Get the complete metadata URI for an achievement
     * @param achievementKey The achievement key
     * @return The metadata URI, or null if not configured
     */
    public String getNftMetadataUri(String achievementKey) {
        // Special case for explosion_pickaxe_5
        if (achievementKey.equals("explosion_pickaxe_5")) {
            String specialUri = config.getString("achievements.explosion_pickaxe_5.metadata_uri", null);
            if (specialUri != null && !specialUri.isEmpty()) {
                plugin.getLogger().info("Using special metadata URI for explosion_pickaxe_5: " + specialUri);
                return specialUri;
            }
        }

        // First check if there's a specific URI for this achievement
        String metadataUri = config.getString("achievements." + achievementKey + ".metadata_uri", null);
        if (metadataUri != null && !metadataUri.isEmpty()) {
            // Log at info level for debugging
            plugin.getLogger().info("Using specific metadata URI for " + achievementKey + ": " + metadataUri);
            return metadataUri;
        }

        // Check if we should use Pinata metadata
        boolean usePinataMetadata = config.getBoolean("solana.use_pinata_metadata", false);
        if (!usePinataMetadata) {
            plugin.getLogger().info("Pinata metadata is disabled in config");
            return null;
        }

        // Check if we have a base Pinata metadata URI
        String pinataBaseUri = config.getString("solana.pinata_base_uri", null);
        if (pinataBaseUri != null && !pinataBaseUri.isEmpty()) {
            // Get the CID for this metadata key
            String metadataCid = config.getString("solana.metadata_cids." + achievementKey, null);
            if (metadataCid != null && !metadataCid.isEmpty()) {
                String fullUri = pinataBaseUri + metadataCid;
                plugin.getLogger().info("Using generated Pinata URI for " + achievementKey + ": " + fullUri);
                return fullUri;
            }
        }

        // No specific metadata URI configured, but we want to use Pinata
        plugin.getLogger().info("No metadata URI found for " + achievementKey + ", but Pinata metadata is enabled");
        plugin.getLogger().info("Configure URI in config.yml using achievements." + achievementKey + ".metadata_uri");
        plugin.getLogger().info("Or using solana.pinata_base_uri and solana.metadata_cids." + achievementKey);

        // No metadata URI configured
        return null;
    }

    /**
     * Check if an achievement is enabled
     * @param achievementKey The achievement key
     * @return True if the achievement is enabled, false otherwise
     */
    public boolean isAchievementEnabled(String achievementKey) {
        return config.getBoolean("achievements." + achievementKey + ".enabled", true);
    }

    /**
     * Get the NFT item material
     * @return The NFT item material
     */
    public Material getNftItemMaterial() {
        String materialName = config.getString("nft_item.material", "DIAMOND_AXE");
        try {
            return Material.valueOf(materialName);
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid material name in config: " + materialName);
            return Material.DIAMOND_AXE;
        }
    }

    /**
     * Get the NFT item name
     * @return The NFT item name
     */
    public String getNftItemName() {
        return ChatColor.translateAlternateColorCodes('&', config.getString("nft_item.name", "&6NFT Item"));
    }

    /**
     * Get the NFT item lore
     * @return The NFT item lore
     */
    public java.util.List<String> getNftItemLore() {
        java.util.List<String> lore = config.getStringList("nft_item.lore");
        java.util.List<String> coloredLore = new java.util.ArrayList<>();

        for (String line : lore) {
            coloredLore.add(ChatColor.translateAlternateColorCodes('&', line));
        }

        return coloredLore;
    }

    /**
     * Get the NFT item enchantments
     * @return The NFT item enchantments
     */
    public Map<String, Integer> getNftItemEnchantments() {
        Map<String, Integer> enchantments = new HashMap<>();

        for (String enchantmentString : config.getStringList("nft_item.enchantments")) {
            String[] parts = enchantmentString.split(":");
            if (parts.length == 2) {
                try {
                    String enchantmentName = parts[0];
                    int level = Integer.parseInt(parts[1]);
                    enchantments.put(enchantmentName, level);
                } catch (NumberFormatException e) {
                    plugin.getLogger().warning("Invalid enchantment level in config: " + enchantmentString);
                }
            }
        }

        return enchantments;
    }

    /**
     * Check if the NFT item is unbreakable
     * @return True if the NFT item is unbreakable, false otherwise
     */
    public boolean isNftItemUnbreakable() {
        return config.getBoolean("nft_item.unbreakable", true);
    }

    /**
     * Get the NFT item custom model data
     * @return The NFT item custom model data
     */
    public int getNftItemCustomModelData() {
        return config.getInt("nft_item.custom_model_data", 1001);
    }

    /**
     * Get the NFT item material name for a specific achievement
     * @param achievementKey The achievement key
     * @return The material name, or null if not found
     */
    public String getNftItemMaterialName(String achievementKey) {
        return config.getString("achievements." + achievementKey + ".item_material", null);
    }

    /**
     * Get the custom model data for a specific achievement
     * @param achievementKey The achievement key
     * @return The custom model data, or -1 if not found
     */
    public int getNftItemCustomModelData(String achievementKey) {
        return config.getInt("achievements." + achievementKey + ".custom_model_data", -1);
    }

    /**
     * Get the Solana Explorer URL
     * @return The Solana Explorer URL
     */
    public String getSolanaExplorerUrl() {
        String network = getSolanaNetwork();
        if ("mainnet".equals(network)) {
            return "https://explorer.solana.com";
        } else if ("devnet".equals(network)) {
            return "https://explorer.solana.com/?cluster=devnet";
        } else if ("testnet".equals(network)) {
            return "https://explorer.solana.com/?cluster=testnet";
        } else {
            return "https://explorer.solana.com";
        }
    }

    /**
     * Get the Solana Explorer URL for a specific address
     * @param address The address to view
     * @return The complete Solana Explorer URL for the address
     */
    public String getSolanaExplorerAddressUrl(String address) {
        String network = getSolanaNetwork();
        if ("mainnet".equals(network)) {
            return "https://explorer.solana.com/address/" + address;
        } else if ("devnet".equals(network)) {
            return "https://explorer.solana.com/address/" + address + "?cluster=devnet";
        } else if ("testnet".equals(network)) {
            return "https://explorer.solana.com/address/" + address + "?cluster=testnet";
        } else {
            return "https://explorer.solana.com/address/" + address;
        }
    }

    /**
     * Get the Solana Explorer URL for a specific transaction
     * @param txId The transaction ID to view
     * @return The complete Solana Explorer URL for the transaction
     */
    public String getSolanaExplorerTransactionUrl(String txId) {
        String network = getSolanaNetwork();
        if ("mainnet".equals(network)) {
            return "https://explorer.solana.com/tx/" + txId;
        } else if ("devnet".equals(network)) {
            return "https://explorer.solana.com/tx/" + txId + "?cluster=devnet";
        } else if ("testnet".equals(network)) {
            return "https://explorer.solana.com/tx/" + txId + "?cluster=testnet";
        } else {
            return "https://explorer.solana.com/tx/" + txId;
        }
    }

    /**
     * Get the NFT detail link for an achievement
     * @param achievementKey The achievement key
     * @return The detail link, or the image URL if not configured
     */
    public String getNftDetailLink(String achievementKey) {
        // Check if there's a specific detail link for this achievement
        String detailLink = config.getString("achievements." + achievementKey + ".detail_link", null);
        if (detailLink != null && !detailLink.isEmpty()) {
            plugin.getLogger().info("Using detail link for " + achievementKey + ": " + detailLink);
            return detailLink;
        }

        // If no detail link is configured, return the image URL as fallback
        return getNftImageUrl(achievementKey);
    }
}
