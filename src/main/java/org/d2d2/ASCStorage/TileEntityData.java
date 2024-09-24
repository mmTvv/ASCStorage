package org.d2d2.ASCStorage;

public class TileEntityData {
    private final String type;
    private final String data;

    public TileEntityData(String type, String data) {
        this.type = type;
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public String getData() {
        return data;
    }
}
