package com.ionos.network.commons.address;

import java.util.StringTokenizer;

import static  com.ionos.network.commons.address.BitsAndBytes.UBYTE_MAX_VALUE;
import static  com.ionos.network.commons.address.BitsAndBytes.USHORT_MAX_VALUE;
import static  com.ionos.network.commons.address.BitsAndBytes.BITS_PER_BYTE;
import static  com.ionos.network.commons.address.BitsAndBytes.HEX_RADIX;

/**
 * Parser implementations for MAC addresses.
 * @see MAC
 * @author Stephan Fuhrmann
 * */
public final class MACParsers {

    /**
     * Private constructor. No instance allowed.
     */
    private MACParsers() {
        // no instance allowed
    }

    private abstract static class AbstractMacParser
            implements AddressParser<MAC> {
        public MAC parse(final String address) {
            return new MAC(parseAsBytes(address));
        }
    }

    /**
     * Parses a MAC address in every possible known format.
     * @see #CISCO_CUSTOM_FORMAT
     * @see #COLON_SEPARATED_HEX_FORMAT
     */
    public static final AddressParser<MAC> DEFAULT = new AbstractMacParser() {
        @Override
        public byte[] parseAsBytes(final String address) {
            byte[] result;
            try {
                result = COLON_SEPARATED_HEX_FORMAT.parseAsBytes(address);
            } catch (IllegalArgumentException e) {
                result = CISCO_CUSTOM_FORMAT.parseAsBytes(address);
            }
            return result;
        }
    };

    /**
     * Parses a single byte hex representation of the MAC.
     * <br>
     * <i>Example:</i> {@code 00:ac:ac:dd:00:12}.
     * @see MACFormats#COLON_SEPARATED_HEX_FORMAT
     */
    public static final AddressParser<MAC> COLON_SEPARATED_HEX_FORMAT =
            new AbstractMacParser() {
        @Override
        public byte[] parseAsBytes(final String address) {
            StringTokenizer stringTokenizer =
                    new StringTokenizer(address, ":");
            if (stringTokenizer.countTokens() != MAC.MAC_LENGTH) {
                throw new IllegalArgumentException("Illegal address "
                        + address);
            }
            byte[] result = new byte[MAC.MAC_LENGTH];
            int index = 0;
            while (stringTokenizer.hasMoreTokens()) {
                String component = stringTokenizer.nextToken();
                int value = Integer.parseInt(component, HEX_RADIX);
                if (value < 0
                        || value > UBYTE_MAX_VALUE
                        || component.length() != 2) {
                    throw new IllegalArgumentException("Illegal component "
                            + component + " in address " + address);
                }

                result[index++] = (byte) value;
            }
            return result;
        }
    };

    /** The number of blocks in the cisco format. */
    private static final int CISCO_FORMAT_PARTS = 3;

    /** The character length of a block in cisco format. */
    private static final int CISCO_BLOCK_LENGTH = 4;

    /**
     * Parses a word hex representation of the MAC in CISCO style.
     * <br>
     * <i>Example:</i>{@code 00ac.acdd.0012}.
     * @see MACFormats#CISCO_CUSTOM_FORMAT
     */
    public static final AddressParser<MAC> CISCO_CUSTOM_FORMAT =
            new AbstractMacParser() {
        @Override
        public byte[] parseAsBytes(final String address) {
            StringTokenizer stringTokenizer = new StringTokenizer(address, ".");
            if (stringTokenizer.countTokens() != CISCO_FORMAT_PARTS) {
                throw new IllegalArgumentException("Illegal address "
                        + address);
            }
            byte[] result = new byte[MAC.MAC_LENGTH];
            int index = 0;
            while (stringTokenizer.hasMoreTokens()) {
                String component = stringTokenizer.nextToken();
                int value = Integer.parseInt(component, HEX_RADIX);
                if (value < 0
                        || value > USHORT_MAX_VALUE
                        || component.length() != CISCO_BLOCK_LENGTH) {
                    throw new IllegalArgumentException(
                            "Illegal component " + component
                                    + " in address " + address);
                }

                result[index++] = (byte) (value >> BITS_PER_BYTE);
                result[index++] = (byte) value;
            }
            return result;
        }
    };
}
