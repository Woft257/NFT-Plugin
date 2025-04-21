# ✨ Minecraft NFT Plugin ✨

A comprehensive Solana NFT integration plugin for Minecraft, allowing players to receive, store, and use NFTs with special abilities in-game. This plugin creates a seamless bridge between Minecraft gameplay and Solana blockchain technology, enhancing player experience with unique collectible items.

<div align="center">

[<kbd>Download Latest Release</kbd>](https://github.com/Woft257/nft-plugin/releases) &nbsp;&nbsp;&nbsp;
[<kbd>View Documentation</kbd>](https://github.com/Woft257/nft-plugin/wiki) &nbsp;&nbsp;&nbsp;
[<kbd>Report an Issue</kbd>](https://github.com/Woft257/nft-plugin/issues/new)

</div>

## ✅ Features

- **Mint NFTs on Solana** when players achieve in-game accomplishments
- **Virtual NFT Inventory** with unlimited pagination for storing and managing NFTs
- **Functional NFT Items** with special abilities:
  - **Explosion Pickaxes** (Levels 1-5): Mine in 3x3 to 7x7 areas
  - **Laser Pickaxes** (Levels 1-5): Mine up to 6 blocks deep
  - **Lucky Charms** (Levels 1-20): Increase rare item drop rates
- **SolanaLogin Integration** to link Solana wallets with Minecraft accounts
- **Lootbox System** with tiered rarity (Common, Rare, Epic, Legendary, Mythic)
- **NFT Fusion System** to combine lower-tier NFTs into higher-tier ones
- **Admin Commands** for minting NFTs and managing player inventories
- **Enhanced NFT Display** with compact and user-friendly information
- **Interactive UI** with clickable buttons for Solana Explorer and image links
- **Visual Enhancements** for better user experience

## ⚙️ Requirements

- Minecraft Paper 1.18.2
- Java 17 or higher
- Node.js 16 or higher (for Solana backend)
- SolanaLogin Plugin (for wallet linking)
- MySQL/MariaDB (for data storage)

## 💾 Installation

1. Download the latest JAR file from [<kbd>Download Latest Release</kbd>](https://github.com/Woft257/nft-plugin/releases)
2. Place the JAR file in your Minecraft server's `plugins` directory
3. Start the server to generate configuration files
4. Configure the plugin in `plugins/NFTPlugin/config.yml`
5. Set up the Solana backend:
   ```bash
   cd plugins/NFTPlugin/solana-backend
   npm install
   ```
6. Configure the Solana backend in `plugins/NFTPlugin/solana-backend/.env`
7. Restart the server

## 🔧 Configuration

### config.yml

```yaml
# Database Configuration
database:
  host: localhost
  port: 3306
  database: minecraft
  username: root
  password: your_password
  table-prefix: nftplugin_

# Achievement Settings
achievements:
  # Great Light - Blaze Rod
  great_light:
    enabled: true
    type: named_item
    material: BLAZE_ROD
    item_name: "Great Light"

  # Ancient Scroll - Paper item
  ancient_scroll:
    enabled: true
    type: named_item
    material: PAPER
    item_name: "Ancient Scroll"

# Solana Settings
solana:
  network: "devnet"
  rpc_url: "https://api.devnet.solana.com"
  server_wallet_private_key: "" # Do not fill this in here! Use the SOLANA_PRIVATE_KEY environment variable
  mint_fee: 0.000005
```

### Solana Backend Configuration (.env)

Create a `.env` file in the `plugins/NFTPlugin/solana-backend/` directory with the following content:

```
# Server wallet private key (base58 format)
SOLANA_PRIVATE_KEY=your_private_key_here

# Solana network (devnet, testnet, mainnet)
SOLANA_NETWORK=devnet

# Solana RPC URL
SOLANA_RPC_URL=https://api.devnet.solana.com

# NFT minting fee (SOL)
MINT_FEE=0.000005

# Transaction confirmation timeout (milliseconds)
CONFIRMATION_TIMEOUT=60000

# Number of retry attempts on error
RETRY_COUNT=5
```

### Metadata Files

Create JSON files in the `plugins/NFTPlugin/metadata/` directory for each achievement:

**great_light.json**:
```json
{
  "name": "Great Light Staff",
  "description": "A mystical staff containing the power of the great light",
  "image": "https://cyan-perfect-clam-972.mypinata.cloud/ipfs/bafkreifri6u3f3ww7u6v2gkkcfsol2ijqbno5qmc77n5h57hytebvtr6n4",
  "attributes": [
    {
      "trait_type": "Type",
      "value": "Weapon"
    },
    {
      "trait_type": "Rarity",
      "value": "Legendary"
    }
  ],
  "quest": {
    "type": "HOLD_NAMED_ITEM_INSTANT",
    "target": "BLAZE_ROD",
    "target_name": "Great Light",
    "duration": 0,
    "description": "Hold a Blaze Rod named 'Great Light'"
  }
}
```

**ancient_scroll.json**:
```json
{
  "name": "Ancient Scroll",
  "description": "A mysterious scroll containing ancient knowledge",
  "image": "https://cyan-perfect-clam-972.mypinata.cloud/ipfs/bafkreifri6u3f3ww7u6v2gkkcfsol2ijqbno5qmc77n5h57hytebvtr6n4",
  "attributes": [
    {
      "trait_type": "Type",
      "value": "Artifact"
    },
    {
      "trait_type": "Rarity",
      "value": "Epic"
    }
  ],
  "quest": {
    "type": "HOLD_NAMED_ITEM_INSTANT",
    "target": "PAPER",
    "target_name": "Ancient Scroll",
    "duration": 0,
    "description": "Hold a Paper item named 'Ancient Scroll'"
  }
}
```

## 💬 Commands

### Player Commands
- `/nftinfo` - Display information about the NFT item currently held in hand
- `/nftlist` - View a list of all your NFTs with pagination
- `/nftinv` - Open your virtual NFT inventory to store and manage NFTs

### Admin Commands
- `/resetnft <player>` - Reset a player's achievement and NFT progress (Admin only)
- `/mintnft <player> <metadata_key>` - Manually mint an NFT for a player (Admin only)
- `/nftbuff <player>` - View a player's active NFT buffs and their values (Admin only)

## 📖 Usage Guide

### For Players

1. **Register Solana Wallet**:
   - Register your Solana wallet using the SolanaLogin plugin
   - Use the command `/connectwallet <wallet_address>`

2. **Obtain NFTs**:
   - Complete in-game achievements to earn NFTs
   - NFTs will be minted on the Solana blockchain and added to your virtual inventory
   - Use `/nftinv` to access your NFT inventory

3. **Use NFT Abilities**:
   - **Explosion Pickaxes**: Mine blocks in a square area (3x3 to 7x7)
   - **Laser Pickaxes**: Mine blocks in depth (2 to 6 blocks deep)
   - **Lucky Charms**: Increase your chance of finding rare items

4. **View NFT Information**:
   - Hold an NFT item and use `/nftinfo` to see detailed information
   - Use `/nftlist` to browse all your NFTs with pagination

### For Admins

1. **Mint NFTs for Players**:
   - Use `/mintnft <player> <metadata_key>` to mint an NFT for a player
   - The NFT will be added directly to the player's virtual inventory

2. **Check Player Buffs**:
   - Use `/nftbuff <player>` to view a player's active NFT buffs
   - This shows exact numeric values without percentage symbols

3. **Reset Player Progress**:
   - Use `/resetnft <player>` to reset a player's NFT progress
   - This will remove all NFTs from their inventory and database records

## 🎮 NFT Rarity System

NFTs are categorized into 5 rarity tiers with different drop rates:

1. 🍃 **Common** (60%): Basic NFTs with minor buffs
2. 🍀 **Rare** (25%): Uncommon NFTs with moderate buffs
3. 🔥 **Epic** (10%): Powerful NFTs with significant buffs
4. 💎 **Legendary** (4%): Very rare NFTs with major buffs
5. 🌀 **Mythic** (1%): Extremely rare NFTs with exceptional buffs

For detailed information about the NFT rarity system and lootboxes, see [NFT-Lootbox-System.md](NFT-Lootbox-System.md).

## 🔧 Troubleshooting

### "Signature is not valid" Error

If you encounter a "Signature is not valid" error when minting NFTs:

1. **Check Server Wallet Balance**:
   - Ensure the server wallet has sufficient SOL (at least 0.05 SOL)
   - Add SOL to the server wallet from the Solana Faucet: [<kbd>Get SOL from Faucet</kbd>](https://solfaucet.com/)

2. **Try Alternative RPC URL**:
   - Change the RPC_URL in the `.env` file to `https://devnet.genesysgo.net/`

3. **Update Metaplex**:
   ```bash
   cd plugins/NFTPlugin/solana-backend
   npm install @metaplex-foundation/js@latest
   ```

4. **Clear Cache and Reinstall**:
   ```bash
   cd plugins/NFTPlugin/solana-backend
   rm -rf node_modules
   npm cache clean --force
   npm install
   ```

### Backend Testing Command

To test the Solana backend directly:

```bash
cd plugins/NFTPlugin/solana-backend
node mint-nft.js \
  --network devnet \
  --rpc-url https://api.devnet.solana.com \
  --private-key your_private_key \
  --recipient recipient_wallet_address \
  --name "Test NFT" \
  --description "This is a test NFT" \
  --image "https://cyan-perfect-clam-972.mypinata.cloud/ipfs/bafkreifri6u3f3ww7u6v2gkkcfsol2ijqbno5qmc77n5h57hytebvtr6n4" \
  --player "TestPlayer" \
  --achievement "test_achievement"
```

## ✨ Adding New NFTs

To add a new NFT:

1. **Create a Metadata File**:
   - Create a new JSON file in the `plugins/NFTPlugin/metadata/` directory
   - Name the file according to the format `<nft_key>.json`
   - Include all necessary fields: name, description, image, attributes, and quest

2. **Restart the Server**:
   - The plugin will automatically detect and load the new NFT metadata
   - No need to modify config.yml for new NFTs

### NFT Abilities Troubleshooting

If NFT abilities like explosion mining or laser mining aren't working:

1. **Check Permissions**:
   - Ensure the player has the `nftplugin.use.abilities` permission
   - Add the permission with: `/lp user <player> permission set nftplugin.use.abilities true`

2. **Verify Configuration**:
   - Make sure abilities are enabled in the config.yml file
   - Check that the NFT metadata file has the correct enchantment entries

3. **Debug Mode**:
   - Enable debug mode in config.yml to see detailed logs
   - Check the console for any error messages related to NFT abilities

## 🔐 License

This plugin is released under the MIT License.

## 📬 Contact

If you have any questions or encounter issues, please create an issue on GitHub or contact via email: your.email@example.com

---

<div align="center">

### ⭐ Enjoy using the Minecraft NFT Plugin! ⭐

[<kbd>Report an Issue</kbd>](https://github.com/Woft257/nft-plugin/issues/new) &nbsp;&nbsp;&nbsp; [<kbd>Request a Feature</kbd>](https://github.com/Woft257/nft-plugin/issues/new?labels=enhancement) &nbsp;&nbsp;&nbsp; [<kbd>View on GitHub</kbd>](https://github.com/Woft257/nft-plugin)

</div>
