package com.ionos.network.commons.address;

import java.util.StringTokenizer;

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

    private static abstract class AbstractMacParser implements AddressParser<MAC> {
        public MAC parse(String address) {
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
        public byte[] parseAsBytes(String address) {
            byte[] result;
            try {
                result = COLON_SEPARATED_HEX_FORMAT.parseAsBytes(address);
            }
            catch (IllegalArgumentException e) {
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
    public static final AddressParser<MAC> COLON_SEPARATED_HEX_FORMAT = new AbstractMacParser() {
        @Override
        public byte[] parseAsBytes(String address) {
            StringTokenizer stringTokenizer = new StringTokenizer(address, ":");
            if (stringTokenizer.countTokens() != MAC.MAC_LENGTH) {
                throw new IllegalArgumentException("Illegal address " + address);
            }
            byte[] result = new byte[MAC.MAC_LENGTH];
            int index = 0;
            while (stringTokenizer.hasMoreTokens()) {
                String component = stringTokenizer.nextToken();
                int value = Integer.parseInt(component, 16);
                if (value < 0 || value > 255 || component.length() != 2) {
                    throw new IllegalArgumentException("Illegal component " + component + " in address " + address);
                }

                result[index++] = (byte)value;
            }
            return result;
        }
    };

    /**
     * Parses a word hex representation of the MAC in CISCO style.
     * <br>
     * <i>Example:</i>{@code 00ac.acdd.0012}.
     * @see MACFormats#CISCO_CUSTOM_FORMAT
     */
    public static final AddressParser<MAC> CISCO_CUSTOM_FORMAT = new AbstractMacParser() {
        @Override
        public byte[] parseAsBytes(String address) {
            StringTokenizer stringTokenizer = new StringTokenizer(address, ".");
            if (stringTokenizer.countTokens() != 3) {
                throw new IllegalArgumentException("Illegal address " + address);
            }
            byte[] result = new byte[MAC.MAC_LENGTH];
            int index = 0;
            while (stringTokenizer.hasMoreTokens()) {
                String component = stringTokenizer.nextToken();
                int value = Integer.parseInt(component, 16);
                if (value < 0 || value > 65535 || component.length() != 4) {
                    throw new IllegalArgumentException("Illegal component " + component + " in address " + address);
                }

                result[index++] = (byte)(value>>8);
                result[index++] = (byte)value;
            }
            return result;
        }
    };
}
