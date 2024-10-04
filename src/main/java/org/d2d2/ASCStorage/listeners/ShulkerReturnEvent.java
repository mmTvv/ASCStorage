package ord.d2d2.ASCStorage.listeners;

import ord.d2d2.ASCStorage.utils.ShulkerUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.block.ShulkerBox;

import java.util.List;

public class ShulkerReturnEvent implements Listener {

    @EventHandler
    public void onChestOpen(InventoryOpenEvent event) {
        Inventory inventory = event.getInventory();
        InventoryHolder holder = inventory.getHolder();

        // Проверка, является ли сундук обычным или сдвоенным
        if (holder instanceof org.bukkit.block.Chest chest) {
            openChest(chest.getInventory().getContents());
        } else if (holder instanceof org.bukkit.block.DoubleChest doubleChest) {
            openChest(doubleChest.getInventory().getContents());
        }
    }

    @EventHandler
    public void onChestClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        InventoryHolder holder = inventory.getHolder();

        // Здесь мы можем добавить логику для сохранения шалкеров при закрытии сундуков
        if (holder instanceof org.bukkit.block.Chest chest) {
            closeChest(chest.getInventory().getContents());
        } else if (holder instanceof org.bukkit.block.DoubleChest doubleChest) {
            closeChest(doubleChest.getInventory().getContents());
        }
    }

    // Открытие сундука и загрузка содержимого шалкеров
    private void openChest(ItemStack[] contents) {
        for (ItemStack item : contents) {
            if (item != null && ShulkerUtils.isShulkerBox(item)) {
                ItemMeta meta = item.getItemMeta();
                if (meta.hasLore()) {
                    List<String> lore = meta.getLore();
                    for (String line : lore) {
                        if (line.startsWith("UUID: ")) {
                            String shulkerUUID = line.replace("UUID: ", "");
                            BlockStateMeta im = (BlockStateMeta) item.getItemMeta();
                            ShulkerBox shulker = (ShulkerBox) im.getBlockState();
                            shulker.getInventory().setContents(ShulkerUtils.loadShulkerContentsFromDatabase(shulkerUUID));
                            im.setBlockState(shulker);
                            item.setItemMeta(im);
                        }
                    }
                }
            }
        }
    }

    // Сохранение содержимого шалкеров при закрытии сундука
    private void closeChest(ItemStack[] contents) {
        for (ItemStack item : contents) {
            if (item != null && ShulkerUtils.isShulkerBox(item)) {
                ShulkerUtils.saveShulkerToDatabase(item);
            }
        }
    }
}
