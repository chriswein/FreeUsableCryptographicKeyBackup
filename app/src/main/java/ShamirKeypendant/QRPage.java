package ShamirKeypendant;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;

import java.math.BigInteger;

/**
 * This class allows to generate a page full of key pendant labels.
 * You can give ids (ID) and a url (URL) to prefix the ids.
 * E.G.: Let URL be example.com/?id= and ID be 4506 the resulting Link will be:
 * example.com/?id=4506
 *
 * The url will be given as a qr-code on the rightern side of the label.
 * * @author Christoph
 */
public class QRPage {

    BigInteger[] ids; //= {20428, 20690, 21397, 21774};
    int page_width = 4961;
    int page_height = 7016;
    int WIDTH = 850; //3,6cm*1,7cm @600dpi
    int HEIGHT = 402;
    int SPACING_X = 50; //5
    int SPACING_Y = 25; //16
    int LABELS_PER_PAGE = 15*5;

    String url = "https://userpage.fu-berlin.de/chriswei/?id=";
    String s_url = "bit.ly/2pADXnR";

    public int getLABELS_PER_PAGE() {
        return LABELS_PER_PAGE;
    }
    String FILE_FORMAT;
    public static int JPG = 0;
    public static int PNG = 1;
    // How many times 600dpi is the desired resulting dpi
    double SCALE = 1.0;

    public QRPage(int[] items, int dpi) {
        this.FILE_FORMAT = "png";
        this.ids = convertToBigInteger(items);
        double scaling_rate = dpi / 600.0;
        page_width = (int) (page_width * scaling_rate);
        page_height = (int) (page_height * scaling_rate);
        WIDTH = (int) (WIDTH * scaling_rate);
        HEIGHT = (int) (HEIGHT * scaling_rate);
        SPACING_X = (int) (SPACING_X * scaling_rate);
        SPACING_Y = (int) (SPACING_Y * scaling_rate);
        SCALE = scaling_rate;
    }

    /**
     *
     * @param items Array containing the ids you want to use.
     * @param dpi Desired dpi of the document. 
     * @param dataformat Use QRPage.JPG or QRPage.PNG
     */
    public QRPage(int[] items, int dpi, int dataformat) {
        this.FILE_FORMAT = dataformat == JPG ? "jpg" : "png";
        this.ids = convertToBigInteger(items);
        double scaling_rate = dpi / 600;
        page_width = (int) (page_width * scaling_rate);
        page_height = (int) (page_height * scaling_rate);
        WIDTH = (int) (WIDTH * scaling_rate);
        HEIGHT = (int) (HEIGHT * scaling_rate);
        SPACING_X = (int) (SPACING_X * scaling_rate);
        SPACING_Y = (int) (SPACING_Y * scaling_rate);
        SCALE = scaling_rate;
    }

    /**
     *
     * @param items Array containing the ids you want to use.
     * @param dpi Desired dpi of the document.
     * @param dataformat Use QRPage.JPG or QRPage.PNG
     */
    public QRPage(BigInteger[] items, int dpi, int dataformat){
        this.FILE_FORMAT = dataformat == JPG ? "jpg" : "png";
        this.ids = items;
        double scaling_rate = dpi / 600.0;
        page_width = (int) (page_width * scaling_rate);
        page_height = (int) (page_height * scaling_rate);
        WIDTH = (int) (WIDTH * scaling_rate);
        HEIGHT = (int) (HEIGHT * scaling_rate);
        SPACING_X = (int) (SPACING_X * scaling_rate);
        SPACING_Y = (int) (SPACING_Y * scaling_rate);
        SCALE = scaling_rate;
    }

    public QRPage() {
        this.FILE_FORMAT = "png";

    }
    
    /**
     * Same as GeneratePage(String path), but gives the image back as a returnstatement.
     * @return The singe page as a BufferedImage.
     */    
    public Bild GeneratePage(){
            Bild output_image = new AndroidBild(page_width, page_height);  // A4@600dpi

            for (int i = 0; i < ids.length; i++) {
                //http://chart.apis.google.com/chart?chs=500x500&cht=qr&chld=L&chl=https%3A%2F%2Fuserpage.fu-berlin.de/chriswei/?id=
             /*   String url_s = url + ids[i];
                String kurzurl = s_url;
                Bild image = QRGen.getCode(url_s);
             

           */     if (i == 0) {
                    output_image.drawRect(0, 0, page_width, page_height,0xffffff);
                }
/*
                // g.setFont(new Font("TimesRoman", Font.PLAIN, 72));     
               
                // ID
                Bild img_id = ImageFromText.getImage(ids[i] + "", 400, 400);
                output_image.drawImage(img_id, ((i % 5) * (SPACING_X + WIDTH)) + (SPACING_X / 2), ((i / 5 + 1) * (SPACING_Y + HEIGHT)) + SPACING_Y + (int)(50*SCALE), (int)(400*SCALE), (int)(400*SCALE));

                // QR Code
                output_image.drawImage( image, ((i % 5) * (SPACING_X + WIDTH)) + (int)(430*SCALE), ((i / 5 + 1) * (SPACING_Y + HEIGHT)), (int)(400*SCALE), (int)(400*SCALE));
           //     g.setColor(Color.black);
                output_image.drawBorder((i % 5) * (SPACING_X + WIDTH), ((i / 5 + 1) * (SPACING_Y + HEIGHT)), WIDTH, HEIGHT,0x000000);

                // URL 
                Bild img_tmp = ImageFromText.getImage(kurzurl, 1000, 250);

                double scale = img_tmp.getWidth() / img_tmp.getHeight();       
                output_image.drawImage(img_tmp, ((i % 5) * (SPACING_X + WIDTH)) + (SPACING_X / 2), ((i / 5 + 1) * (SPACING_Y + HEIGHT)) + SPACING_Y, (int)(400*SCALE), (int) (400*SCALE / scale));
                */

                PendantLabel label = new SimplePendantLabelImplementation(url,s_url,ids[i],(int)(SCALE*600));

                Bild lbild = label.getBild();
                output_image.drawImage(label.getBild(),(i % 5) * (SPACING_X+WIDTH), (i/5 + 1) * (SPACING_X + HEIGHT), WIDTH, HEIGHT);

              //  output_image.drawImage(lbild,100,100,850,400);

            }

            return output_image;
 
    }

    /**
     * Generates a single page of ids and saves it in the given path
     * @param path A path containing the desired file name. e.g.: C://myfile.jpg
     */
    public void GeneratePage(String path) {
        GeneratePage().writeToFile(path, "png");
    }

    public interface PageGenerated {
        /**
         * Gets invoked when the page 0 is ready
         * @param bild
         */
        void PageReady(Bild bild);

        /**
         * Gets invoked if the implementation supports multiple pages. Invoked after {@method PageReady}
         * @param bilder
         */
        void PagesReady(Bild[] bilder);
    }


    private class PrivClassPageGenerated implements Runnable{

        PageGenerated p;

        public PrivClassPageGenerated(PageGenerated p){
            this.p = p;
        }

        @Override
        public void run() {
            Bild b = GeneratePage();
            p.PageReady(b);
        }
    }

    /**
     * Generates the Page, but does not block. Invokes callback if ready.
     * @param pg
     */
    public void GeneratePageAndCallback(PageGenerated pg){
        new Thread(new PrivClassPageGenerated(pg)).start();
    }


    @TargetApi(19)
    public PdfDocument getPDF(){
        PdfDocument document = new PdfDocument();
        // crate a page description
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(this.page_width,this.page_height,1).create();

        // start a page
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas c = page.getCanvas();
        Bitmap b = ((AndroidBild)this.GeneratePage()).getBitmap();
        c.drawBitmap(b,0,0,null);
        c.save();

        // write the document content
        document.finishPage(page);

        // close the document
        return document;
    }

    public interface PdfGenerated {
        void PdfReady(PdfDocument pdf);
    }
    
    private class PrivClassPdfGenerated implements Runnable
    {
        private final PdfGenerated p;

        public PrivClassPdfGenerated(PdfGenerated p)
        {
            this.p = p;
        }


        @Override
        public void run() {
            PdfDocument pdf = getPDF();
            p.PdfReady(pdf);
        }
    }

    /**
     * Generates the Pdf, but does not block. Invokes callback if ready.
     * @param pg
     */
    public void GeneratePdfDocumentAndCallback(PdfGenerated pg)
    {
        new Thread(new PrivClassPdfGenerated(pg)).start();
    }

    /**
     * URL prefix in use
     * @return
     */
    public String getUrl() {
        return url;
    }

    /**
     * URL prefix to use
     * @param url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Short version of the given url.
     * @return
     */
    public String getS_url() {
        return s_url;
    }

    /**
     * Short version of the url to be used. Use services like bit.ly to generate.
     * @param s_url
     */
    public void setS_url(String s_url) {
        this.s_url = s_url;
    }

    private BigInteger[] convertToBigInteger(int[] items){
        BigInteger[] tmp_ids = new BigInteger[items.length];
        int u = 0;
        for (int i :
                items) {
            tmp_ids[u] = new BigInteger(""+i);
            u++;
        }
        return tmp_ids;
    }

}
