/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ShamirKeypendant;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;


/**
 *
 * @author Christoph
 */
public class AndroidBild implements Bild{

    public Bitmap b;

    /**
     * Constructor of the android derivate of Bild interface
     * @param width Width of the image
     * @param height Height of the image
     */
    public AndroidBild(int width, int height) {
        b = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_4444);
    }

    public Bitmap getBitmap(){
        return b;
    }

    /**
     * Draws a rect
     * @param x Left Top x
     * @param y Left Top y
     * @param width Width of desired rect
     * @param height Height of desired rect
     * @param color Color as hex-code. e.g.: Black 0x000000
     */
    @Override
    public void drawRect(int x, int y, int width, int height, int color) {
        Canvas c = new Canvas(b);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);


        paint.setColor(getColorByCode(color));
        c.drawRect(x,y,x+(width),y+(height),paint);

        c.save();
    }

    /**
     * This function adjusts java-color codes to android color codes
     * @param color
     * @return
     */
    public int getColorByCode(int color){
        // Android uses most significant bits for alpha channel
        return 0xff000000+color;
    }

    /**
     * Draws a String in a given rect. The fontsize is automatically scaled.
     * @param text
     * @param x Left Top x
     * @param y Left Top y
     * @param w Width of desired rect
     * @param h Height of desired rect
     * @param size Currently not implemented in android
     * @param color Color of the font
     */
    @Override
    public void drawString(String text, int x, int y,int w,int h, int size, int color) {
        Canvas c = new Canvas(b);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(scaleFont(text,"Arial",0,0,w,h));
        paint.setColor(getColorByCode(color));
        c.drawText(text,x,y,paint);

        c.save();
    }

    @Override
    public int getPixel(int x, int y) {
        return b.getPixel(x,y);
    }

    @Override
    public void setPixel(int x, int y, int color) {
        b.setPixel(x,y,getColorByCode(color));
    }

    /**
     * Returns the appropriate font size for a given font and rect
     * @param text Text to scale
     * @param fontname Font name
     * @param x Left Top x
     * @param y Left Top y
     * @param w Width of desired rect
     * @param h Height of desired rect
     * @return Font size
     */
    @Override
    public int scaleFont(String text, String fontname, int x, int y, int w, int h) {
        float fontSize = 200f;
        Paint paint = new Paint();

        // Get the bounds of the text, using our testTextSize.
        paint.setTextSize(fontSize);
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);

        // Calculate the desired size as a proportion of our testTextSize.
        float desiredTextSize = fontSize * w / bounds.width();

        return (int)desiredTextSize-1;
    }

    /**
     * Draws an image
     * @param image The image to be drawn
     * @param x Left Top x
     * @param y Left Top y
     * @param w Width of desired rect
     * @param h Height of desired rect
     */
    @Override
    public void drawImage(Bild image, int x, int y, int w, int h) {
        Canvas c = new Canvas(b);
        Rect r = new Rect(x,y,x+w,y+h);
        c.drawBitmap(((AndroidBild)image).getBitmap(),null,r,null);

        c.save();
    }

    @Override
    public int getWidth() {
        return b.getWidth();
    }

    @Override
    public int getHeight() {
        return b.getHeight();
    }

    @Override
    public void writeToFile(String path, String type) {

    }

    /**
     * Draws a 1px border in a rectangular shape.
     * @param x Left Top x
     * @param y Left Top y
     * @param width Width of desired rect
     * @param height Height of desired rect
     * @param color Desired color of the border
     */
    @Override
    public void drawBorder(int x, int y, int width, int height, int color) {
        Canvas c = new Canvas(b);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);

        paint.setColor(getColorByCode(color)); // getColorByCode(color)
        c.drawRect(x,y,x+width,y+height,paint);
        c.save();
    }
    
}
