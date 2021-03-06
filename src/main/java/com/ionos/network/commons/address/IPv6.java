package com.ionos.network.commons.address;

import java.io.Serializable;

/**
 * IP for the IPv6 protocol.
 * Stores the address bytes and has methods to calculate with IPs.
 * <br>
 * Objects of the IPv6 class are immutable!
 *
 * @see <a href="http://tools.ietf.org/html/rfc4291#section-2.2">RFC 4291,
 * Textual IPv6 representations</a>
 * @see java.net.Inet6Address
 * @see IPFormats
 * @see IPParsers
 * @see IPVersion
 * @author Stephan Fuhrmann
 *
 **/
public final class IPv6 extends IP<IPv6> implements Address, Serializable {
    /** The version number of this class. */
    private static final long serialVersionUID = 6626793331933215894L;

    /** The IPv6 address {@code 0::0}. */
    public static final IPv6 IPV6_ALL_ZERO =
            new IPv6(BitsAndBytes.newArrayOf(
                    IPVersion.IPV6.getAddressBytes(), (byte) 0));

    /** The IPv6 address {@code ffff:...:ffff}. */
    public static final IPv6 IPV6_ALL_ONE =
            new IPv6(BitsAndBytes.newArrayOf(
                    IPVersion.IPV6.getAddressBytes(),
                    (byte) BitsAndBytes.UBYTE_MAX_VALUE));

    /**
     * Creates a new IP address from the address bytes.
     *
     * @param inAddress a 16 byte address in network byte order.
     */
    protected IPv6(final byte[] inAddress) {
        super(inAddress, IPVersion.IPV6.getAddressBytes());
    }

    /**
     * Creates a new IP address from the address bytes.
     *
     * @param inAddress a String containing the IP address in a
     *                  notation supported by {@linkplain IPParsers#IPV6}.
     * @see IPParsers#IPV6
     */
    public IPv6(final String inAddress) {
        this(IPParsers.IPV6.parseAsBytes(inAddress));
    }

    /**
     * Gets the IP version used in this IP address.
     * @return the IP version of this address.
     */
    public IPVersion getIPVersion() {
        return IPVersion.IPV6;
    }

    @Override
    protected IPv6 newInstance(final byte[] address) {
        return new IPv6(address);
    }

    @Override
    protected AddressFormat<IPv6> defaultAddressFormat() {
        return IPFormats.COLON_SEPARATED_HEXTETS;
    }
}
