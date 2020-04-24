package com.ionos.network.commons;

import static com.ionos.network.commons.BitsAndBytes.BITS_PER_BYTE;

/**
 * Parses an IP address from text notation.
 * @see IP
 * @author Stephan Fuhrmann
 *
 **/
public final class MACParser implements AddressParser<MAC> {

    /** The singleton instance for parsing MAC addresses. */
    public static final AddressParser<MAC> INSTANCE = new MACParser();

    /** Private constructor. No instance allowed. */
    private MACParser() {
    }

    /**
     * Parses the specified MAC address and returns its
     * representation as a byte array.
     *
     * @param str a MAC address of single-byte-hex-form
     *            ({@code 000:00:00:00:00:00})
     *            or CISCO triple short form ({@code 00000.0000.0000}).
     * @return the MAC object representing the text address..
     */
    @Override
    public MAC parse(final String str) {
        return new MAC(parseAsBytes(str));
    }

    /**
     * Parses the specified MAC address and returns its representation
     * as a byte array.
     *
     * @param macAddress a MAC address of single-byte-hex-form
     *                   ({@code 00:00:00:00:00:00}) or CISCO
     *                   triple short form ({@code 0000.0000.0000}).
     * @return the byte array representing the MAC address.
     */
    @Override
    public byte[] parseAsBytes(final String macAddress) {
        if (macAddress == null) {
            throw new NullPointerException("str is null");
        }

        if (macAddress.length() == 0) {
            throw new IllegalArgumentException(
                    "given string has a zero length");
        }

        // single byte form: 00:00:00:00:00:00
        if (macAddress.matches("[0-9a-fA-F]{2}(:[0-9a-fA-F]{2}){5}")) {
            final byte[] mac = new byte[MAC.MAC_LENGTH];
            final String[] octets = macAddress.split(":");

            for (int i = 0; i < octets.length; i++) {
                final int value = Integer.parseInt(octets[i], 16);
                mac[i] = (byte) value;
            }
            return mac;
        }

        // two byte cisco form: 0000.0000.0000
        if (macAddress.matches("[0-9a-fA-F]{1,4}(\\.[0-9a-fA-F]{1,4}){2}")) {
            final byte[] mac = new byte[MAC.MAC_LENGTH];
            final String[] words = macAddress.split("\\.");

            for (int i = 0; i < words.length; i++) {
                final int value = Integer.parseInt(words[i], 16);
                mac[i << 1] = (byte) (value >> BITS_PER_BYTE);
                mac[(i << 1) + 1] = (byte) (value);
            }
            return mac;
        }

        throw new IllegalArgumentException("MAC of form "
                + macAddress + " unknown!");
    }
}
