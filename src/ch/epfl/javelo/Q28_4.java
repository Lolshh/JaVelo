package ch.epfl.javelo;

/**
 * This final not instantiable allows us to represent the Q_28 representation
 * and to switch between it and classic representations.
 *
 * @author Gaspard Thoral (345230)
 * @author Alexandre Mourot (346365)
 */
public final class Q28_4 {

    /**
     * The number of bit we shift.
     */
    private static final int BIT_SHIFT = 4;


    /**
     * Private constructor. Not instantiable class.
     */
    private Q28_4() {
    }

    /**
     * This method allows us to obtain an integer which, when read with the Q28.4 representation,
     * gives us the given integer.
     *
     * @param i A given integer.
     * @return The int which binary representation stores the given integer Q28.4 representation.
     */
    public static int ofInt(int i) {
        return i << BIT_SHIFT;
    }

    /**
     * This method allows us to obtain a double  stored inside another one's binary representation.
     *
     * @param q28_4 The integer which binary representation stores a double Q28.4 representation.
     * @return The double stored inside the given integer binary representation.
     */
    public static double asDouble(int q28_4) {
        return Math.scalb((double) q28_4, -BIT_SHIFT);
    }

    /**
     * This method allows us to obtain a float stored inside another one's binary representation.
     *
     * @param q28_4 The integer which binary representation stores a float Q28.4 representation.
     * @return The float stored inside the given integer binary representation.
     */
    public static float asFloat(int q28_4) {
        return Math.scalb(q28_4, -BIT_SHIFT);
    }
}
