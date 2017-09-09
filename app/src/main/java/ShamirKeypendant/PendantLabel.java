package ShamirKeypendant;

/**
 * This interface can be implemented to use custom layouts for your pendant.
 * Created by christoph on 13.08.17.
 */

public interface PendantLabel {
    /**
     *
     * @return The {@link Bild}
     */
    Bild getBild();

    /**
     * Returns the width of your {@link Bild}
     * @return
     */
    int getWidth();

    /**
     * Returns the height of your {@link Bild}
     * @return
     */
    int getHeight();
}
