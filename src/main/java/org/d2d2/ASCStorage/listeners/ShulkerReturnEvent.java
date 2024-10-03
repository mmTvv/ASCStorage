package ord.d2d2.ASCStorage.listeners;

import ord.d2d2.ASCStorage.ASCStoragePlugin;
import ord.d2d2.ASCStorage.utils.ShulkerUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
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
        if (holder instanceof org.bukkit.block.Chest) {
            org.bukkit.block.Chest chest = (org.bukkit.block.Chest) holder;
            openChest(chest.getInventory().getContents());
            
        } else if (holder instanceof org.bukkit.block.DoubleChest) {
            org.bukkit.block.DoubleChest doubleChest = (org.bukkit.block.DoubleChest) holder;
            openChest(doubleChest.getInventory().getContents());
        }
    }

    @EventHandler
    public void onChestClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        InventoryHolder holder = inventory.getHolder();

        // Проверка, является ли сундук обычным или сдвоенным
        if (holder instanceof org.bukkit.block.Chest) {
            org.bukkit.block.Chest chest = (org.bukkit.block.Chest) holder;
            //closeChest(chest.getInventory().getContents());
            
        } else if (holder instanceof org.bukkit.block.DoubleChest) {
            org.bukkit.block.DoubleChest doubleChest = (org.bukkit.block.DoubleChest) holder;
            //closeChest(doubleChest.getInventory().getContents());
        }
    }

    // Методы для обработки содержимого инвентаря
    private void openChest(ItemStack[] contents) {
        for (ItemStack item : contents) {
            if (item != null) {
                if (ShulkerUtils.isShulkerBox(item)) {
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
    }

//    private void closeChest(ItemStack[] contents) {
//        for (ItemStack item : contents) {
//            if (item != null) {
//                if (ShulkerUtils.isShulkerBox(item)) {
//                    ShulkerUtils.saveShulkerToDatabase(item);
//                }
//            }
//        }
//    }
}

