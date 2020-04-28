package com.ionos.network.commons.address;

import java.util.StringTokenizer;

import static com.ionos.network.commons.address.BitsAndBytes.BYTE_MASK;

/**
 * IP parser implementations to parse addresses from text notation.
 * @see IP
 * @author Stephan Fuhrmann
 * @see AddressFormat
 * @author Stephan Fuhrmann
 **/
public final class IPParsers {
    /** Max value for a 16 bit value. */
    private static final int USHORT_MAX_VALUE = 0xffff;

    /** Radix value for displaying hexadecimal numbers. */
    private static final int HEX_RADIX = 16;

    /** No instance allowed. */
    private IPParsers() {
        // no instance allowed
    }

    private abstract static class AbstractIPv4Parser implements AddressParser<IPv4> {
        public IPv4 parse(String address) {
            return new IPv4(parseAsBytes(address));
        }
    }

    private abstract static class AbstractIPv6Parser implements AddressParser<IPv6> {
        public IPv6 parse(String address) {
            return new IPv6(parseAsBytes(address));
        }
    }

    private static void throwExpected(String component, String address) {
        throw new IllegalArgumentException("Expected '" + component + "' in address '" + address + "', but couldn't find");
    }

    private static void throwOutOfRange(String component, String address) {
        throw new IllegalArgumentException("Component '" + component + "' of address '" + address + "' is out of range");
    }

    private static void throwMaformed(String address) {
        throw new IllegalArgumentException("Address or address part '" + address + "' is malformed");
    }

    private static void throwWrongNumberOfComponents(String address) {
        throw new IllegalArgumentException("Address '" + address + "' has wrong number of components");
    }

    /**
     * Parses an IP address in every possible known format.
     */
    public static final AddressParser<IP> DEFAULT = new AddressParser<IP>() {
        private AddressParser<? extends IP> guess(String address) {
            boolean doubleColon = address.contains("::");
            boolean colon = address.contains(":");
            boolean dot = address.contains(".");
            if (doubleColon) {
                if (dot) {
                    return RFC4291_3_COMPRESSED;
                } else {
                    return RFC4291_2;
                }
            } else {
                if (dot && colon) {
                    return RFC4291_3_FULL;
                } else if (colon) {
                    return RFC4291_1;
                } else if (dot) {
                    return DOTTED_DECIMAL;
                }
            }
            throw new IllegalArgumentException("Address format of '" + address + "' unknown");
        }

        public IP parse(String address) {
            AddressParser<? extends IP> parser = guess(address);
            return parser.parse(address);
        }

        @Override
        public byte[] parseAsBytes(String address) {
            AddressParser<? extends IP> parser = guess(address);
            return parser.parseAsBytes(address);
        }
    };


    /**
     * Parses an IPV6 address in every possible known format.
     */
    public static final AddressParser<IPv6> IPV6 = new AddressParser<IPv6>() {
        private AddressParser<IPv6> guess(String address) {
            boolean doubleColon = address.contains("::");
            boolean colon = address.contains(":");
            boolean dot = address.contains(".");
            if (doubleColon) {
                if (dot) {
                    return RFC4291_3_COMPRESSED;
                } else {
                    return RFC4291_2;
                }
            } else {
                if (dot && colon) {
                    return RFC4291_3_FULL;
                } else if (colon) {
                    return RFC4291_1;
                }
            }
            throw new IllegalArgumentException("Address format of '" + address + "' unknown");
        }

        public IPv6 parse(String address) {
            AddressParser<IPv6> parser = guess(address);
            return parser.parse(address);
        }

        @Override
        public byte[] parseAsBytes(String address) {
            AddressParser<? extends IP> parser = guess(address);
            return parser.parseAsBytes(address);
        }
    };

    /**
     * Parses a IPv4 address in the decimal dot format.
     * @see IPFormats#DOTTED_DECIMAL
     */
    public static final AddressParser<IPv4> DOTTED_DECIMAL = new AbstractIPv4Parser() {
        @Override
        public byte[] parseAsBytes(String address) {
            StringTokenizer stringTokenizer = new StringTokenizer(address, ".");
            if (stringTokenizer.countTokens() != IPVersion.IPV4.getAddressBytes()) {
                throwWrongNumberOfComponents(address);
            }
            byte[] result = new byte[IPVersion.IPV4.getAddressBytes()];

            for (int i = 0; stringTokenizer.hasMoreTokens(); i++) {
                String component = stringTokenizer.nextToken();
                int val = Integer.parseInt(component);

                if (val < 0 || val > BYTE_MASK) {
                    throwOutOfRange(component, address);
                }
                result[i] = (byte) val;
            }
            return result;
        }
    };


    private static byte[] parseColonHexVariableLength(String address) {
        int numbers = new StringTokenizer(address, ":", false).countTokens();
        StringTokenizer stringTokenizer = new StringTokenizer(address, ":", true);

        boolean expectNumber = true;
        byte[] result = new byte[2 * numbers];
        int i = 0;
        while (stringTokenizer.hasMoreTokens()) {
            String component = stringTokenizer.nextToken();
            if (component.length() == 0 || component.length() > 4) {
                throwOutOfRange(component, address);
            }
            if (expectNumber) {
                if (component.equals(":")) {
                    throwMaformed(address);
                }
                int val = Integer.parseInt(component, HEX_RADIX);
                if (val < 0 || val > USHORT_MAX_VALUE) {
                    throwOutOfRange(component, address);
                }
                result[i++] = (byte) (val >>> 8);
                result[i++] = (byte) val;
            } else if (!component.equals(":")) {
                throwMaformed(address);
            }
            expectNumber = !expectNumber;
        }
        return result;
    }

    /**
     * Parses a IPv6 address in RFC 4291 variant 1.
     * Example:
     * {@code abcd:abcd:abcd:abcd:abcd:abcd:abcd:abcd}.
     * @see <a href="http://tools.ietf.org/html/rfc4291#section-2.2">
     *     RFC 4291, Section 2.2</a>
     */
    public static final AddressParser<IPv6> RFC4291_1 = new AbstractIPv6Parser() {
        @Override
        public byte[] parseAsBytes(String address) {
            byte[] result = parseColonHexVariableLength(address);
            if (result.length != IPVersion.IPV6.getAddressBytes()) {
                throwWrongNumberOfComponents(address);
            }
            return result;
        }
    };

    /**
     * Parses a IPv6 address in RFC 4291 variant 2.
     * Example:
     * {@code 111:222::333:444}.
     * @see <a href="http://tools.ietf.org/html/rfc4291#section-2.2">
     *     RFC 4291, Section 2.2</a>
     */
    public static final AddressParser<IPv6> RFC4291_2 = new AbstractIPv6Parser() {
        @Override
        public byte[] parseAsBytes(String address) {
            int doubleDotIndex = address.indexOf("::");
            if (doubleDotIndex == -1) {
                throw new IllegalArgumentException("Not a RFC4291-2 address: " + address);
            }
            String left = address.substring(0, doubleDotIndex);
            String right = address.substring(doubleDotIndex + 2);

            byte[] leftComponents = parseColonHexVariableLength(left);
            byte[] rightComponents = parseColonHexVariableLength(right);

            int rightOffset = IPVersion.IPV6.getAddressBytes()
                    - rightComponents.length;

            if (leftComponents.length + rightComponents.length
                    >= IPVersion.IPV6.getAddressBytes()) {
                throw new IllegalArgumentException("Too long address " + address);
            }
            byte[] result = new byte[IPVersion.IPV6.getAddressBytes()];
            System.arraycopy(leftComponents, 0, result, 0, leftComponents.length);
            System.arraycopy(rightComponents, 0, result, rightOffset,
                    rightComponents.length);

            return result;
        }
    };

    /**
     * Parses a IPv6 address in RFC 4291 variant 3, full form.
     * Example:
     * {@code abcd:abcd:abcd:abcd:abcd:abcd:123.123.123.123}.
     * @see <a href="http://tools.ietf.org/html/rfc4291#section-2.2">
     *     RFC 4291, Section 2.2</a>
     */
    public static final AddressParser<IPv6> RFC4291_3_FULL = new AbstractIPv6Parser() {
        @Override
        public byte[] parseAsBytes(String address) {
            int lastColon = address.lastIndexOf(':');
            if (lastColon == -1) {
                throwExpected(":", address);
            }
            byte[] ipv6Part = parseColonHexVariableLength(address.substring(0, lastColon));
            if (ipv6Part.length != IPVersion.IPV6.getAddressBytes() - IPVersion.IPV4.getAddressBytes()) {
                throwWrongNumberOfComponents(address);
            }

            byte[] ipv4Part = DOTTED_DECIMAL.parseAsBytes(address.substring(lastColon + 1));

            byte[] result = new byte[IPVersion.IPV6.getAddressBytes()];

            System.arraycopy(ipv6Part, 0, result, 0, ipv6Part.length);
            System.arraycopy(ipv4Part, 0, result, 12,
                    ipv4Part.length);

            return result;
        }
    };

    /**
     * Parses a IPv6 address in RFC 4291 variant 3, full form.
     * Example:
     * {@code abcd:abcd:abcd:abcd:abcd:abcd:123.123.123.123}.
     * @see <a href="http://tools.ietf.org/html/rfc4291#section-2.2">
     *     RFC 4291, Section 2.2</a>
     */
    public static final AddressParser<IPv6> RFC4291_3_COMPRESSED = new AbstractIPv6Parser() {
        @Override
        public byte[] parseAsBytes(String address) {
            int doubleColon = address.indexOf("::");
            if (doubleColon == -1) {
                throwExpected("::", address);
            }
            int lastColon = address.lastIndexOf(':');
            if (lastColon == -1) {
                throwExpected(":", address);
            }
            byte[] leftV6Components = parseColonHexVariableLength(address.substring(0, doubleColon));
            byte[] rightV6Components;
            if (lastColon > doubleColon + 1) {
                rightV6Components = parseColonHexVariableLength(address.substring(doubleColon + 2, lastColon));
            } else {
                rightV6Components = new byte[0];
            }
            int rightOffset = IPVersion.IPV6.getAddressBytes() - IPVersion.IPV4.getAddressBytes() - rightV6Components.length;
            byte[] v4Components = DOTTED_DECIMAL.parseAsBytes(address.substring(lastColon + 1));

            byte[] result = new byte[IPVersion.IPV6.getAddressBytes()];
            System.arraycopy(leftV6Components,
                    0,
                    result,
                    0,
                    leftV6Components.length);
            System.arraycopy(rightV6Components,
                    0,
                    result,
                    rightOffset, rightV6Components.length);
            System.arraycopy(v4Components,
                    0,
                    result,
                    IPVersion.IPV6.getAddressBytes() - IPVersion.IPV4.getAddressBytes(),
                    v4Components.length);

            return result;
        }
    };
}
