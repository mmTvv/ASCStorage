package ord.d2d2.ASCStorage.utils;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.block.ShulkerBox;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import ord.d2d2.ASCStorage.ASCStoragePlugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.inventory.Inventory;

public class ShulkerUtils {
    // Кэш для хранения изменений до записи в БД
    private static final ConcurrentHashMap<String, String> pendingShulkerUpdates = new ConcurrentHashMap<>();

    public static boolean isShulkerBox(ItemStack item) {
        return item != null && item.getType().toString().endsWith("_SHULKER_BOX");
    }

    public static void saveShulkerToCache(String uuid, String contents) {
        pendingShulkerUpdates.put(uuid, contents);
    }

    // Функция для сброса всех изменений из кэша в базу данных
    public static void flushPendingChangesToDatabase() {
        if (pendingShulkerUpdates.isEmpty()) return;

        try (Connection conn = ASCStoragePlugin.getInstance().getDatabaseManager().getConnection()) {
            String sql = "INSERT OR REPLACE INTO shulkers (uuid, content) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                for (String uuid : pendingShulkerUpdates.keySet()) {
                    stmt.setString(1, uuid);
                    stmt.setString(2, pendingShulkerUpdates.get(uuid));
                    stmt.addBatch(); // Используем пакетную вставку
                }
                stmt.executeBatch(); // Выполняем пакетное обновление
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Очищаем кэш после записи в базу данных
        pendingShulkerUpdates.clear();
    }

    // Сохранение шалкера в кэш, а не сразу в базу данных
    public static void saveShulkerToDatabase(ItemStack shulkerItem) {
        if (shulkerItem == null || !isShulkerBox(shulkerItem)) return;

        BlockStateMeta meta = (BlockStateMeta) shulkerItem.getItemMeta();
        ShulkerBox shulkerBox = (ShulkerBox) meta.getBlockState();

        String uuid = getShulkerUUID(meta);
        if (uuid == null) {
            uuid = UUID.randomUUID().toString(); // Генерация нового UUID, если его нет
        }

        String contentsString = serializeInventory(shulkerBox.getInventory().getContents());
        saveShulkerToCache(uuid, contentsString); // Сохраняем в кэш вместо немедленной записи в БД

        // Очищаем содержимое шалкера и обновляем lore
        shulkerBox.getInventory().clear();
        meta.setBlockState(shulkerBox);
        meta.setLore(List.of("UUID: " + uuid));
        shulkerItem.setItemMeta(meta);
    }

    private static String getShulkerUUID(ItemMeta meta) {
        if (meta.hasLore()) {
            for (String line : meta.getLore()) {
                if (line.startsWith("UUID: ")) {
                    return line.replace("UUID: ", "");
                }
            }
        }
        return null;
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

    public static void deleteShulkerData(String shulkerUUID) {
        String sql = "DELETE FROM shulkers WHERE uuid = ?";
        try (Connection conn = ASCStoragePlugin.getInstance().getDatabaseManager().getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, shulkerUUID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
