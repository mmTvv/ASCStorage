package org.d2d2.ASCStorage;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.Chunk;

public class EventListener implements Listener {
    private final DatabaseManager databaseManager;

    public EventListener(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        Chunk chunk = event.getChunk();
        for (BlockState blockState : chunk.getTileEntities()) {
            if (blockState instanceof TileState) {
                TileState tile = (TileState) blockState;
                String data = tileToString(tile); // Метод для преобразования TileState в строку
                System.out.println(data);
                //databaseManager.saveTileEntity(data);
            }
        }
    }

    private String tileToString(TileState tile) {
        return tile.getType().toString(); // Пример: просто имя типа
    }
}
