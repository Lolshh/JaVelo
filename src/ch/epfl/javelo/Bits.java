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
     * @param value  - int : A given integer.
     * @param start  - int : The index at which we start extracting the bits.
     * @param length - int : The length of the bit extraction.
     * @return - int : The signed representation of the extracted bits.
     * @throw IllegalArgumentException (checkArgument) : Throws an exception if specific conditions are not met.
     */
    public static int extractSigned(int value, int start, int length) {
        checkArgument((start + length) <= 32 && start <= 31 && start >= 0 &&
                length >= 0 && length <= 32);
        return (value << (Integer.SIZE - (start + length)) ) >> Integer.SIZE - length;
    }

    /**
     * This method allows us to extract specific bits from an int and to make it unsigned.
     *
     * @param value  - int : A given integer.
     * @param start  - int : The index at which we start extracting the bits.
     * @param length - int : The length of the bit extraction.
     * @return - int : The unsigned representation of the extracted bits.
     * @throw IllegalArgumentException (checkArgument) : Throws an exception if specific conditions are not met.
     */
    public static int extractUnsigned(int value, int start, int length) {
        checkArgument((start + length) <= 32 && start <= 31 && start >= 0 &&
                length >= 0 && length < 32);
        return (value << Integer.SIZE - (start + length)) >>> Integer.SIZE - length;
    }
}
