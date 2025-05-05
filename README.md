# ✨ Minecraft NFT Plugin ✨

A comprehensive Solana NFT integration plugin for Minecraft, allowing players to receive, store, and use NFTs with special abilities in-game. This plugin creates a seamless bridge between Minecraft gameplay and Solana blockchain technology, enhancing player experience with unique collectible items.

<div align="center">

![NFT Plugin Banner](https://cyan-perfect-clam-972.mypinata.cloud/ipfs/bafkreifri6u3f3ww7u6v2gkkcfsol2ijqbno5qmc77n5h57hytebvtr6n4)

[<img src="https://img.shields.io/badge/Solana-Devnet-blue?style=for-the-badge&logo=solana" alt="Solana Devnet">](https://explorer.solana.com/?cluster=devnet)
[<img src="https://img.shields.io/badge/Minecraft-1.18+-green?style=for-the-badge&logo=minecraft" alt="Minecraft 1.18+">](https://www.minecraft.net/)
[<img src="https://img.shields.io/badge/IPFS-Pinata-orange?style=for-the-badge&logo=ipfs" alt="IPFS Pinata">](https://www.pinata.cloud/)

</div>

## ✅ Features

- **Solana Blockchain Integration**: Mint real NFTs directly on the Solana blockchain
- **Virtual NFT Inventory**: Store and manage your NFTs with an unlimited paginated inventory system
- **Custom Enchantments**:
  - **Explosion Mining** (Levels I-IV): Mine in 3x3 to 6x6 areas
  - **Laser Mining** (Levels I-V): Mine up to 6 blocks deep in a straight line
- **Buff System**: Gain special abilities and bonuses from your NFTs
  - **Lucky Charms**: Increase drop rates for rare items with percentage-based buffs
- **SolanaLogin Integration**: Securely link your Solana wallet to your Minecraft account
- **NFT Lootbox System**: Open lootboxes with different rarity tiers to earn NFTs
- **Admin Commands**: Comprehensive tools for server administrators
- **Metadata Storage**: Store NFT metadata on Pinata IPFS for decentralized access
- **Unbreakable Items**: NFT items are unbreakable and persist after death

## ⚙️ Requirements

- **Minecraft**: Paper/Spigot 1.18.2 or higher
- **Java**: Java 17 or higher
- **Node.js**: Node.js 16+ (for Solana blockchain integration)
- **Plugins**:
  - **WalletLogin**: Required for connecting player accounts to Solana wallets
  - **Vault**: Optional, for Lootbox economy integration

## 💾 Installation

1. **Download the Plugin**:
   - Download the latest NFT-Plugin.jar file from the releases page

2. **Install the Plugin**:
   - Place the JAR file in your Minecraft server's `plugins` directory
   - Install the required WalletLogin plugin

3. **First Run**:
   - Start the server to generate configuration files
   - The plugin will create the following directories:
     - `plugins/NFTPlugin/` - Main plugin directory
     - `plugins/NFTPlugin/metadata/` - NFT metadata files
     - `plugins/NFTPlugin/solana-backend/` - Solana integration files

4. **Set Up Solana Backend**:
   ```bash
   cd plugins/NFTPlugin/solana-backend
   npm install
   ```

5. **Configure the Plugin**:
   - Edit `plugins/NFTPlugin/config.yml` to customize settings
   - Create `.env` file in `plugins/NFTPlugin/solana-backend/` (see below)
   - Add your Solana wallet private key to the `.env` file

6. **Add Metadata Files**:
   - Place your NFT metadata JSON files in the `plugins/NFTPlugin/metadata/` directory
   - Upload metadata to Pinata IPFS and update CIDs in config.yml

7. **Restart the Server**:
   - Restart your Minecraft server to apply all changes

## 🔧 Configuration

### Main Configuration (config.yml)

```yaml
# Plugin Settings
plugin:
  prefix: "&6[NFT] &r"  # Prefix for plugin messages
  debug: false          # Enable debug logging

# Solana Settings
solana:
  # Blockchain Settings
  network: "devnet"     # Solana network (devnet, testnet, mainnet)
  rpc_url: "https://api.devnet.solana.com"  # Solana RPC URL
  mint_fee: 0.000005    # Fee for minting NFTs (in SOL)
  confirmation_timeout: 60000  # Transaction confirmation timeout (ms)
  retry_count: 3        # Number of retries for failed operations

  # Metadata Settings
  use_metadata_image_url: true  # Use image URL from metadata files
  default_image_url: "https://cyan-perfect-clam-972.mypinata.cloud/ipfs/bafkreifri6u3f3ww7u6v2gkkcfsol2ijqbno5qmc77n5h57hytebvtr6n4"

  # Pinata IPFS Settings
  use_pinata_metadata: true  # Use complete metadata files from Pinata
  pinata_base_uri: "https://cyan-perfect-clam-972.mypinata.cloud/ipfs/"  # Base URI for Pinata IPFS

  # Metadata CIDs for each NFT type
  metadata_cids:
    lucky_charm_1: "bafkreih5hvacyeu4ojl374t7s3bhdeje3xnwxkmyqz2cidsqto6k5pavhy"
    explosion_pickaxe_1: "bafkreiabcd1234567890abcdef1234567890abcdef1234567890"
    laser_pickaxe_1: "bafkreiefgh1234567890abcdef1234567890abcdef1234567890"
    # Add more CIDs for other NFT types

# NFT Inventory Settings
inventory:
  title: "NFT Inventory"  # Title of the NFT inventory GUI
  rows: 6                 # Number of rows in the inventory (max 6)
  auto_pagination: true   # Automatically create new pages when needed

# NFT Item Settings
nft_item:
  unbreakable: true       # Make NFT items unbreakable
  keep_on_death: true     # Keep NFT items on death
  allow_in_chests: true   # Allow storing NFT items in chests

# Enchantment Settings
enchantments:
  explosion:
    enabled: true
    max_level: 4          # Maximum level for Explosion Mining (1-4)
    cooldown: 0           # Cooldown between uses (seconds)

  laser:
    enabled: true
    max_level: 5          # Maximum level for Laser Mining (1-5)
    cooldown: 0           # Cooldown between uses (seconds)

# Buff Settings
buffs:
  luck:
    enabled: true
    max_value: 20         # Maximum luck buff value (percentage)
```

### Solana Backend Configuration (.env)

Create a `.env` file in the `plugins/NFTPlugin/solana-backend/` directory:

```
# IMPORTANT: You must set your Solana wallet private key here
# This wallet will be used to mint NFTs and pay for transaction fees
# The private key should be in base58 format
SOLANA_PRIVATE_KEY=your_private_key_here

# Solana network settings (devnet, testnet, mainnet)
SOLANA_NETWORK=devnet
SOLANA_RPC_URL=https://api.devnet.solana.com

# Mint fee in SOL (paid by the server wallet)
MINT_FEE=0.000005

# Transaction confirmation timeout (milliseconds)
CONFIRMATION_TIMEOUT=60000

# Number of retries for failed operations
RETRY_COUNT=5
```

### Metadata Files

Create JSON files in the `plugins/NFTPlugin/metadata/` directory for each NFT type:

**lucky_charm_1.json**:
```json
{
  "name": "Lucky Charm I",
  "symbol": "LUCK1",
  "description": "A magical charm that brings a small amount of luck",
  "image": "https://cyan-perfect-clam-972.mypinata.cloud/ipfs/bafybeieiot6gbstht27uxqqcug5l3ers5mstnrxwr3rronmcs2il5vdr5e",
  "attributes": [
    {
      "trait_type": "Type",
      "value": "Charm"
    },
    {
      "trait_type": "Rarity",
      "value": "Common"
    },
    {
      "trait_type": "Buff",
      "value": "Luck +1%"
    }
  ],
  "properties": {
    "files": [
      {
        "uri": "https://cyan-perfect-clam-972.mypinata.cloud/ipfs/bafybeieiot6gbstht27uxqqcug5l3ers5mstnrxwr3rronmcs2il5vdr5e",
        "type": "image/png"
      }
    ]
  },
  "quest": {
    "reward": {
      "item": "EMERALD",
      "name": "§a§lLucky Charm I",
      "lore": [
        "§7A magical charm that brings a small amount of luck",
        "§7Earned through good fortune",
        "§d§lBuff Effects:",
        "§b- Luck: §7+1% chance of better drops",
        "§7\"Fortune favors the bold\"",
        "§dRarity: §aCommon"
      ],
      "enchantments": [
        "LUCK:1"
      ],
      "unbreakable": true,
      "glowing": true,
      "custom_model_data": 8001
    },
    "buff": {
      "type": "LUCK",
      "value": 1
    }
  }
}
```

**explosion_pickaxe_1.json**:
```json
{
  "name": "Explosion Pickaxe I",
  "symbol": "EXPICK1",
  "description": "A pickaxe enchanted with explosive mining capabilities",
  "image": "https://cyan-perfect-clam-972.mypinata.cloud/ipfs/bafybeieiot6gbstht27uxqqcug5l3ers5mstnrxwr3rronmcs2il5vdr5e",
  "attributes": [
    {
      "trait_type": "Type",
      "value": "Tool"
    },
    {
      "trait_type": "Rarity",
      "value": "Rare"
    },
    {
      "trait_type": "Enchantment",
      "value": "Explosion Mining I"
    }
  ],
  "properties": {
    "files": [
      {
        "uri": "https://cyan-perfect-clam-972.mypinata.cloud/ipfs/bafybeieiot6gbstht27uxqqcug5l3ers5mstnrxwr3rronmcs2il5vdr5e",
        "type": "image/png"
      }
    ]
  },
  "quest": {
    "reward": {
      "item": "DIAMOND_PICKAXE",
      "name": "§b§lExplosion Pickaxe I",
      "lore": [
        "§7A pickaxe enchanted with explosive mining capabilities",
        "§7Mines blocks in a 3x3 area",
        "§d§lEnchantments:",
        "§b- Explosion Mining I",
        "§b- Unbreaking X",
        "§7\"One swing, many blocks\"",
        "§dRarity: §bRare"
      ],
      "enchantments": [
        "DURABILITY:10",
        "EXPLOSION_MINING:1"
      ],
      "unbreakable": true,
      "custom_model_data": 9001
    }
  }
}
```

## 💬 Commands

### Player Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/nftinv` | Open your NFT inventory | `nftplugin.command.nftinv` |
| `/nftlist` | View a list of your NFTs | `nftplugin.command.nftlist` |
| `/nfthelp` | Show help information | `nftplugin.command.nfthelp` |

### Admin Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/mintnft <username> <metadata_key>` | Mint an NFT for a player | `nftplugin.command.mintnft` |
| `/nftbuff <player>` | View a player's NFT buffs | `nftplugin.command.nftbuff` |

### Command Examples

**Minting an NFT for a player:**
```
/mintnft Steve lucky_charm_1
```

**Checking a player's NFT buffs:**
```
/nftbuff Steve
```

**Opening your NFT inventory:**
```
/nftinv
```

## 📖 Usage Guide

### For Players

#### Connecting Your Wallet

1. **Register Your Solana Wallet**:
   - Install the WalletLogin plugin on your server
   - Use `/connectwallet <wallet_address>` to link your Solana wallet
   - This allows you to receive NFTs on the Solana blockchain

#### Managing Your NFTs

1. **View Your NFT Inventory**:
   - Use `/nftinv` to open your virtual NFT inventory
   - Your NFT items will be displayed in a GUI
   - The inventory automatically creates new pages as needed
   - Click on any NFT to see detailed information

2. **List Your NFTs**:
   - Use `/nftlist` to see your NFTs in an interactive GUI
   - Click on any NFT to view detailed information including:
     - NFT name and description
     - NFT ID and transaction ID
     - Clickable Solana Explorer link to view on blockchain
     - Clickable image link to view the NFT artwork
   - Navigate between pages using the arrow buttons

3. **Get Help**:
   - Use `/nfthelp` to see available commands and information
   - Regular players will see only player commands

#### Using NFT Special Abilities

1. **Explosion Mining**:
   - Equip an Explosion Mining pickaxe
   - Mine a block to trigger the explosion effect
   - Blocks in a square area will be mined simultaneously
   - Area size depends on enchantment level:
     - Level I: 3x3 area
     - Level II: 4x4 area
     - Level III: 5x5 area
     - Level IV: 6x6 area

2. **Laser Mining**:
   - Equip a Laser Mining pickaxe
   - Mine a block to trigger the laser effect
   - Blocks in a straight line will be mined
   - Depth depends on enchantment level:
     - Level I: 2 blocks deep
     - Level II: 3 blocks deep
     - Level III: 4 blocks deep
     - Level IV: 5 blocks deep
     - Level V: 6 blocks deep

3. **Lucky Charm Buffs**:
   - Keep Lucky Charm items in your inventory
   - Buffs are applied automatically
   - Higher level charms provide better luck bonuses

### For Administrators

#### Managing NFTs

1. **Mint NFTs for Players**:
   - Use `/mintnft <player> <metadata_key>` to mint an NFT
   - Example: `/mintnft Steve lucky_charm_1`
   - The NFT will be minted on the Solana blockchain
   - A corresponding item will be added to the player's NFT inventory

2. **Check Player Buffs**:
   - Use `/nftbuff <player>` to view a player's active buffs
   - Example: `/nftbuff Steve`
   - This shows exact numeric values of all buffs

#### Setting Up NFTs

1. **Create Metadata Files**:
   - Create JSON files in the `plugins/NFTPlugin/metadata/` directory
   - Follow the format shown in the Configuration section
   - Include all required fields: name, description, image, etc.

2. **Upload to Pinata IPFS**:
   - Upload metadata files to Pinata IPFS
   - Get the CID (Content Identifier) for each file
   - Add CIDs to the `config.yml` file

3. **Configure the Plugin**:
   - Set up the Solana wallet for the server
   - Configure metadata URIs in `config.yml`
   - Customize enchantment and buff settings

## 🎮 NFT Lootbox System

The plugin includes a comprehensive lootbox system with different tiers and rarity levels:

### Lootbox Types

| Lootbox Type | Description | Rarity Distribution |
|--------------|-------------|---------------------|
| **Common Lootbox** | Basic lootbox with mostly common items | Common: 80%, Rare: 15%, Epic: 4%, Legendary: 1% |
| **Rare Lootbox** | Mid-tier lootbox with better chances | Common: 40%, Rare: 40%, Epic: 15%, Legendary: 5% |
| **Epic Lootbox** | High-tier lootbox with good odds | Common: 20%, Rare: 30%, Epic: 35%, Legendary: 15% |
| **Legendary Lootbox** | Top-tier lootbox with best chances | Common: 10%, Rare: 20%, Epic: 40%, Legendary: 30% |

### NFT Rarity Tiers

| Rarity | Description | Buff/Enchantment Range |
|--------|-------------|------------------------|
| **Common** | Basic NFTs with minor buffs | Luck: 1-5%, Enchantments: Level I |
| **Rare** | Uncommon NFTs with moderate buffs | Luck: 5-10%, Enchantments: Level II |
| **Epic** | Valuable NFTs with significant buffs | Luck: 10-15%, Enchantments: Level III |
| **Legendary** | Extremely rare NFTs with powerful buffs | Luck: 15-20%, Enchantments: Level IV |

### Lootbox Configuration

Lootbox settings can be configured in the `config.yml` file:

```yaml
lootboxes:
  common:
    enabled: true
    common_chance: 80
    rare_chance: 15
    epic_chance: 4
    legendary_chance: 1

  rare:
    enabled: true
    common_chance: 40
    rare_chance: 40
    epic_chance: 15
    legendary_chance: 5

  # Add more lootbox types as needed
```

## 🔧 Troubleshooting

### Common Issues and Solutions

#### Solana Integration Issues

| Issue | Solution |
|-------|----------|
| **"Failed to mint NFT"** | Ensure server wallet has sufficient SOL (at least 0.05 SOL) |
| **"Failed to load bindings"** | Run `npm rebuild` in the solana-backend directory |
| **"Error: NFT metadata is required"** | Ensure metadata files have name, description, and image fields |
| **"Cannot connect to RPC URL"** | Check network connection and RPC URL in config |

#### Node.js Backend Issues

If you encounter issues with the Node.js backend:

1. **Rebuild Node.js Dependencies**:
   ```bash
   cd plugins/NFTPlugin/solana-backend
   npm rebuild
   ```

2. **Update Solana Dependencies**:
   ```bash
   cd plugins/NFTPlugin/solana-backend
   npm install @metaplex-foundation/js@latest @solana/web3.js@latest
   ```

3. **Clean Install**:
   ```bash
   cd plugins/NFTPlugin/solana-backend
   rm -rf node_modules
   npm cache clean --force
   npm install
   ```

#### Testing the Solana Backend

To test the Solana backend directly:

```bash
cd plugins/NFTPlugin/solana-backend
node mint-nft.js --metadata-uri "https://cyan-perfect-clam-972.mypinata.cloud/ipfs/bafkreih5hvacyeu4ojl374t7s3bhdeje3xnwxkmyqz2cidsqto6k5pavhy" --recipient "5EQJH2HDC2oUbvMCTnYa7vwLsY2Um7Y6LAJsZoqxZQGz" --name "Lucky Charm I" --description "Test Description" --image "https://cyan-perfect-clam-972.mypinata.cloud/ipfs/bafybeieiot6gbstht27uxqqcug5l3ers5mstnrxwr3rronmcs2il5vdr5e"
```

#### Getting SOL for Testing

For testing on Solana devnet, you can get free SOL from:
- [Solana Faucet](https://solfaucet.com/)
- [QuickNode Faucet](https://faucet.quicknode.com/solana/devnet)

## 🚀 Creating New NFTs

### Step 1: Create Metadata File

Create a new JSON file in the `plugins/NFTPlugin/metadata/` directory with the following structure:

```json
{
  "name": "Your NFT Name",
  "symbol": "SYMBOL",
  "description": "Description of your NFT",
  "image": "https://your-pinata-ipfs-url.com/ipfs/your-image-cid",
  "attributes": [
    {
      "trait_type": "Type",
      "value": "Tool/Charm/Artifact"
    },
    {
      "trait_type": "Rarity",
      "value": "Common/Rare/Epic/Legendary"
    },
    {
      "trait_type": "Enchantment",
      "value": "Explosion Mining I"
    }
  ],
  "properties": {
    "files": [
      {
        "uri": "https://your-pinata-ipfs-url.com/ipfs/your-image-cid",
        "type": "image/png"
      }
    ]
  },
  "quest": {
    "reward": {
      "item": "DIAMOND_PICKAXE",
      "name": "§b§lYour NFT Name",
      "lore": [
        "§7Description line 1",
        "§7Description line 2",
        "§d§lEnchantments:",
        "§b- Enchantment description"
      ],
      "enchantments": [
        "DURABILITY:10",
        "EXPLOSION_MINING:1"
      ],
      "unbreakable": true,
      "custom_model_data": 9001
    }
  }
}
```

### Step 2: Upload to Pinata IPFS

1. Create an account on [Pinata](https://www.pinata.cloud/)
2. Upload your metadata file to Pinata
3. Get the CID (Content Identifier) of your uploaded file

### Step 3: Update Configuration

Add the CID to your `config.yml` file:

```yaml
solana:
  metadata_cids:
    your_nft_key: "your-metadata-cid-from-pinata"
```

### Step 4: Test Your NFT

Use the admin command to mint your new NFT:

```
/mintnft <player> your_nft_key
```

## 💻 Technical Details

### Solana Integration

The plugin uses the following technologies for Solana blockchain integration:

- **Metaplex SDK**: For creating and managing NFTs on Solana
- **Solana Web3.js**: For interacting with the Solana blockchain
- **Node.js Backend**: For handling blockchain operations
- **IPFS via Pinata**: For decentralized storage of metadata and images

#### Viewing NFTs on Solana Explorer

Players can view their NFTs on the Solana blockchain:

1. Use `/nftlist` or `/nftinv` and click on an NFT
2. Click the "Click to Open Explorer" button in the detailed view
3. This opens the Solana Explorer in a web browser
4. The Explorer shows all blockchain details about the NFT:
   - Owner address
   - Metadata
   - Transaction history
   - Token information

#### Viewing NFT Images

Players can view the actual NFT artwork:

1. Use `/nftlist` or `/nftinv` and click on an NFT
2. Click the "Open Image in Browser" button in the detailed view
3. This opens the NFT image stored on IPFS in a web browser

### Enchantment System

Custom enchantments are implemented using:

- **PersistentDataContainer**: For storing enchantment data on items
- **Custom Event Handlers**: For processing mining events
- **Efficient Algorithms**: For calculating affected blocks

### Buff System

The buff system provides in-game bonuses:

- **Luck Buffs**: Increase drop rates for rare items
- **Mining Buffs**: Enhance mining speed and efficiency
- **Stackable Effects**: Multiple buffs can be combined

### NFT Inventory

The virtual NFT inventory system features:

- **Custom GUI**: User-friendly interface for managing NFTs
- **Unlimited Pagination**: Automatically creates new pages as needed
- **Persistent Storage**: NFTs are saved between sessions

## 📞 Support and Contact

If you need help with the plugin:

- **GitHub Issues**: Report bugs and request features
- **Discord**: Join our community for support
- **Documentation**: Refer to this README for detailed information

---

<div align="center">

### ⭐ Enjoy your NFT adventures in Minecraft! ⭐

[<img src="https://img.shields.io/badge/Solana-Devnet-blue?style=for-the-badge&logo=solana" alt="Solana Devnet">](https://explorer.solana.com/?cluster=devnet)
[<img src="https://img.shields.io/badge/IPFS-Pinata-orange?style=for-the-badge&logo=ipfs" alt="IPFS Pinata">](https://www.pinata.cloud/)

</div>
