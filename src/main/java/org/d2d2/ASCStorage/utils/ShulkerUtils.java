package ord.d2d2.ASCStorage.utils;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.block.ShulkerBox;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.Material;
import ord.d2d2.ASCStorage.ASCStoragePlugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class ShulkerUtils {

    public static boolean isShulkerBox(ItemStack item) {
        return item != null && item.getType().toString().endsWith("_SHULKER_BOX");
    }

    public static void saveShulkerToDatabase(ItemStack shulkerItem) {
        if (shulkerItem == null || !isShulkerBox(shulkerItem)) return;

        BlockStateMeta meta = (BlockStateMeta) shulkerItem.getItemMeta();
        ShulkerBox shulkerBox = (ShulkerBox) meta.getBlockState();
        Inventory inventory = shulkerBox.getInventory();
        ItemStack[] contents = inventory.getContents();

        UUID shulkerUUID = UUID.randomUUID(); // Генерируем UUID для шалкера
        String contentsString = serializeInventory(contents);

        try (Connection conn = ASCStoragePlugin.getInstance().getDatabaseManager().getConnection()) {
            String sql = "INSERT OR REPLACE INTO shulkers (uuid, content) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, shulkerUUID.toString());
            stmt.setString(2, contentsString);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Заменяем содержимое шалкера на UUID
        shulkerBox.getInventory().clear();
        meta.setBlockState(shulkerBox);
        shulkerItem.setItemMeta(meta);
    }

    public static String serializeInventory(ItemStack[] contents) {
        StringBuilder serialized = new StringBuilder();
        for (ItemStack item : contents) {
            if (item != null) {
                serialized.append(item.getType()).append(":").append(item.getAmount()).append(";");
            }
        }
        return serialized.toString();
    }

    public static ItemStack[] deserializeInventory(String data) {
        String[] items = data.split(";");
        ItemStack[] contents = new ItemStack[items.length];
        for (int i = 0; i < items.length; i++) {
            String[] itemData = items[i].split(":");
            Material material = Material.getMaterial(itemData[0]);
            int amount = Integer.parseInt(itemData[1]);
            contents[i] = new ItemStack(material, amount);
        }
        return contents;
    }

    public static ItemStack[] loadShulkerContentsFromDatabase(UUID uuid) {
        try (Connection conn = ASCStoragePlugin.getInstance().getDatabaseManager().getConnection()) {
            String sql = "SELECT content FROM shulkers WHERE uuid = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, uuid.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return deserializeInventory(rs.getString("content"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ItemStack[0];
    }
}

