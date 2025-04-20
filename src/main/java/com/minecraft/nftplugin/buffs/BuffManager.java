package com.minecraft.nftplugin.buffs;

import com.minecraft.nftplugin.NFTPlugin;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Manages buffs from NFT items
 */
public class BuffManager {

    private final NFTPlugin plugin;
    private final NamespacedKey buffTypeKey;
    private final NamespacedKey buffValueKey;

    // Pattern to extract buff values from lore
    private final Pattern luckPattern = Pattern.compile("§b- Luck: §7\\+(\\d+)% chance");

    // Map to store player buffs
    private final Map<UUID, Map<BuffType, Integer>> playerBuffs = new HashMap<>();

    public BuffManager(NFTPlugin plugin) {
        this.plugin = plugin;
        this.buffTypeKey = new NamespacedKey(plugin, "buff_type");
        this.buffValueKey = new NamespacedKey(plugin, "buff_value");
    }

    /**
     * Get the total buff value for a player
     * @param player The player
     * @param buffType The type of buff
     * @return The total buff value
     */
    public int getPlayerBuffValue(Player player, BuffType buffType) {
        Map<BuffType, Integer> buffs = playerBuffs.getOrDefault(player.getUniqueId(), new HashMap<>());
        return buffs.getOrDefault(buffType, 0);
    }

    /**
     * Update buffs for a player based on their inventory
     * @param player The player
     */
    public void updatePlayerBuffs(Player player) {
        // Clear existing buffs for this player
        playerBuffs.put(player.getUniqueId(), new HashMap<>());

        // Check only items in the player's main inventory and armor slots
        // This excludes items in ender chest, chests, etc.
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && plugin.getItemManager().isNftItem(item)) {
                addBuffFromItem(player, item);
            }
        }

        // Also check armor slots
        for (ItemStack item : player.getInventory().getArmorContents()) {
            if (item != null && plugin.getItemManager().isNftItem(item)) {
                addBuffFromItem(player, item);
            }
        }
    }

    /**
     * Add a buff from an item to a player
     * @param player The player
     * @param item The item
     */
    private void addBuffFromItem(Player player, ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return;
        }

        ItemMeta meta = item.getItemMeta();

        // First check persistent data
        PersistentDataContainer container = meta.getPersistentDataContainer();
        String buffTypeStr = container.get(buffTypeKey, PersistentDataType.STRING);
        Integer buffValue = container.get(buffValueKey, PersistentDataType.INTEGER);

        if (buffTypeStr != null && buffValue != null) {
            try {
                BuffType buffType = BuffType.valueOf(buffTypeStr);
                addBuff(player, buffType, buffValue);
                return;
            } catch (IllegalArgumentException e) {
                // Invalid buff type, continue to check lore
            }
        }

        // If not found in persistent data, check lore
        if (meta.hasLore()) {
            for (String line : meta.getLore()) {
                // Check for luck buff
                Matcher luckMatcher = luckPattern.matcher(line);
                if (luckMatcher.find()) {
                    int value = Integer.parseInt(luckMatcher.group(1));
                    addBuff(player, BuffType.LUCK, value);
                }
            }
        }
    }

    /**
     * Add a buff to a player
     * @param player The player
     * @param buffType The type of buff
     * @param value The value of the buff
     */
    private void addBuff(Player player, BuffType buffType, int value) {
        Map<BuffType, Integer> buffs = playerBuffs.getOrDefault(player.getUniqueId(), new HashMap<>());
        int currentValue = buffs.getOrDefault(buffType, 0);
        buffs.put(buffType, currentValue + value);
        playerBuffs.put(player.getUniqueId(), buffs);
    }

    /**
     * Apply buff to an item
     * @param item The item
     * @param buffType The type of buff
     * @param value The value of the buff
     * @return The buffed item
     */
    public ItemStack applyBuff(ItemStack item, BuffType buffType, int value) {
        if (item == null || !item.hasItemMeta()) {
            return item;
        }

        ItemMeta meta = item.getItemMeta();

        // Store in persistent data
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(buffTypeKey, PersistentDataType.STRING, buffType.name());
        container.set(buffValueKey, PersistentDataType.INTEGER, value);

        item.setItemMeta(meta);
        return item;
    }

    /**
     * Get a map of all player buffs
     * @return A map of player UUIDs to their buffs
     */
    public Map<UUID, Map<BuffType, Integer>> getAllPlayerBuffs() {
        return new HashMap<>(playerBuffs);
    }

    /**
     * Update buffs for all online players
     */
    public void updateAllPlayerBuffs() {
        plugin.getServer().getOnlinePlayers().forEach(this::updatePlayerBuffs);
    }
}
