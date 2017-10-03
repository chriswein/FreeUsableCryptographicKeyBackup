package ShamirKeypendant;



import java.math.BigInteger;

/**
 * An Implementation of {@link PendantLabel} you can supply with {@link KeyValueParameters} to
 * use.
 */

public class RAWDataPendantLabelImplementation implements PendantLabel {

    public static String OWNER = "owner";
    private final KeyValueParameters contentValues;
    BigInteger bint;
    int DPI = 600;
    int WIDTH = 850; //3,6cm*1,7cm @600dpi
    int HEIGHT = 402;
    int SPACING_X = 50; //5
    int SPACING_Y = 25; //16
    // How many times 300dpi is the desired resulting dpi
    double SCALE = 1.0;

    public RAWDataPendantLabelImplementation(BigInteger bint, int DPI, KeyValueParameters contentValues) {

        this.bint = bint;
        this.DPI = DPI;
        SCALE = (DPI) / 600.0;
        HEIGHT = (int) (HEIGHT * SCALE);
        WIDTH = (int) (WIDTH * SCALE);
        SPACING_Y = (int) (SPACING_Y * SCALE);
        SPACING_X = (int)(SPACING_X * SCALE);
        this.contentValues = contentValues;

    }

    @Override
    public Bild getBild() {
        Bild output_image = new AndroidBild(WIDTH,HEIGHT);

        Bild image = QRGen.getCode(bint.toString());

        String owner = contentValues.get(OWNER);
        // ID
        Bild img_id = ImageFromText.getImage(owner+ "", 400, 400);
        output_image.drawImage(
                img_id
                ,((int)(40*SCALE))
                , ((int)(40*SCALE))
                ,(int)(400*SCALE)
                ,(int)(400*SCALE)
        );

        // QR Code
        output_image.drawImage(
                image
                ,((WIDTH)) - (int)(400*SCALE)
                , ((HEIGHT)) - (int)(400*SCALE)
                , (int)(400*SCALE)
                , (int)(400*SCALE)
        );

        // Border
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
