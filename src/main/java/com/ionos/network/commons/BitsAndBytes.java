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
}
