package com.ionos.network.commons;

import java.io.IOException;
import static com.ionos.network.commons.BitsAndBytes.BYTE_MASK;

/**
 * Formats a MAC address to text form in multiple notations.
 * @see MAC
 * @see AddressFormat
 * @author Stephan Fuhrmann
 *
 * */
public final class MACFormats {


    /**
     * Private constructor. No instance allowed.
     */
    private MACFormats() {
        // no instance allowed
    }

    /**
     * Returns a single byte hex representation of the MAC.
     * <br>
     * <i>Example:</i> {@code 00:ac:ac:dd:00:12}.
     */
    public static final AddressFormat<MAC> COLON_SEPARATED_HEX_FORMAT =
            new AbstractAddressFormat<MAC>() {
                @Override
                public <A extends Appendable> A format(
                        final MAC mac,
                        final A toAppendTo)
                        throws IOException {
                    final byte[] address = mac.getBytes();

                    for (int i = 0; i < address.length; i++) {
                        int val = address[i] & BYTE_MASK;
                        if (i != 0) {
                            toAppendTo.append(':');
                        }
                        // create hex string with leading zeroes
                        toAppendTo.append(String.format("%02X", val)
                                .toUpperCase());
                    }

                    return toAppendTo;
                }
            };


    /**
     * Word hex representation of the MAC in CISCO style.
     * <br>
     * <i>Example:</i>{@code 00ac.acdd.0012}.
     */
    public static final AddressFormat<MAC> CISCO_CUSTOM_FORMAT =
            new AbstractAddressFormat<MAC>() {
                @Override
                public <A extends Appendable> A format(
                        final MAC mac,
                        final A toAppendTo)
                        throws IOException {
                    final byte[] address = mac.getBytes();
                    for (int i = 0; i < address.length; i++) {
                        int val = address[i] & BYTE_MASK;
                        // only before digits 2 and 4
                        if (i != 0 && (i & 1) == 0) {
                            toAppendTo.append('.');
                        }
                        // create hex string with leading zeroes
                        toAppendTo.append(String.format("%02X", val)
                                .toLowerCase());
                    }

                    return toAppendTo;
                }
            };
}
