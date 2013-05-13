package com.nokia.maps.example.overlay;


import com.nokia.maps.map.MapUrlProvider;
import javax.microedition.lcdui.Image;


/**
 * This is an example of a working Map Provider, for use as an overlay.
 * As a source we used an original public domain image and split it into individual map tiles.
 *
 * see : http://commons.wikimedia.org/wiki/File:Map_de_berlin_1789_%28georeferenced%29.jpg
 *
 */
public class OldBerlinProvider extends MapUrlProvider {

    private static final String TILE_SERVER = "http://api.maps.nokia.com/maptiles/old_berlin/";
    private static final long MAP_VALIDITY = (24L * 3600L * 1000L); // Valid 1000 days.

    /**
     *  Construct the overlay, the API prefers 128 X 128 pixel tiles on phones
     *  with a smaller memory, 256 x 256 as standard.
     *  The tiles are considered valid for 1000 days.
     *  @param resolution - the tile resolution.
     */
    public OldBerlinProvider(int resolution) {
        super(resolution, MAP_VALIDITY);
    }

    /**
     * When a request is made for a tile, we need to construct the URL.
     * URLs are of the form tile service 128/5/15/9.png
     * where The last four digits are the resolution , the zoom, the row and the column
     * respectively.
     *
     * @param zoom
     * @param column
     * @param row
     * @return the complete URL for the Old Berlin tile service.
     */
    public String getTileUrl(int zoom, int column, int row) {

        StringBuffer buffer = new StringBuffer(TILE_SERVER);

        buffer.append(getResolution());
        buffer.append("/");
        buffer.append(zoom);
        buffer.append("/");
        buffer.append(row);
        buffer.append("/");
        buffer.append(column);
        buffer.append(".png");

        return buffer.toString();
    }

    /**
     * Having received data for an image we need to decode and display it.
     * It also be possible to do post-processing here. Tile sizes should be
     * minimized this server side, to reduce network traffic.
     *
     * @param data
     * @return the tile as an image.
     */
    public final Image decode(byte[] data) {
        return Image.createImage(data, 0, data.length);
    }

    /**
     * <p>If you attempt to download a tile and the tile server cannot respond, an application error
     * is thrown. It is therefore necessary to check for the validity of the request prior to
     * requesting a tile.  If this calculation is simple, then the MapProvider class can provide
     * this information directly. If it is more complex, it is necessary to make a server call to
     * find out whether the tile can be found.</p>
     *
     * @param zoom
     * @param column
     * @param row
     * @return <code>true</code> if the tile is supported, <code>false</code> otherwise.
     */
    public boolean isTileSupported(int zoom, int column, int row) {
        boolean valid = (zoom > 11 && zoom < 16);

        if (((zoom == 12) && (row != 1343 || column != 2200))
                || ((zoom == 13)
                        && (row < 2686 || column < 4400 || row > 2687
                        || column > 4401))
                        || ((zoom == 14)
                                && (row < 5372 || column < 8800 || row > 5375
                                || column > 8803))
                                || ((zoom == 15)
                                        && (row < 10744 || column < 17601
                                        || row > 10750 || column > 17607))) {
            valid = false;
        }

        return valid;
    }

    /**
     * A unique name for the overlay.
     * @return  overlay of  a historical map of Berlin from 1789
     */
    public String getName() {
        return "old.berlin";
    }

}
