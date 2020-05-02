package com.ionos.network.commons.address;

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
            new HexadecimalAddressParser<>(':',
                    2,
                    array -> new MAC(array),
                    MAC.MAC_LENGTH);

    /**
     * Parses a word hex representation of the MAC in CISCO style.
     * <br>
     * <i>Example:</i>{@code 00ac.acdd.0012}.
     * @see MACFormats#CISCO_CUSTOM_FORMAT
     */
    public static final AddressParser<MAC> CISCO_CUSTOM_FORMAT =
            new HexadecimalAddressParser<>('.',
                    4,
                    array -> new MAC(array),
                    MAC.MAC_LENGTH);
}
