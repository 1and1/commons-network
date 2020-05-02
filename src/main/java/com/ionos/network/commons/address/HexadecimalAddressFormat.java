package com.ionos.network.commons.address;

import java.io.IOException;

/**
 * Formats an address to a text form in a customizable hexadecimal
 * notation in fixed length.
 * @param <T> the type of address to format.
 * @see HexadecimalAddressParser
 * @author Stephan Fuhrmann
 * */
public final class HexadecimalAddressFormat<T extends Address>
        extends AbstractAddressFormat<T> {

    /** The character to use as a separator. */
    private final char separator;

    /** The interval in characters for separators to occur. */
    private final int separatorInterval;

    /**
     * Constructor of an instance.
     * @param inSeparator the character to separate the parts with.
     * @param inSeparatorInterval the number of hex digits that form the parts.
     * @throws IllegalArgumentException if the
     * {@code separatorInterval is equal or less than 0}.
     */
    public HexadecimalAddressFormat(final char inSeparator,
                                    final int inSeparatorInterval) {
        this.separator = inSeparator;
        if (inSeparatorInterval <= 0) {
            throw new IllegalArgumentException(
                    "separatorInterval must be > 0");
        }
        this.separatorInterval = inSeparatorInterval;
    }

    @Override
    public <A extends Appendable> A format(
            final T address,
            final A toAppendTo)
            throws IOException {
        byte[] addressBytes = AbstractAddress.getBytesForReading(address);

        int charIndex = 0;
        for (int i = 0; i < addressBytes.length; i++) {
            int val = addressBytes[i];

            if (charIndex != 0 && charIndex % separatorInterval == 0) {
                toAppendTo.append(separator);
            }

            toAppendTo.append(BitsAndBytes.toHexDigit(
                    val >>> BitsAndBytes.BITS_PER_NIBBLE
                            & BitsAndBytes.BIT_MASK_NIBBLE));
            charIndex++;

            if (charIndex % separatorInterval == 0) {
                toAppendTo.append(separator);
            }
            toAppendTo.append(BitsAndBytes.toHexDigit(
                    val & BitsAndBytes.BIT_MASK_NIBBLE));
            charIndex++;
        }

        return toAppendTo;
    }
}
