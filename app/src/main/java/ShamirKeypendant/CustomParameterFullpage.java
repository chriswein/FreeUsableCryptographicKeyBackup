package ShamirKeypendant;

import java.math.BigInteger;

/**
 * Use this class to put one pendant label on one page.
 * Returns a bunch of {@link Bild}
 * Created by christoph on 17.09.17.
 */

public class CustomParameterFullpage extends SingleIDFullpage {

    private final KeyValueParameters contentValues;


    public CustomParameterFullpage(BigInteger[] items, int dpi, int dataformat, KeyValueParameters contentValues) {
        super(items, dpi, dataformat);
        this.contentValues = contentValues;
        this.WIDTH = WIDTH*3;
        this.HEIGHT = HEIGHT*3;
        this.SPACING_X = SPACING_X*3;
        this.SPACING_Y = SPACING_Y*3;
    }


    /**
     * Returns a pendant label. Overwrite this function with you own implementation, if you so desire.
     * @param item This is the payload
     * @param dpi Desired dpi
     * @param contentValues Additional parameters you can determine
     * @return
     */
    public PendantLabel getPendantLabel(BigInteger item, int dpi, KeyValueParameters contentValues){
        SPACING_Y = 500;
        return (PendantLabel)(new RAWDataPendantLabelImplementation(item, dpi, contentValues));
    }

    /**
     * Only outputs the first id.
     * @return First id.
     */
    @Override
    public Bild GeneratePage() {
        Bild output_image = new AndroidBild(page_width, page_height);  // A4@600dpi

        for (int i = 0; i < 1; i++) {

            if (i == 0) {
                output_image.drawRect(0, 0, page_width, page_height,0xffffff);
            }

            PendantLabel label = getPendantLabel(ids[i],(int)(SCALE*600),contentValues);


            output_image.drawImage(label.getBild(),(i % 5) * (SPACING_X+WIDTH), (i/5 + 1) * (SPACING_X + HEIGHT), WIDTH, HEIGHT);

        }

        return output_image;
    }

    public Bild[] GeneratePages() {

        Bild[] bilder = new Bild[ids.length];


        for (int i = 0; i < ids.length; i++) {

            Bild output_image = new AndroidBild(page_width, page_height);  // A4@600dpi

            if (i == 0) {
                output_image.drawRect(0, 0, page_width, page_height,0xffffff);
            }

            PendantLabel label = getPendantLabel(ids[i],(int)(SCALE*600),contentValues);


            output_image.drawImage(label.getBild(),(SPACING_X), (SPACING_Y), WIDTH, HEIGHT);

            bilder[i] = output_image;
        }


        return bilder;
    }

    private class PrivClassPageGenerated implements Runnable{

        PageGenerated p;

        public PrivClassPageGenerated(PageGenerated p){
            this.p = p;
        }

        @Override
        public void run() {
            Bild[] bilder = GeneratePages();
            p.PageReady(bilder[0]);
            p.PagesReady(bilder);
        }
    }

    @Override
    public void GeneratePageAndCallback(PageGenerated pg) {
        new Thread(new PrivClassPageGenerated(pg)).start();
    }
}
