package com.ionos.network.commons.address;

import java.util.Objects;
import java.util.regex.Pattern;

import static com.ionos.network.commons.address.BitsAndBytes.BITS_PER_BYTE;

/**
 * Parses an IP address from text notation.
 * @see IP
 * @author Stephan Fuhrmann
 **/
public final class MACParser implements AddressParser<MAC> {

    /** The singleton instance for parsing MAC addresses. */
    public static final AddressParser<MAC> INSTANCE = new MACParser();

    /** Regex to recognize format {@code 00:11:22:33:44:55}.
     * @see MACFormats#COLON_SEPARATED_HEX_FORMAT
     * */
    private static final Pattern COLON_SEPARATED_HEX_FORMAT =
            Pattern.compile("[0-9a-fA-F]{2}(:[0-9a-fA-F]{2}){5}");

    /** Regex to recognize format {@code 0011.2233.4455}.
     * @see MACFormats#CISCO_CUSTOM_FORMAT
     * */
    private static final Pattern CISCO_CUSTOM_FORMAT =
            Pattern.compile("[0-9a-fA-F]{1,4}(\\.[0-9a-fA-F]{1,4}){2}");

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
     * @throws NullPointerException if macAddress is {@code null}.
     * @throws IllegalArgumentException if macAddress format does not match a known format.
     */
    @Override
    public byte[] parseAsBytes(final String macAddress) {
        Objects.requireNonNull(macAddress, "Argument must not be null");
        if (macAddress.length() == 0) {
            throw new IllegalArgumentException(
                    "given string has a zero length");
        }

        // single byte form: 00:00:00:00:00:00
        if (COLON_SEPARATED_HEX_FORMAT.matcher(macAddress).matches()) {
            final byte[] mac = new byte[MAC.MAC_LENGTH];
            final String[] octets = macAddress.split(":");

            for (int i = 0; i < octets.length; i++) {
                final int value = Integer.parseInt(octets[i], 16);
                mac[i] = (byte) value;
            }
            return mac;
        }

        // two byte cisco form: 0000.0000.0000
        if (CISCO_CUSTOM_FORMAT.matcher(macAddress).matches()) {
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
