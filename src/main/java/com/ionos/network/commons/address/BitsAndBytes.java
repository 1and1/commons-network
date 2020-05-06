package com.ionos.network.commons.address;

import java.io.IOException;
import java.util.Arrays;

/** Internal constants and utility methods for the package.
 * @author Stephan Fuhrmann
 *
 **/
final class BitsAndBytes {

    /** Max value for an unsigned 16 bit value.
     * */
    static final int USHORT_MAX_VALUE = 0xffff;

    /** Max value for an unsigned 8 bit value.
     * */
    static final int UBYTE_MAX_VALUE = 0xff;

    /** The radix for hexadecimal numbers. */
    static final int HEX_RADIX = 16;

    /** The mask for all bits in a byte. */
    static final int BYTE_MASK = 0xff;

    /** The number of bits per byte. */
    static final int BITS_PER_BYTE = 8;

    /** Bits to mask to get the modulo of 8. */
    static final int BIT_MASK_TRIPLE = 0x07;

    /** Bits to shift to divide by 8. */
    static final int BIT_SHIFT_BYTE = 3;

    /** The number of bits of a nibble. */
    static final int BITS_PER_NIBBLE = 4;

    /** The bits to mask a nibble. */
    static final int BIT_MASK_NIBBLE = 0xf;

    /** Maximum decimal value for one digit. */
    static final int DECIMAL_MAX_DIGIT = 9;

    /** Hexadecimal value of 'A'. */
    static final int HEXADECIMAL_A_VALUE = 10;

    /** Maximum value for a hexadecimal digit. */
    static final int HEXADECIMAL_MAX_DIGIT = 15;

    /** No instance allowed. */
    private BitsAndBytes() {
    }

    /** Creates a new result with the given length and fill byte.
     * @param length the length of the desired array.
     * @param fill the value to fill every element with.
     * @return a new array of the length {@code length},
     * filled with the value {@code fill}.
     * */
    static byte[] newArrayOf(final int length, final byte fill) {
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
                    & 1 << (i & BIT_MASK_TRIPLE)) != 0) {
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
                    & 1 << (i & BIT_MASK_TRIPLE)) != 0) {
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
        to.append(BitsAndBytes.toHexDigit(upper >>> BITS_PER_NIBBLE
                & BIT_MASK_NIBBLE));
        to.append(BitsAndBytes.toHexDigit(upper & BIT_MASK_NIBBLE));
        to.append(BitsAndBytes.toHexDigit(lower >>> BITS_PER_NIBBLE
                & BIT_MASK_NIBBLE));
        to.append(BitsAndBytes.toHexDigit(lower & BIT_MASK_NIBBLE));
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
        if (upper >>> BITS_PER_NIBBLE != 0) {
            to.append(BitsAndBytes.toHexDigit(upper >>> BITS_PER_NIBBLE
                    & BIT_MASK_NIBBLE));
            leading = true;
        }
        if (leading || upper != 0) {
            to.append(BitsAndBytes.toHexDigit(upper
                    & BIT_MASK_NIBBLE));
            leading = true;
        }
        if (leading || lower >>> BITS_PER_NIBBLE != 0) {
            to.append(BitsAndBytes.toHexDigit(lower >>> BITS_PER_NIBBLE
                    & BIT_MASK_NIBBLE));
        }
        to.append(BitsAndBytes.toHexDigit(lower & BIT_MASK_NIBBLE));
    }

    /** Convert the passed in value to a hex digit.
     * @param value a value between 0 and 15 (inclusive).
     * @return a character between '0' and '9' or 'a and 'f'.
     * @see #toInt(char)
     * */
    static char toHexDigit(final int value) {
        if (value >= 0 && value <= DECIMAL_MAX_DIGIT) {
            return (char) ('0' + value);
        } else if (value >= HEXADECIMAL_A_VALUE
                && value <= HEXADECIMAL_MAX_DIGIT) {
            return (char) ('a' - HEXADECIMAL_A_VALUE + value);
        } else {
            throw new IllegalArgumentException("Integer '"
                    + value + "' is not in the range [0-15] and"
                    + " can not be mapped to hex");
        }
    }

    /** Convert the passed in value to a hex digit.
     * @param hexDigit the hex digit to convert to an int.
     * @return a value between 0 and 15.
     * @see #toHexDigit(int)
     * */
    static int toInt(final char hexDigit) {
        if (hexDigit >= '0' && hexDigit <= '9') {
            return hexDigit - '0';
        } else if (hexDigit >= 'a' && hexDigit <= 'f') {
            return hexDigit - 'a' + HEXADECIMAL_A_VALUE;
        } else if (hexDigit >= 'A' && hexDigit <= 'F') {
            return hexDigit - 'A' + HEXADECIMAL_A_VALUE;
        } else {
            throw new IllegalArgumentException("Char '"
                    + hexDigit + "' is not in the set [0-9a-fA-F] and"
                    + " can not be interpreted as hex");
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
    static boolean equalsWithMask(final byte[] left,
                                  final byte[] right,
                                  final byte[] mask) {
        if (left.length != right.length
            || left.length != mask.length) {
            return false;
        }

        for (int i = 0; i < left.length; i++) {
            if ((left[i] & mask[i]) != (right[i] & mask[i])) {
                return false;
            }
        }
        return true;
    }

    /** Sets a number of leading bits to 1.
     * @param data the array where to set the first {@code bits} to one.
     * @param bits the number of bits to set to one.
     */
    static void setLeadingBits(final byte[] data, final int bits) {
        int i = 0;
        int remainingBits;
        for (remainingBits = bits;
             remainingBits >= BITS_PER_BYTE;
             remainingBits -= BITS_PER_BYTE) {
            data[i++] = (byte) BYTE_MASK;
        }

        if (remainingBits != 0) {
            data[i] = (byte)
                    (BYTE_MASK
                            << (BITS_PER_BYTE - remainingBits));
        }
    }
}
