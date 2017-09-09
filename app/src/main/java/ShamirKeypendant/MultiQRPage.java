package ShamirKeypendant;
 
import java.util.ArrayList;
import java.util.Arrays;
 

/**
 *
 * This class allows for automatic splitting into multiple {@link QRPage} Objects.
 * @author Christoph
 */
public class MultiQRPage {

    int[] ids = null;

    public MultiQRPage(int[] ids) {
        this.ids = ids;
    }

    /**
     *
     * @return Pages of QRPages
     */
    public Bild[] Generate() {
        ArrayList<Bild> tmp = new ArrayList<>();
        int max_ids_per_page = new QRPage().getLABELS_PER_PAGE();
        int pages = ids.length / max_ids_per_page;
        pages = ((ids.length % max_ids_per_page) != 0) ? pages + 1 : pages; // add one page if the remainder is bigger 0
        
        // Iterate over the ids and generate the single pages
        for (int i = 0; i < pages; i++) {
            int start = i * max_ids_per_page;
            // Check the range
            int end = ((i * max_ids_per_page + max_ids_per_page) < ids.length) ? (i * max_ids_per_page + max_ids_per_page) : ids.length;
            // Copy one page worth of ids
            int[] ids_sub = Arrays.copyOfRange(ids, start, end);
            tmp.add(new QRPage(ids_sub, 300).GeneratePage());
        }

        // Convert to array
        Bild[] array = new Bild[tmp.size()];
        for (int u = 0; u < tmp.size(); u++) {
            array[u] = tmp.get(u);
        }
        return array;
    }

    /**
     * Saves all Images in the path-Folder
     *
     * @param path "C://.../" End with a Slash (/)
     * @param pages Image files
     */
    public static void SaveAsImages(String path, Bild[] pages) {
        int page_id = 0;
        for (Bild page : pages) {
           page.writeToFile(path + "Page_" + page_id + ".png", "png");
               // ImageIO.write(page, "png", new File(path + "Page_" + page_id + ".png"));
           
            page_id++;
        }
    }

}
