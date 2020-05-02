package com.ionos.network.commons.address;

import java.util.Objects;
import java.util.function.Function;

/**
 * Parses an address in fixed-length hexadecimal text form.
 * The address String is separated with separator chars
 * every separator interval.
 * @param <T> the type of address to parse.
 * @see HexadecimalAddressFormat
 * @author Stephan Fuhrmann
 * */
public final class HexadecimalAddressParser<T extends Address>
        implements AddressParser<T> {

    /** The character to use as a separator. */
    private final char separator;

    /** The interval in characters for separators to occur. */
    private final int separatorInterval;

    /** The factory for creating instances of {@code T}. */
    private final Function<byte[], T> factory;

    /** The factory for creating instances of {@code T}. */
    private final int addressLength;

    /**
     * Constructor of an instance.
     * @param inSeparator the character to separate the parts with.
     * @param inSeparatorInterval the number of hex digits that form the parts.
     * @param inFactory a factory to create a new instance of {@code T}
     *                  from a parsed byte array.
     * @param inAddressLength the length of an address array in bytes.
     * @throws NullPointerException if {@code inFactory} is {@code null}.
     * @throws IllegalArgumentException if {@code inSeparatorInterval}
     * is smaller or equal 0.
     */
    public HexadecimalAddressParser(final char inSeparator,
                                    final int inSeparatorInterval,
                                    final Function<byte[], T> inFactory,
                                    final int inAddressLength) {
        this.separator = inSeparator;
        if (inSeparatorInterval <= 0) {
            throw new IllegalArgumentException(
                    "separatorInterval must be > 0");
        }
        this.separatorInterval = inSeparatorInterval;
        this.factory = Objects.requireNonNull(inFactory, "factory is null");
        this.addressLength = inAddressLength;
    }

    @Override
    public T parse(final String address) {
        return factory.apply(parseAsBytes(address));
    }

    @Override
    public byte[] parseAsBytes(final String address) {
        byte[] result = new byte[addressLength];
        int expectLength = addressLength * 2
                + (2 * addressLength - 1) / separatorInterval;
        if (address.length() != expectLength) {
            throw new IllegalArgumentException("Illegal address "
                    + address +  " length, expected " + expectLength);
        }

        // check separators
        for (int charIndex = separatorInterval;
             charIndex < expectLength;
             charIndex += 1 + separatorInterval) {
            char curSeparator = address.charAt(charIndex);
            if (separator != curSeparator) {
                throw new IllegalArgumentException("Illegal separator '"
                        + curSeparator + "' in address " + address);
            }
        }

        // parse the bytes
        for (int byteIndex = 0; byteIndex < addressLength; byteIndex++) {
            char firstChar = address.charAt(2 * byteIndex
                    + (2 * byteIndex) / separatorInterval);
            char secondChar = address.charAt((2 * byteIndex + 1)
                    + (2 * byteIndex + 1) / separatorInterval);
            int byteValue = BitsAndBytes.toInt(firstChar)
                    << BitsAndBytes.BITS_PER_NIBBLE
                    | BitsAndBytes.toInt(secondChar);
            result[byteIndex] = (byte) byteValue;
        }

        return result;
    }
}
