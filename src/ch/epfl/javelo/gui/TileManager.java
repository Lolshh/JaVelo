package ch.epfl.javelo.gui;


import ch.epfl.javelo.Preconditions;
import javafx.scene.image.Image;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;

import static ch.epfl.javelo.gui.TileManager.TileId.isValid;

/**
 * This method allows us to manage our Tiles representing our map. It can download and stock the Tile into
 * a cache memory and a cache disk.
 * @author Gaspard Thoral (345230)
 * @author Alexandre Mourot (346365)
 */
public final class TileManager {
    private static final int MAX_CAP_MEMORY = 100;
    private final Path path;
    private final String serv;
    private final LinkedHashMap<TileId, Image> cacheMemory =
            new LinkedHashMap<>(MAX_CAP_MEMORY, 0.75f, true);

    /**
     * Constructor of TileManager with the disk cache's path and the server hosting the Tiles online.
     *
     * @param path - Path : path leading to the disk cache.
     * @param serv - String : string representing the server name.
     */
    public TileManager(Path path, String serv) {
        this.path = path;
        this.serv = serv;

    }

    /**
     * This record represent a TileId useful to identify a Tile thanks to zoomLevel, x and y coordinate.
     *
     * @param zoomLevel - int : the zoomLevel.
     * @param xTile     - int : the x coordinate of the Tile.
     * @param yTile     - int : the y coordinate of the Tile.
     */
    public record TileId(int zoomLevel, int xTile, int yTile) {

        public static boolean isValid(int zoom, int x, int y) {
            return !((x > (1 << zoom)) || y > (1 << zoom)) && (x > 0) && (y > 0);
        }
    }

    /**
     * This method allows us to get a Tile (format Image) from a TileId.
     *
     * @param tileId - TileId : tileId from the Tile to get.
     * @return - Image : Image representing the Tile.
     * @throws IOException : Throw an exception if the TileId is not valid.
     */
    public Image imageForTileAt(TileId tileId) throws IOException {

        Preconditions.checkArgument(isValid(tileId.zoomLevel, tileId.xTile, tileId.yTile));

        Path fullPath = path.resolve(String.valueOf(tileId.zoomLevel)).
                resolve(String.valueOf(tileId.xTile)).resolve(tileId.xTile + ".png");

        if (!cacheMemory.containsKey(tileId)) {

            if (!Files.exists(fullPath)) {
                Files.createDirectories(fullPath.getParent());
                URL u = new URL("https://" + serv +"/" + tileId.zoomLevel
                        + "/" + tileId.xTile + "/" + tileId.yTile + ".png");
                URLConnection c = u.openConnection();
                c.setRequestProperty("User-Agent", "JaVelo");

                try (InputStream i = c.getInputStream();
                     OutputStream t = new FileOutputStream(fullPath.toString())) {
                    i.transferTo(t);
                }
            }
                try(InputStream i = new FileInputStream(fullPath.toString())){
                    Image newImage = new Image(i);
                    if (cacheMemory.size() == MAX_CAP_MEMORY) {
                        TileId toRemove = cacheMemory.keySet().iterator().next();
                        cacheMemory.remove(toRemove);
                    }
                    cacheMemory.put(tileId, newImage);
            }
        } return cacheMemory.get(tileId);

    }
}