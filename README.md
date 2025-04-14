# Minecraft NFT Plugin

Plugin tích hợp Solana NFT cho Minecraft, cho phép người chơi nhận NFT khi đạt được các thành tựu trong game.

## Tính năng

- **Mint NFT trên Solana DevNet** khi người chơi đạt được thành tựu
- **Tích hợp với SolanaLogin** để liên kết ví Solana với tài khoản Minecraft
- **Hệ thống thành tựu linh hoạt** dựa trên việc cầm vật phẩm có tên đặc biệt
- **Lưu trữ NFT trong game** dưới dạng vật phẩm đặc biệt không thể rơi hoặc mất
- **Cấu hình dễ dàng** thông qua file config.yml và metadata JSON
- **Thêm thành tựu mới** mà không cần biên dịch lại plugin

## Yêu cầu

- Minecraft Paper 1.18.2
- Java 17 hoặc cao hơn
- Node.js 16 hoặc cao hơn (cho backend Solana)
- Plugin SolanaLogin (để liên kết ví Solana)
- MySQL/MariaDB (để lưu trữ dữ liệu)

## Cài đặt

1. Tải file JAR mới nhất từ [Releases](https://github.com/yourusername/nft-plugin/releases)
2. Đặt file JAR vào thư mục `plugins` của server Minecraft
3. Khởi động server để tạo các file cấu hình
4. Cấu hình plugin trong `plugins/NFTPlugin/config.yml`
5. Cài đặt backend Solana:
   ```bash
   cd plugins/NFTPlugin/solana-backend
   npm install
   ```
6. Cấu hình backend Solana trong `plugins/NFTPlugin/solana-backend/.env`
7. Khởi động lại server

## Cấu hình

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
  server_wallet_private_key: "" # Không điền vào đây! Sử dụng biến môi trường SOLANA_PRIVATE_KEY
  mint_fee: 0.000005
```

### Cấu hình Backend Solana (.env)

Tạo file `.env` trong thư mục `plugins/NFTPlugin/solana-backend/` với nội dung:

```
# Private key của ví Solana server (dạng base58)
SOLANA_PRIVATE_KEY=your_private_key_here

# Mạng Solana (devnet, testnet, mainnet)
SOLANA_NETWORK=devnet

# RPC URL của Solana
SOLANA_RPC_URL=https://api.devnet.solana.com

# Phí mint NFT (SOL)
MINT_FEE=0.000005

# Thời gian chờ xác nhận giao dịch (milliseconds)
CONFIRMATION_TIMEOUT=60000

# Số lần thử lại khi gặp lỗi
RETRY_COUNT=5
```

### Metadata Files

Tạo file JSON trong thư mục `plugins/NFTPlugin/metadata/` cho mỗi thành tựu:

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
    "description": "Cầm Que Quỷ Lửa (Blaze Rod) được đặt tên 'Great Light'"
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
    "description": "Cầm Giấy (Paper) được đặt tên 'Ancient Scroll'"
  }
}
```

## Lệnh

- `/nftinfo` - Hiển thị thông tin về NFT đang cầm trên tay
- `/resetnft <player>` - Đặt lại tiến trình thành tựu và NFT của người chơi (chỉ Admin)

## Cách sử dụng

1. **Đăng ký ví Solana**:
   - Người chơi cần đăng ký ví Solana của họ bằng plugin SolanaLogin
   - Sử dụng lệnh `/connectwallet <địa_chỉ_ví>` từ plugin SolanaLogin

2. **Đạt được thành tựu**:
   - Người chơi cần tìm và cầm vật phẩm có tên đặc biệt
   - Khi cầm vật phẩm, plugin sẽ tự động mint NFT và gửi đến ví Solana của người chơi
   - Vật phẩm gốc sẽ bị xóa và thay thế bằng vật phẩm NFT trong game

3. **Xem thông tin NFT**:
   - Cầm vật phẩm NFT và sử dụng lệnh `/nftinfo`
   - Thông tin chi tiết về NFT sẽ được hiển thị, bao gồm liên kết đến Solana Explorer

## Khắc phục sự cố

### Lỗi "Signature is not valid"

Nếu gặp lỗi "Signature is not valid" khi mint NFT:

1. **Kiểm tra số dư ví server**:
   - Đảm bảo ví server có đủ SOL (ít nhất 0.05 SOL)
   - Nạp SOL vào ví server từ [Solana Faucet](https://solfaucet.com/)

2. **Thử RPC URL thay thế**:
   - Thay đổi RPC_URL trong file `.env` thành `https://devnet.genesysgo.net/`

3. **Cập nhật Metaplex**:
   ```bash
   cd plugins/NFTPlugin/solana-backend
   npm install @metaplex-foundation/js@latest
   ```

4. **Xóa cache và cài đặt lại**:
   ```bash
   cd plugins/NFTPlugin/solana-backend
   rm -rf node_modules
   npm cache clean --force
   npm install
   ```

### Lệnh test backend

Để test backend Solana trực tiếp:

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

## Thêm thành tựu mới

Để thêm thành tựu mới:

1. **Tạo file metadata**:
   - Tạo file JSON mới trong thư mục `plugins/NFTPlugin/metadata/`
   - Đặt tên file theo định dạng `<achievement_key>.json`

2. **Cập nhật config.yml**:
   - Thêm mục mới trong phần `achievements` của file `config.yml`

Ví dụ thêm thành tựu "Diamond Sword":

**diamond_sword.json**:
```json
{
  "name": "Diamond Sword of Power",
  "description": "A legendary diamond sword with immense power",
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
    "target": "DIAMOND_SWORD",
    "target_name": "Sword of Power",
    "duration": 0,
    "description": "Cầm Kiếm Kim Cương (Diamond Sword) được đặt tên 'Sword of Power'"
  }
}
```

**Cập nhật config.yml**:
```yaml
achievements:
  # Các thành tựu hiện có...
  
  # Diamond Sword
  diamond_sword:
    enabled: true
    type: named_item
    material: DIAMOND_SWORD
    item_name: "Sword of Power"
```

## Giấy phép

Plugin này được phát hành dưới giấy phép MIT.

## Liên hệ

Nếu bạn có bất kỳ câu hỏi hoặc gặp vấn đề, vui lòng tạo issue trên GitHub hoặc liên hệ qua email: your.email@example.com
