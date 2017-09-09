package ShamirKeypendant;

/**
 * This interface is used to abstract from a local bitmap implementation of your target system.
 *
 * @author Christoph M. Weinhold
 */

public interface Bild {
    /**
     * Draws a Rect on a {@link Bild}
     * @param x　The left x - coordinate 
     * @param y　The top y - coordinate 
     * @param width
     * @param height
     * @param color　A color code. Use a hexadecimal number representing a color. E.g.: #000000
     */
    void drawRect(int x, int y, int width, int height, int color);

    /**
     *
     * @param x　The left x - coordinate 
     * @param y　The top y - coordinate 
     * @param width
     * @param height
     * @param color　A color code. Use a hexadecimal number representing a color. E.g.: #000000
     */
    void drawBorder(int x, int y, int width, int height, int color);

    /**
     *
     * @param text
     * @param x　The left x - coordinate 
     * @param y　The top y - coordinate 
     * @param w
     * @param h
     * @param size
     * @param color　A color code. Use a hexadecimal number representing a color. E.g.: #000000
     */
    void drawString(String text, int x, int y, int w, int h, int size, int color);

    /**
     *
     * @param x　The left x - coordinate 
     * @param y　The top y - coordinate 
     * @return
     */
    int getPixel(int x, int y);

    /**
     *
     * @param x　The left x - coordinate 
     * @param y　The top y - coordinate 
     * @param color　A color code. Use a hexadecimal number representing a color. E.g.: #000000
     */
    void setPixel(int x, int y, int color);

    /**
     * Helper function to return an adequate font-size.
     *
     * @param text Text to be drawn
     * @param fontname Font name of the desired font
     * @param x　The left x - coordinate 
     * @param y　The top y - coordinate 
     * @param w  The desired width of your text on the {@link Bild}
     * @param h  The desired height of your text on the {@link Bild}
     * @return  The matching font-size of your text, given the supplied parameters.
     */
    int scaleFont(String text, String fontname, int x, int y, int w, int h);

    /**
     *
     * @param image A {@link Bild} to be drawn on your {@link Bild}.
     * @param x　The left x - coordinate 
     * @param y　The top y - coordinate 
     * @param w  The desired width of your {@link Bild} on this {@link Bild}
     * @param h  The desired height of your {@link Bild} on this {@link Bild}
     */
    void drawImage(Bild image, int x, int y, int w, int h);

    /**
     *
     * @return
     */
    int getWidth();

    /**
     *
     * @return
     */
    int getHeight();

    /**
     *
     * @param path A path specifying the destination path of your file.
     * @param type The file extension of your choice.
     */
    void writeToFile(String path, String type);
}
