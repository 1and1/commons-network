package com.ionos.network.commons.address;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static com.ionos.network.commons.address.BitsAndBytes.BYTE_MASK;
import static com.ionos.network.commons.address.BitsAndBytes.BITS_PER_BYTE;

/**
 * Parses an IP address from text notation.
 * @see IP
 * @author Stephan Fuhrmann
 *
 **/
public final class IPParser implements AddressParser<IP> {

    /** Exception message for an address component being out of range. */
    private static final String OUT_OF_RANGE_ERROR =
            "Component %s is out of range";

    /** Exception message for the left side of an address
     * being malformed. */
    private static final String LEFT_SIDE_MALFORMED_ERROR =
            "Left side '%s' malformed";

    /** Exception message for the right side of an address
     * being malformed. */
    private static final String RIGHT_SIDE_MALFORMED_ERROR =
            "Right side '%s' malformed";

    static {
        IPV4_PATTERN = Pattern.compile("([0-9]{1,3})"
                + "\\.([0-9]{1,3})"
                + "\\.([0-9]{1,3})"
                + "\\.([0-9]{1,3})");
        IPV6_PATTERN = Pattern.compile("([0-9a-fA-F]{1,4})"
                + "(:([0-9a-fA-F]{1,4})){7}");
        IPV6_PART_PATTERN = Pattern.compile("([0-9a-fA-F]{1,4})?"
                + "(:[0-9a-fA-F]{1,4}){0,7}");
        IPV6_PATTERN_TYPE_3 = Pattern.compile(
                "(([0-9a-fA-F]{1,4})"
                    + "(:([0-9a-fA-F]{1,4})){5})"
                    + ":(([0-9]{1,3})"
                    + "\\.([0-9]{1,3})"
                    + "\\.([0-9]{1,3})"
                    + "\\.([0-9]{1,3}))");
        INSTANCE = new IPParser();
    }

    /** The group number of the IPv6 group in {@link #IPV6_PATTERN_TYPE_3}. */
    private static final int IPV6_PATTERN_TYPE_3_IPV6_GROUP = 1;
    /** The group number of the IPv4 group in {@link #IPV6_PATTERN_TYPE_3}. */
    private static final int IPV6_PATTERN_TYPE_3_IPV4_GROUP = 5;

    /** The singleton instance for parsing IP addresses. */
    public static final AddressParser<IP> INSTANCE;

    /**
     * The pattern of an IPv4 address. Note that the range of the digits
     * are checked.
     */
    private static final Pattern IPV4_PATTERN;

    /**
     * The pattern of an IPv6 address.
     * This is the full address without the short form.
     *
     * @see <a href="http://tools.ietf.org/html/rfc4291#section-2.2">
     *     rfc4291</a>
     */
    private static final Pattern IPV6_PATTERN;

    /**
     * A part of an IPv6 address. May contain one to many colon separated
     * hex-sections.
     * Examples: <code>a</code> or <code>aaaa:b:cccc</code>
     */
    private static final Pattern IPV6_PART_PATTERN;

    /**
     * Full Ipv6 with an IPv4 ending like pattern type 3 from rfc 4291.
     *
     * @see <a href="http://tools.ietf.org/html/rfc4291#section-2.2">rfc4291</a>
     */
    private static final Pattern IPV6_PATTERN_TYPE_3;

    /** Private constructor. No instance allowed. */
    private IPParser() {
        // no instance allowed
    }

    /**
     * Parse the IP contained in the String.
     *
     * @param str the textual representation of the IP to parse. Can be an IPv4
     *            of the form <code>a.b.c.d</code>
     *            or an IPv6 of the form <code>a:b:c:d:e:f:g:h</code>.
     * @return the bytes representing the parsed address
     * @see <a href="http://tools.ietf.org/html/rfc4291#section-2.2">rfc4291</a>
     */
    @Override
    public IP parse(final String str) {
        return new IP(parseAsBytes(str));
    }

    @Override
    public byte[] parseAsBytes(final String str) {
        if (str == null) {
            throw new NullPointerException("str is null");
        }
        if (str.length() == 0) {
            throw new IllegalArgumentException(
                    "given string has a zero length");
        }

        // if it contains dots I expect it to be a IPv4
        // form: 123.13.123.12
        Matcher m = IPV4_PATTERN.matcher(str);
        if (m.matches()) {
            return parseDottedIPv4(m);
        }

        // rfc4291 variant 1
        // ipv6 address of the full form?
        // abcd:abcd:abcd:abcd:abcd:abcd:abcd:abcd
        m = IPV6_PATTERN.matcher(str);
        if (m.matches()) {
            // right length is ensured by the regex above
            return parseIPv6Variant1(str);
        }

        // rfc4291 variant 2
        // ipv6 address of the short form?
        // abcd:abcd::abcd:abcd:abcd:abcd
        int idxDoubleColon = str.indexOf("::");
        int idxDot = str.indexOf(".");
        if (idxDoubleColon != -1 && idxDot == -1) {
            return parseIPv6Variant2(str, idxDoubleColon);
        }

        // rfc4291 variant 3, full form
        // 1111:2222:3333:4444:5555:6666:7.8.9.10
        if (idxDoubleColon == -1 && idxDot != -1) {
            return parseIPv6Variant3Full(str);
        }

        // rfc4291 variant 3, compressed form
        // 1111:2222::4444:5555:6666:7.8.9.10
        if (idxDoubleColon != -1 && idxDot != -1) {
            return parseIPv6Variant3Compressed(str, idxDoubleColon);
        }

        throw new IllegalArgumentException(
                "Unknown address type used in " + str);
    }

    /**
     * Parses a variant 3 IPv6 with compression.
     * @param str the String to parse a IPv6 address from.
     * @param idxDoubleColon the index of the double colon separator.
     * @return the parsed IPv6 address bytes.
     */
    private byte[] parseIPv6Variant3Compressed(final String str,
                                               final int idxDoubleColon) {
        int lastColon = str.lastIndexOf(':');
        boolean hasLastColon = lastColon != idxDoubleColon
                && lastColon != idxDoubleColon + 1;

        final int v4len = IPVersion.IPv4.getAddressBytes();
        final int v6len = IPVersion.IPv6.getAddressBytes();
        final int v6partLen = v6len - v4len;
        String leftV6Side = str.substring(0, idxDoubleColon);
        String rightV6Side = "";
        if (hasLastColon) {
            rightV6Side = str.substring(idxDoubleColon + 2, lastColon);
        }
        String v4Part = str.substring(lastColon + 1);

        if (!IPV6_PART_PATTERN.matcher(leftV6Side).matches()) {
            throw new IllegalArgumentException(
                    String.format(LEFT_SIDE_MALFORMED_ERROR, leftV6Side));
        }
        if (!IPV6_PART_PATTERN.matcher(rightV6Side).matches()) {
            throw new IllegalArgumentException(
                    String.format(RIGHT_SIDE_MALFORMED_ERROR, rightV6Side));
        }
        byte[] leftV6Components = parseIPv6Variant1(leftV6Side);
        byte[] rightV6Components = parseIPv6Variant1(rightV6Side);
        byte[] v4Components = parseDecV4Array(v4Part);

        if (leftV6Components.length >= v6partLen
                || rightV6Components.length >= v6partLen
                || leftV6Components.length + rightV6Components.length
                        >= v6partLen
                || v4Components.length != IPVersion.IPv4.getAddressBytes()) {
            throw new IllegalArgumentException(
                    "Malformed address '" + str + "'");
        }
        int rightOffset = v6partLen - rightV6Components.length;

        byte[] adr = new byte[v6len];

        System.arraycopy(leftV6Components,
                0,
                adr,
                0,
                leftV6Components.length);
        System.arraycopy(rightV6Components,
                0,
                adr,
                rightOffset, rightV6Components.length);
        System.arraycopy(v4Components,
                0,
                adr,
                v6partLen,
                v4Components.length);

        return adr;
    }

    /**
     * Parses a variant 3 IPv6.
     * @param str the String to parse a IPv6 address from.
     * @return the parsed IPv6 address bytes.
     */
    private byte[] parseIPv6Variant3Full(final String str) {
        Matcher t3 = IPV6_PATTERN_TYPE_3.matcher(str);
        if (t3.matches()) {
            String v6part = t3.group(IPV6_PATTERN_TYPE_3_IPV6_GROUP);
            String v4part = t3.group(IPV6_PATTERN_TYPE_3_IPV4_GROUP);

            byte[] v6Components = parseIPv6Variant1(v6part);
            byte[] v4Components = parseDecV4Array(v4part);

            if (v6Components.length
                    != IPVersion.IPv6.getAddressBytes()
                            - IPVersion.IPv4.getAddressBytes()) {
                throw new IllegalArgumentException(
                        "IPv6 part has illegal component count "
                                + v6Components.length);
            }
            if (v4Components.length != IPVersion.IPv4.getAddressBytes()) {
                throw new IllegalArgumentException(
                        "IPv4 part has illegal component count "
                                + v4Components.length);
            }
            byte[] adr = new byte[IPVersion.IPv6.getAddressBytes()];

            System.arraycopy(v6Components,
                    0,
                    adr,
                    0,
                    v6Components.length);
            System.arraycopy(v4Components,
                    0,
                    adr,
                    v6Components.length,
                    v4Components.length);

            return adr;
        }
        throw new IllegalArgumentException("could not parse " + str);
    }

    /**
     * Parses a variant 2 IPv6.
     * @param str the String to parse a IPv6 address from.
     * @param idxDoubleColon the index of the double colon separator.
     * @return the parsed IPv6 address bytes.
     */
    private byte[] parseIPv6Variant2(final String str,
                                     final int idxDoubleColon) {
        String leftSide = str.substring(0, idxDoubleColon);
        String rightSide = str.substring(idxDoubleColon + 2);

        if (!IPV6_PART_PATTERN.matcher(leftSide).matches()) {
            throw new IllegalArgumentException(
                    String.format(LEFT_SIDE_MALFORMED_ERROR, leftSide));
        }
        if (!IPV6_PART_PATTERN.matcher(rightSide).matches()) {
            throw new IllegalArgumentException(
                    String.format(RIGHT_SIDE_MALFORMED_ERROR, rightSide));
        }
        byte[] leftComponents = parseIPv6Variant1(leftSide);
        byte[] rightComponents = parseIPv6Variant1(rightSide);

        int rightOffset = IPVersion.IPv6.getAddressBytes()
                - rightComponents.length;

        if (leftComponents.length + rightComponents.length
                >= IPVersion.IPv6.getAddressBytes()) {
            throw new IllegalArgumentException("Too long address " + str);
        }
        byte[] adr = new byte[IPVersion.IPv6.getAddressBytes()];

        System.arraycopy(leftComponents, 0, adr, 0, leftComponents.length);
        System.arraycopy(rightComponents, 0, adr, rightOffset,
                rightComponents.length);

        return adr;
    }

    /** Parses an address in the form {@code 123.234.111.1}.
     * @param m the matcher that is matching an IPv4 address.
     * @return the parsed address as a byte array.
     * */
    private byte[] parseDottedIPv4(final Matcher m) {
        byte[] adr = new byte[IPVersion.IPv4.getAddressBytes()];
        for (int i = 0; i < adr.length; i++) {
            int val = Integer.parseInt(m.group(i + 1));

            if (val < 0 || val > BYTE_MASK) {
                throw new IllegalArgumentException(
                        String.format(OUT_OF_RANGE_ERROR, val));
            }
            adr[i] = (byte) val;
        }

        return adr;
    }

    /** Max value for a 16 bit value. */
    private static final int USHORT_MAX_VALUE = 0xffff;

    /** Radix value for displaying hexadecimal numbers. */
    private static final int HEX_RADIX = 16;

    /**
     * Parse an IPv6 style colon separated hex array that must
     * match {@link #IPV6_PART_PATTERN}.
     *
     * @param inHex a hex array of the form 0000:1234:ffff with
     *              one to eight segments
     * @return the parsed address bytes, length 0 to 16 bytes.
     * @throws IllegalArgumentException if a component is out
     * of range or there are too many components
     */
    private static byte[] parseIPv6Variant1(final String inHex) {
        String[] components;
        if (inHex.length() > 0) {
            components = inHex.split(":");
        } else {
            components = new String[0];
        }

        if (components.length > IPVersion.IPv6.getAddressBytes() / 2) {
            throw new IllegalArgumentException("Too many components");
        }
        byte[] result = new byte[components.length * 2];

        for (int i = 0; i < components.length; i++) {
            int val = Integer.parseInt(components[i], HEX_RADIX);

            if (val < 0 || val > USHORT_MAX_VALUE) {
                throw new IllegalArgumentException(
                        String.format(OUT_OF_RANGE_ERROR, val));
            }
            result[i << 1] = (byte) (val >> BITS_PER_BYTE);
            result[(i << 1) + 1] = (byte) (val);
        }
        return result;
    }

    /**
     * Parse an IPv4 style dot separated decimal array that must
     * match {@link #IPV4_PATTERN}.
     *
     * @param inDec the array of decimals
     * @return the parsed address bytes, length 0 to 4 bytes.
     * @throws IllegalArgumentException if a component is out of range or
     * there are too many components
     */
    private static byte[] parseDecV4Array(final String inDec) {
        String[] components;
        if (inDec.length() > 0) {
            components = inDec.split("\\.");
        } else {
            components = new String[0];
        }
        if (components.length > IPVersion.IPv4.getAddressBytes()) {
            throw new IllegalArgumentException("Too many components");
        }
        if (components.length < IPVersion.IPv4.getAddressBytes()) {
            throw new IllegalArgumentException("Too few components");
        }
        byte[] result = new byte[components.length];

        for (int i = 0; i < components.length; i++) {
            int val = Integer.parseInt(components[i]);

            if (val < 0 || val > BYTE_MASK) {
                throw new IllegalArgumentException(
                        String.format(OUT_OF_RANGE_ERROR, val));
            }
            result[i] = (byte) val;
        }
        return result;
    }
}
