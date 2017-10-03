package ShamirKeypendant;

/**
 * A class for getting a {@link Bild} representing a given text.
 * @author Christoph
 */


public class ImageFromText {

    /**
     * Returns the desired Bild with the Text filling it's complete width.
     * @param text Text to be returned as a Bild
     * @param width Desired Width of the image
     * @param height Desired Height of the image
     * @return A representation of the text as a Bild
     */
    public static Bild getImage(String text,int width,int height){
        int x = width;
        int y = height;
        Bild b = new AndroidBild(x, y);

        b.drawString(text,0, y/2,x,y, 12, 0x000000);

        return (b);
        
    }
}
