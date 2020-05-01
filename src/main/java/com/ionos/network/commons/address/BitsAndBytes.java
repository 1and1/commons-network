package com.ionos.network.commons.address;

import java.io.IOException;
import java.util.Arrays;

/** Internal constants and utility methods for the package.
 * @author Stephan Fuhrmann
 *
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

    /** Creates a new result with the given length and fill byte.
     * @param length the length of the desired array.
     * @param fill the value to fill every element with.
     * */
    static byte[] newArrayOf(int length, byte fill) {
        byte[] result = new byte[length];
        Arrays.fill(result, fill);
        return result;
    }

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

    /** Appends two bytes to an Appendable as hex.
     * There are leading zeros appended.
     * @param to the Appendable to append to.
     * @param upper the most significant byte.
     * @param lower the least significant byte.
     * @throws IOException when appending to the Appendable has a problem.
     * */
    static void appendHexWithLeadingZeros(final Appendable to,
                                          final byte upper,
                                          final byte lower)
            throws IOException {
        to.append(BitsAndBytes.toHexDigit(upper >>> 4 & 0x0f));
        to.append(BitsAndBytes.toHexDigit(upper & 0x0f));
        to.append(BitsAndBytes.toHexDigit(lower >>> 4 & 0x0f));
        to.append(BitsAndBytes.toHexDigit(lower & 0x0f));
    }

    /** Appends two bytes to an Appendable as hex.
     * There are no leading zeros appended.
     * @param to the Appendable to append to.
     * @param upper the most significant byte.
     * @param lower the least significant byte.
     * @throws IOException when appending to the Appendable has a problem.
     * */
    static void appendHex(final Appendable to,
                          final byte upper,
                          final byte lower)
            throws IOException {
        boolean leading = false;
        if (upper >>> 4 != 0) {
            to.append(BitsAndBytes.toHexDigit(upper >>> 4 & 0x0f));
            leading = true;
        }
        if (leading || upper != 0) {
            to.append(BitsAndBytes.toHexDigit(upper & 0x0f));
            leading = true;
        }
        if (leading || lower >>> 4 != 0) {
            to.append(BitsAndBytes.toHexDigit(lower >>> 4 & 0x0f));
        }
        to.append(BitsAndBytes.toHexDigit(lower & 0x0f));
    }

    /** Convert the passed in value to a hex digit.
     * @param value a value between 0 and 15 (inclusive).
     * @return a character between '0' and '9' or 'a and 'f'.
     * */
    static char toHexDigit(int value) {
        if (value >= 0 && value <= 9) {
            return (char)('0' + value);
        } else if (value >= 10 && value <= 15) {
            return (char)('a' - 10 + value);
        } else {
            throw new IllegalArgumentException("Value "
                    + value + " can not be mapped to hex");
        }
    }

    /** Checks for equality of two byte arrays using a mask per byte array.
     * All arrays need to have the same size.
     * @param left the array the {@code right} array with.
     * @param right the array to compare the {@code left} array with.
     * @param mask  the mask to use for the comparison on {@code left} and
     *              {@code right}.
     * @return {@code true} if {@code left} and {@code right}
     * are equal after applying the {@code mask} and all
     * arrays have the same size.
     * */
    static boolean equalsWithMask(byte[] left, byte[] right, byte[] mask) {
        if (left.length != right.length
            || left.length != mask.length) {
            return false;
        }

        for (int i=0; i < left.length; i++) {
            if ((left[i] & mask[i]) != (right[i] & mask[i])) {
                return false;
            }
        }
        return true;
    }
}
