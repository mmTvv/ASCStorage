package ord.d2d2.ASCStorage.listeners;

import ord.d2d2.ASCStorage.ASCStoragePlugin;
import ord.d2d2.ASCStorage.utils.ShulkerUtils;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.ShulkerBox;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.ItemStack;

public class ChunkLoadListener implements Listener {

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(ASCStoragePlugin.getInstance(), () -> {
            for (Block block : event.getChunk().getTileEntities()) {
                if (block instanceof Chest chest) {
                    for (ItemStack item : chest.getBlockInventory().getContents()) {
                        if (ShulkerUtils.isShulkerBox(item)) {
                            ShulkerUtils.saveShulkerToDatabase(item);
                        }
                    }
                } else if (block instanceof ShulkerBox shulkerBox) {
                    ItemStack shulkerItem = shulkerBox.getInventory().getItem(0);
                    ShulkerUtils.saveShulkerToDatabase(shulkerItem);
                }
            }
        });
    }
}

