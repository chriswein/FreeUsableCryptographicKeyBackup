package ShamirKeypendant;

import java.math.BigInteger;

/**
 * An simple Implementation of {@link PendantLabel}
 */

public class SimplePendantLabelImplementation implements PendantLabel{

    String url,url_s;
    BigInteger bint;
    int DPI = 600;
    int WIDTH = 850; //3,6cm*1,7cm @600dpi
    int HEIGHT = 402;
    int SPACING_X = 50; //5
    int SPACING_Y = 25; //16
    // How many times 300dpi is the desired resulting dpi
    double SCALE = 1.0;

    public SimplePendantLabelImplementation(String url, String url_s, BigInteger bint, int DPI) {
        this.url = url;
        this.url_s = url_s;
        this.bint = bint;
        this.DPI = DPI;
        SCALE = (DPI) / 600.0;
        HEIGHT = (int) (HEIGHT * SCALE);
        WIDTH = (int) (WIDTH * SCALE);
        SPACING_Y = (int) (SPACING_Y * SCALE);
        SPACING_X = (int)(SPACING_X * SCALE);

    }

    @Override
    public Bild getBild() {
        Bild output_image = new AndroidBild(WIDTH,HEIGHT);
        String url_comp = url + bint.toString();
        String kurzurl = url_s;
        Bild image = QRGen.getCode(url_comp);

        // ID
        Bild img_id = ImageFromText.getImage(bint.toString() + "", 400, 400);
        output_image.drawImage(
                                img_id
                                ,0
                                , ((int)(40*SCALE))
                                ,(int)(400*SCALE)
                                ,(int)(400*SCALE)
        );

        // QR Code
        output_image.drawImage(
                image
                ,((WIDTH)) - (int)(401*SCALE)
                , ((HEIGHT)) - (int)(401*SCALE)
                , (int)(400*SCALE)
                , (int)(400*SCALE)
        );



        // URL
        Bild img_tmp = ImageFromText.getImage(kurzurl, 1000, 250);

        double scale = img_tmp.getWidth() / img_tmp.getHeight();

        output_image.drawImage(
                img_tmp
                , SPACING_X//((SPACING_X )) + (SPACING_X / 2)
                , (int) (20*SCALE)//((SPACING_Y )) + SPACING_Y
                , (int) (400*SCALE)
                , (int) (400*SCALE / scale)
        );

        output_image.drawBorder(
                (1)
                ,(1)
                , WIDTH-2
                , HEIGHT-2
                , 0x000000
        );

        return output_image;
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }
}
