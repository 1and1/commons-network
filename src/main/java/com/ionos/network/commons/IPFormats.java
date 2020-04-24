package com.ionos.network.commons;

import java.io.IOException;

import static com.ionos.network.commons.BitsAndBytes.BYTE_MASK;
import static com.ionos.network.commons.BitsAndBytes.BITS_PER_BYTE;

/**
 * Text formatting alternatives for IP address in multiple notations.
 * @see IP
 * @see AddressFormat
 * @author Stephan Fuhrmann
 *
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
    public static final AddressFormat<IP> DOTTED_DECIMAL =
            new AbstractAddressFormat<IP>() {
                @Override
                public <A extends Appendable> A format(
                        final IP ip,
                        final A toAppendTo)
                        throws IOException {
                    final byte[] address = ip.getBytes();
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
    public static final AddressFormat<IP>
            COLON_SEPARATED_HEXTETS =
            new AbstractAddressFormat<IP>() {
                @Override
                public <A extends Appendable> A format(
                        final IP ip,
                        final A toAppendTo)
                        throws IOException {
                    final byte[] address = ip.getBytes();
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
    public static final AddressFormat<IP>
            COLON_SEPARATED_ZEROED_HEXTETS =
            new AbstractAddressFormat<IP>() {
                @Override
                public <A extends Appendable> A format(
                        final IP ip,
                        final A toAppendTo)
                        throws IOException {
                    final byte[] address = ip.getBytes();
                    // everything else goes HEX with word components
                    for (int i = 0; i < address.length; i += 2) {
                        if (i > 0) {
                            toAppendTo.append(':');
                        }
                        int val = ((address[i] & BYTE_MASK) << BITS_PER_BYTE)
                                | (address[i + 1] & BYTE_MASK);
                        toAppendTo.append(String.format("%04x", val));
                    }
                    return toAppendTo;
                }
            };

    /** Canonical IPv6 formatter. This is the compressed
     * colon-separated notation in lower case.
     * Example: {@code ::ffff:1111:5555:f:44:aa:0}.
     * @see <a href="https://tools.ietf.org/html/rfc5952">
     *     A Recommendation for IPv6 Address Text
     *     Representation</a>
     * */
    public static final AddressFormat<IP>
            RFC_5952 =
            new AbstractAddressFormat<IP>() {
                @Override
                public <A extends Appendable> A format(
                        final IP ip,
                        final A toAppendTo)
                        throws IOException {
                    final byte[] address = ip.getBytes();
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
     * Only makes sense for {@link IPVersion#IPv4}.
     * */
    public static final AddressFormat<IP> SYSTEM_ID_NOTATION =
            new AbstractAddressFormat<IP>() {
                    @Override
                    public <A extends Appendable> A format(
                            final IP ip,
                            final A toAppendTo)
                            throws IOException {
                        final byte[] address = ip.getBytes();
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

    /** Appends two bytes to an Appendable as hex.
     * @param to the Appendable to append to.
     * @param upper the most significant byte.
     * @param lower the least significant byte.
     * @throws IOException when appending to the Appendable has a problem.
     * */
    private static void appendHex(final Appendable to,
                                  final byte upper,
                                  final byte lower)
            throws IOException {
        int val = ((upper & BYTE_MASK) << BITS_PER_BYTE)
                | (lower & BYTE_MASK);
        to.append(Integer.toHexString(val));
    }
}
