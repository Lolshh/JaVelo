package ch.epfl.javelo;

import static ch.epfl.javelo.Preconditions.checkArgument;

/**
 * This final not instantiable class allows us to extract bit sequence.
 *
 * @author Gaspard Thoral (345230)
 * @author Alexandre Mourot (346365)
 */
public final class Bits {

    /**
     * Private constructor.
     */
    private Bits() {
    }

    /**
     * This method allows us to extract specific bits from an int and to make it signed.
     *
     * @param value  A given integer.
     * @param start  The index at which we start extracting the bits.
     * @param length The length of the bit extraction.
     * @return The signed representation of the extracted bits.
     * @throws IllegalArgumentException (checkArgument) : Throws an exception if specific conditions are not met.
     */
    public static int extractSigned(int value, int start, int length) {
        checkArgument((start + length) <= Integer.SIZE && start < Integer.SIZE && start >= 0 &&
                length >= 0 && length <= Integer.SIZE);
        return (value << (Integer.SIZE - (start + length))) >> Integer.SIZE - length;
    }

    /**
     * This method allows us to extract specific bits from an int and to make it unsigned.
     *
     * @param value  A given integer.
     * @param start  The index at which we start extracting the bits.
     * @param length The length of the bit extraction.
     * @return The unsigned representation of the extracted bits.
     * @throws IllegalArgumentException (checkArgument) : Throws an exception if specific conditions are not met.
     */
    public static int extractUnsigned(int value, int start, int length) {
        checkArgument((start + length) <= Integer.SIZE && start < Integer.SIZE && start >= 0 &&
                length >= 0 && length < Integer.SIZE);
        return (value << Integer.SIZE - (start + length)) >>> Integer.SIZE - length;
    }
}
