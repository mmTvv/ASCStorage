package ord.d2d2.ASCStorage.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ord.d2d2.ASCStorage.utils.ShulkerUtils;
import org.bukkit.Bukkit;

import java.util.List;

public class DelUUIDonMove implements Listener {

    @EventHandler
    public void onItemMove(InventoryClickEvent event) {
        // Проверяем, что предмет перемещается в инвентарь игрока
        if (event.getClickedInventory() != null && event.getWhoClicked().getInventory() != null) {
            ItemStack item = event.getCurrentItem();

            if (item != null && item.hasItemMeta() && ShulkerUtils.isShulkerBox(item)) {
                ItemMeta meta = item.getItemMeta();

                // Проверяем, есть ли lore у предмета
                if (meta.hasLore()) {
                	List<String> lore = meta.getLore();
	                for (String line : lore) {
	                    if (line.startsWith("UUID: ")) {
	                        String shulkerUUID = line.replace("UUID: ", "");
		                    ShulkerUtils.deleteShulkerData(shulkerUUID);
        		            meta.setLore(null);
                		    item.setItemMeta(meta);
                		}
                	}
                }
            }
        }
    }
}
