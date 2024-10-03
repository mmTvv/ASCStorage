package ord.d2d2.ASCStorage.listeners;

import ord.d2d2.ASCStorage.ASCStoragePlugin;
import ord.d2d2.ASCStorage.utils.ShulkerUtils;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.ShulkerBox;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Bukkit;

public class ChunkLoadListener implements Listener {

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {

        Chunk chunk = event.getChunk();
        World world = chunk.getWorld();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();

        // Используем RegionScheduler с правильными аргументами (Plugin, World, X, Z, Runnable)
        ASCStoragePlugin.getInstance().getServer().getRegionScheduler().execute(ASCStoragePlugin.getInstance(), world, chunkX, chunkZ, () -> {
            for (BlockState blockState : chunk.getTileEntities()) {
                if (blockState instanceof Chest chest) {
                    for (ItemStack item : chest.getBlockInventory().getContents()) {
                        if (ShulkerUtils.isShulkerBox(item)) {
                            ShulkerUtils.LoadChunks(item);
                        }
                    }
                } 
            }
        });
    }
}

