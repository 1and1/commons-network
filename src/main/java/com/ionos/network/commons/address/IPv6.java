package com.ionos.network.commons.address;

import java.io.Serializable;

/**
 * IP in IPv6 protocol.
 * Stores the address bytes and has methods to calculate with IPs.
 * The IP class has some methods for calculation with IP numbers, for example
 * {@link #add(byte[])}, {@link #add(long)},
 * {@link AbstractAddress#compareTo(AbstractAddress)}.
 * <br>
 * Objects of the IP class are immutable!
 *
 * @see <a href="http://tools.ietf.org/html/rfc4291#section-2.2">RFC 4291,
 * Textual IPv6 representations</a>
 * @see java.net.Inet6Address
 * @see IPVersion
 * @author Stephan Fuhrmann
 *
 **/
public final class IPv6 extends IP<IPv6> implements Address, Serializable {
    /** The version number of this class. */
    private static final long serialVersionUID = 6626793331933215894L;

    /** The IPv6 address 0::0. */
    public static final IPv6 IPV6_ALL_ZERO =
            new IPv6(BitsAndBytes.newArrayOf(IPVersion.IPV6.getAddressBytes(), (byte)0));

    /** The IPv6 address ffff:...:ffff. */
    public static final IPv6 IPV6_ALL_ONE =
            new IPv6(BitsAndBytes.newArrayOf(IPVersion.IPV6.getAddressBytes(), (byte)255));

    /**
     * Creates a new IP address from the address bytes.
     *
     * @param inAddress a 16 byte address in network byte order.
     */
    protected IPv6(final byte[] inAddress) {
        super(inAddress);
        if (inAddress.length != IPVersion.IPV6.getAddressBytes()) {
            throw new IllegalArgumentException(
                    "IPv6 addresses must be 16 bytes long. "
                            + "Your address has " + inAddress.length
                            + " bytes.");
        }
    }

    /**
     * Creates a new IP address from the address bytes.
     *
     * @param inAddress a String containing the IP address in a
     *                  notation supported by {@linkplain IPParser}.
     * @see IPParser#parse(String)
     */
    public IPv6(final String inAddress) {
        this(IPParser.INSTANCE.parseAsBytes(inAddress));
    }

    /**
     * Gets the IP version used in this IP address.
     * @return the IP version of this address.
     */
    public IPVersion getIPVersion() {
        return IPVersion.IPV6;
    }

    @Override
    protected IPv6 newInstance(byte[] address) {
        return new IPv6(address);
    }

    @Override
    protected AddressFormat<IPv6> defaultAddressFormat() {
        return IPFormats.COLON_SEPARATED_HEXTETS;
    }
}
