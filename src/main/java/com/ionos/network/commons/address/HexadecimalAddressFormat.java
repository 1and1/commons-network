package com.ionos.network.commons.address;

import java.io.IOException;

/**
 * Formats an address to a text form in a customizable hexadecimal
 * notation.
 * @author Stephan Fuhrmann
 * */
public final class HexadecimalAddressFormat<T extends Address> extends AbstractAddressFormat<T> {

    /** The character to use as a separator. */
    private final char separator;
    /** The interval in characters for separators to occur. */
    private final int separatorInterval;

    /**
     * Constructor of an instance.
     * @param inSeparator the character to separate the parts with.
     * @param inSeparatorInterval the number of hex digits that form the parts.
     */
    public HexadecimalAddressFormat(char inSeparator, int inSeparatorInterval) {
        this.separator = inSeparator;
        if (inSeparatorInterval <= 0) {
            throw new IllegalArgumentException("separatorInterval must be > 0");
        }
        this.separatorInterval = inSeparatorInterval;
    }

    @Override
    public <A extends Appendable> A format(
            final T address,
            final A toAppendTo)
            throws IOException {
        byte[] addressBytes;
        if (address instanceof AbstractAddress) {
            addressBytes = ((AbstractAddress)address).address;
        } else {
            addressBytes = address.getBytes();
        }

        int charIndex = 0;
        for (int i = 0; i < addressBytes.length; i++) {
            int val = addressBytes[i];

            if (charIndex != 0 && charIndex % separatorInterval == 0) {
                toAppendTo.append(separator);
            }

            toAppendTo.append(BitsAndBytes.toHexDigit(val >>> 4 & 0x0f));
            charIndex++;

            if (charIndex % separatorInterval == 0) {
                toAppendTo.append(separator);
            }
            toAppendTo.append(BitsAndBytes.toHexDigit(val & 0x0f));
            charIndex++;
        }

        return toAppendTo;
    }
}
