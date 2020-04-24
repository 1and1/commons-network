package com.ionos.network.commons;

/** Internal constants and utility methods for the package.
 * @author Stephan Fuhrmann
 * @version 2.0
 **/
final class BitsAndBytes {

    /** No instance allowed. */
    private BitsAndBytes() {
    }

    /** The mask for all bits in a byte. */
    static final int BYTE_MASK = 0xff;

    /** The number of bits per byte. */
    static final int BITS_PER_BYTE = 8;

    /** Bits to mask to get the modulo of 8. */
    static final int BIT_MASK_BYTE = 0x07;

    /** Bits to shift to divide by 8. */
    static final int BIT_SHIFT_BYTE = 3;

    /**
     * Get the highest bit set.
     *
     * @param data the array to find the highest bit set in.
     *             The first byte starts with the most significant bit.
     * @return the highest bit number (starting with 0),
     * or -1 if <code>data</code> has no bit set.
     * @see Integer#highestOneBit(int)
     */
    static int getHighestBitSet(final byte[] data) {
        final int high = data.length << BIT_SHIFT_BYTE;
        int result = -1;
        for (int i = high - 1; i >= 0; i--) {
            if ((data[(high - 1 - i) >> BIT_SHIFT_BYTE] //NOSONAR
                    & 1 << (i & BIT_MASK_BYTE)) != 0) {
                result = i;
                break;
            }
        }
        return result;
    }

    /**
     * Get the lowest bit set.
     *
     * @param data the array to find the lowest bit set in.
     *             The first byte starts with
     *             the most significant bit.
     * @return the lowest set bit number (starting with 0),
     * or -1 if <code>data</code> has no bit set.
     * @see Integer#lowestOneBit(int)
     */
    static int getLowestBitSet(final byte[] data) {
        final int high = data.length << BIT_SHIFT_BYTE;
        int result = -1;
        for (int i = 0; i < high; i++) {
            if ((data[(high - 1 - i) >> BIT_SHIFT_BYTE] //NOSONAR
                    & 1 << (i & BIT_MASK_BYTE)) != 0) {
                result = i;
                break;
            }
        }
        return result;
    }
}
