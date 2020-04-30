package com.ionos.network.commons.address;

import java.io.IOException;

import static com.ionos.network.commons.address.BitsAndBytes.BYTE_MASK;
import static com.ionos.network.commons.address.BitsAndBytes.appendHex;
import static com.ionos.network.commons.address.BitsAndBytes.appendHexWithLeadingZeros;

/**
 * Text formatting alternatives for IP address in multiple notations.
 * @see IP
 * @see AddressFormat
 * @author Stephan Fuhrmann
 **/
public final class IPFormats {

    /** No instance allowed. */
    private IPFormats() {
        // no instance allowed
    }

    /** IP formatter in dotted decimal notation, like {@code 172.20.0.11}.
     * @see <a href="https://en.wikipedia.org/wiki/Dot-decimal_notation">
     *     Dot-decimal notation</a>
     * */
    public static final AddressFormat<IPv4> DOTTED_DECIMAL =
            new AbstractAddressFormat<IPv4>() {
                @Override
                public <A extends Appendable> A format(
                        final IPv4 ip,
                        final A toAppendTo)
                        throws IOException {
                    final byte[] address = ip.address;
                    // decimals with dot separators
                    for (int i = 0; i < address.length; i++) {
                        if (i > 0) {
                            toAppendTo.append('.');
                        }
                        toAppendTo.append(Integer.toString(
                                BYTE_MASK & address[i]));
                    }
                    return toAppendTo;
                }
    };

    /** IP formatter in uncompressed colon-separated notation, like
     * {@code 0:ffff:1111:5555:f:44:aa:0}.
     * @see <a href="https://en.wikipedia.org/wiki/IPv6_address">
     *     IPv6 address</a>
     * */
    public static final AddressFormat<IPv6>
            COLON_SEPARATED_HEXTETS =
            new AbstractAddressFormat<IPv6>() {
                @Override
                public <A extends Appendable> A format(
                        final IPv6 ip,
                        final A toAppendTo)
                        throws IOException {
                    final byte[] address = ip.address;
                    // everything else goes HEX with word components
                    for (int i = 0; i < address.length; i += 2) {
                        if (i > 0) {
                            toAppendTo.append(':');
                        }
                        appendHex(toAppendTo, address[i], address[i + 1]);
                    }
                    return toAppendTo;
                }
    };

    /** IP formatter in leading-zero uncompressed colon-separated
     * notation,
     * like {@code 0000:ffff:1111:5555:000f:0044:00aa:0000}.
     * This IP address format has a fixed character size.
     * @see <a href="https://en.wikipedia.org/wiki/IPv6_address">
     *     IPv6 address</a>
     * */
    public static final AddressFormat<IPv6>
            COLON_SEPARATED_ZEROED_HEXTETS =
            new HexadecimalAddressFormat<IPv6>(':', 4);

    /** Canonical IPv6 formatter. This is the compressed
     * colon-separated notation in lower case.
     * Example: {@code ::ffff:1111:5555:f:44:aa:0}.
     * @see <a href="https://tools.ietf.org/html/rfc5952">
     *     A Recommendation for IPv6 Address Text
     *     Representation</a>
     * */
    public static final AddressFormat<IPv6>
            RFC_5952 =
            new AbstractAddressFormat<IPv6>() {
                @Override
                public <A extends Appendable> A format(
                        final IPv6 ip,
                        final A toAppendTo)
                        throws IOException {
                    final byte[] address = ip.address;
                    // offset of longest 0-run
                    int maxOfs = -1;
                    // length of longest 0-run
                    int maxLen = -1;
                    for (int i = 0; i < address.length; i += 2) {
                        int curLen = 0;
                        for (int j = 0; i + j < address.length; j += 2) {
                            if (address[i + j] == 0
                                    && address[i + j + 1] == 0) {
                                curLen += 2;
                            } else {
                                break;
                            }
                        }

                        if (curLen > maxLen) {
                            maxOfs = i;
                            maxLen = curLen;
                        }
                    }

                    if (maxLen <= 2) {
                        return COLON_SEPARATED_HEXTETS
                                .format(ip, toAppendTo);
                    }
                    // left part
                    for (int i = 0; i < maxOfs; i += 2) {
                        if (i > 0) {
                            toAppendTo.append(':');
                        }
                        appendHex(toAppendTo, address[i], address[i + 1]);
                    }
                    toAppendTo.append(':');

                    // right part
                    for (int i = maxOfs + maxLen; i < address.length; i += 2) {
                        toAppendTo.append(':');
                        appendHex(toAppendTo, address[i], address[i + 1]);
                    }
                    if (maxOfs + maxLen == address.length) {
                        toAppendTo.append(':');
                    }
                    return toAppendTo;
                }
            };

    /** Maximum digits of a decimal byte. */
    private static final int MAX_DIGITS_DEC_BYTE = 3;

    /** IP formatter in system id notation as used in IS-IS, like
     * {@code 00 10 01 00 10 01}.
     * */
    public static final AddressFormat<IPv4> SYSTEM_ID_NOTATION =
            new AbstractAddressFormat<IPv4>() {
                    @Override
                    public <A extends Appendable> A format(
                            final IPv4 ip,
                            final A toAppendTo)
                            throws IOException {
                        final byte[] address = ip.address;
                        int count = 0;

                        for (byte b : address) {
                            String str = Integer.toString(b & BYTE_MASK);
                            for (int i = str.length();
                                 i < MAX_DIGITS_DEC_BYTE;
                                 i++) {
                                if (count > 0 && count % 2 == 0) {
                                    toAppendTo.append(' ');
                                }
                                count++;
                                toAppendTo.append('0');
                            }
                            for (char c : str.toCharArray()) {
                                if (count > 0 && count % 2 == 0) {
                                    toAppendTo.append(' ');
                                }
                                count++;
                                toAppendTo.append(c);
                            }
                        }
                        return toAppendTo;
                    }
            };
}
