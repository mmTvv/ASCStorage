package ord.d2d2.ASCStorage.utils;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.block.ShulkerBox;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.bukkit.Bukkit;
import ord.d2d2.ASCStorage.ASCStoragePlugin;
import org.bukkit.inventory.meta.BlockStateMeta;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

public class ShulkerUtils {
    public static boolean isShulkerBox(ItemStack item) {
        return item != null && item.getType().toString().endsWith("_SHULKER_BOX");
    }

    public static void LoadChunks(ItemStack shulkerItem) {
        if (shulkerItem == null || !isShulkerBox(shulkerItem)) return;
        BlockStateMeta meta = (BlockStateMeta) shulkerItem.getItemMeta();
        ShulkerBox shulkerBox = (ShulkerBox) meta.getBlockState();
        if (!meta.hasLore()) {
            UUID shulkerUUID = UUID.randomUUID(); // Generate UUID for shulker
            Inventory inv = shulkerBox.getInventory();

            String contentsString = serializeInventory(inv.getContents());
            try (Connection conn = ASCStoragePlugin.getInstance().getDatabaseManager().getConnection()) {
                String sql = "INSERT OR REPLACE INTO shulkers (uuid, content) VALUES (?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, shulkerUUID.toString());
                    stmt.setString(2, contentsString);
                    stmt.executeUpdate();
                    // Clear the contents of the shulker box
                    shulkerBox.getInventory().clear();
                    meta.setBlockState(shulkerBox);
                    meta.setLore(List.of("UUID: " + shulkerUUID.toString())); // Update lore
                    shulkerItem.setItemMeta(meta);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveShulkerToDatabase(ItemStack shulkerItem) {
        if (shulkerItem == null || !isShulkerBox(shulkerItem)) return;
        BlockStateMeta meta = (BlockStateMeta) shulkerItem.getItemMeta();
        ShulkerBox shulkerBox = (ShulkerBox) meta.getBlockState();
        
        if (meta.hasLore()) {
            List<String> lore = meta.getLore();
            for (String line : lore) {
                if (line.startsWith("UUID: ")) {
                    String shulkerUUID = line.replace("UUID: ", "");
                    Inventory inv = shulkerBox.getInventory();

                    String contentsString = serializeInventory(inv.getContents());
                    try (Connection conn = ASCStoragePlugin.getInstance().getDatabaseManager().getConnection()) {
                        String sql = "INSERT OR REPLACE INTO shulkers (uuid, content) VALUES (?, ?)";
                        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                            stmt.setString(1, shulkerUUID);
                            stmt.setString(2, contentsString);
                            stmt.executeUpdate();
                            // Clear the contents of the shulker box
                            shulkerBox.getInventory().clear();
                            meta.setBlockState(shulkerBox);
                            meta.setLore(List.of("UUID: " + shulkerUUID.toString())); // Update lore
                            shulkerItem.setItemMeta(meta);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            UUID shulkerUUID = UUID.randomUUID(); // Generate UUID for shulker
            Inventory inv = shulkerBox.getInventory();

            String contentsString = serializeInventory(inv.getContents());
            try (Connection conn = ASCStoragePlugin.getInstance().getDatabaseManager().getConnection()) {
                String sql = "INSERT OR REPLACE INTO shulkers (uuid, content) VALUES (?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, shulkerUUID.toString());
                    stmt.setString(2, contentsString);
                    stmt.executeUpdate();
                    // Clear the contents of the shulker box
                    shulkerBox.getInventory().clear();
                    meta.setBlockState(shulkerBox);
                    meta.setLore(List.of("UUID: " + shulkerUUID.toString())); // Update lore
                    shulkerItem.setItemMeta(meta);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static String serializeInventory(ItemStack[] contents) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try (BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)) {
                dataOutput.writeObject(contents.clone());
            }
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static ItemStack[] deserializeInventory(String data) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            try (BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream)) {
                return (ItemStack[]) dataInput.readObject();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ItemStack[] loadShulkerContentsFromDatabase(String uuid) {
        try (Connection conn = ASCStoragePlugin.getInstance().getDatabaseManager().getConnection()) {
            String sql = "SELECT content FROM shulkers WHERE uuid = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, uuid);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return deserializeInventory(rs.getString("content"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ItemStack[0];
    }
    public static String deleteShulkerData(String shulkerUUID) {
        String sql = "DELETE FROM shulkers WHERE uuid = ?";
        
        try (Connection conn = ASCStoragePlugin.getInstance().getDatabaseManager().getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setString(1, shulkerUUID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
