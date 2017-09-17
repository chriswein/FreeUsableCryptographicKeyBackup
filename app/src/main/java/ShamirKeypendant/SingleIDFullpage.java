/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ShamirKeypendant;


import java.math.BigInteger;

/**
 * This class generates a DIN A4 Page containing a QR-Code, ID and URL
 * @author Christoph
 */
public class SingleIDFullpage extends QRPage{

    public SingleIDFullpage(int[] items, int dpi) {
        super(items, dpi);
    }

    public SingleIDFullpage(int[] items, int dpi, int dataformat) {
        super(items, dpi, dataformat);
    }

    public SingleIDFullpage(BigInteger[] items, int dpi, int dataformat){
        super(items,dpi,dataformat);
    }

    @Override
    public void GeneratePage(String path) {
            Bild output_image = GeneratePage();
            output_image.writeToFile(path, "png");
    
    }

    @Override
    public Bild GeneratePage() {


        Bild output_image = new AndroidBild(page_width, page_height);  // A4@600dpi
        int i = 0;
        //http://chart.apis.google.com/chart?chs=500x500&cht=qr&chld=L&chl=https%3A%2F%2Fuserpage.fu-berlin.de/chriswei/?id=
        String url_s = "https://userpage.fu-berlin.de/chriswei/?id=" + ids[i];
        String kurzurl = "http://bit.ly/2pADXnR";
        Bild image = QRGen.getCode(url_s);

        if (i == 0) {
            //     output_image.setColor(Color.white);
            output_image.drawRect(0, 0, page_width, page_height,0xffffff);
        }

        // g.setFont(new Font("TimesRoman", Font.PLAIN, 72));
        float globalscale = 3.0f;
        int padding = (int)(20*SCALE*globalscale);
        int dimensions = (int)(400*SCALE*globalscale);
        int padding2 = (int)(430*SCALE*globalscale);
        // ID
        Bild img_id = ImageFromText.getImage(ids[i] + "", 400, 400);
        output_image.drawImage(img_id, padding, padding, dimensions, dimensions);

        // QR Code
        output_image.drawImage(image, padding2, padding , dimensions, dimensions);
        //g.setColor(Color.black);
        //g.drawRect((i % 5) * (SPACING_X + WIDTH), ((i / 5 + 1) * (SPACING_Y + HEIGHT)), WIDTH, HEIGHT);

        // URL
        Bild img_tmp = ImageFromText.getImage(kurzurl, 1000, 250);

        double scale = img_tmp.getWidth() / img_tmp.getHeight();
        output_image.drawImage(img_tmp, padding,padding2 , dimensions, (int) (dimensions / scale));





        return output_image;


    }
    
}
