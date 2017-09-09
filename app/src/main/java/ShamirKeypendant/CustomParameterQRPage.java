package ShamirKeypendant;

import android.content.ContentValues;

import java.math.BigInteger;

/**
 * Extends QRPage to enable the use of {@link ContentValues}. These can be used to
 * give data to your implementation of the {@link PendantLabel}
 */

public class CustomParameterQRPage extends QRPage {
    private final KeyValueParameters contentValues;

    /**
     * Extends the constructor of {@link QRPage} by attaching an option for {@link ContentValues}
     * @param items
     * @param dpi
     * @param dataformat
     * @param contentValues Additional Parameters
     */
    public CustomParameterQRPage(BigInteger[] items, int dpi, int dataformat, KeyValueParameters contentValues){
        super(items,dpi,dataformat);
        this.contentValues = contentValues;
    }

    @Override
    public Bild GeneratePage() {
        Bild output_image = new AndroidBild(page_width, page_height);  // A4@600dpi

        for (int i = 0; i < ids.length; i++) {

            if (i == 0) {
                output_image.drawRect(0, 0, page_width, page_height,0xffffff);
            }

            PendantLabel label = new RAWDataPendantLabelImplementation(ids[i],(int)(SCALE*600),contentValues);


            output_image.drawImage(label.getBild(),(i % 5) * (SPACING_X+WIDTH), (i/5 + 1) * (SPACING_X + HEIGHT), WIDTH, HEIGHT);

        }

        return output_image;
    }
}
