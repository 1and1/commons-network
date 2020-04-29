package com.ionos.network.commons.address;

import java.io.Serializable;

/**
 * IP for the IPv4 protocol.
 * Stores the address bytes and has methods to calculate with IPs.
 * <br>
 * Objects of the IPv4 class are immutable!
 *
 * @see java.net.Inet4Address
 * @see IPVersion
 * @see IPFormats
 * @see IPParsers
 * @author Stephan Fuhrmann
 *
 **/
public final class IPv4 extends IP<IPv4> implements Address, Serializable {
    /** The version number of this class. */
    private static final long serialVersionUID = 1212884661391791729L;

    /** The IPv4 address {@code 0.0.0.0}. */
    public static final IPv4 IPV4_ALL_ZERO =
            new IPv4(BitsAndBytes.newArrayOf(IPVersion.IPV4.getAddressBytes(), (byte)0));

    /** The IPv4 address {@code 255.255.255.255}. */
    public static final IPv4 IPV4_ALL_ONE =
            new IPv4(BitsAndBytes.newArrayOf(IPVersion.IPV4.getAddressBytes(), (byte)255));

    /**
     * Creates a new IP address from the address bytes.
     *
     * @param inAddress a 4 byte address in network byte order.
     */
    protected IPv4(final byte[] inAddress) {
        super(inAddress);
        if (inAddress.length !=  IPVersion.IPV4.getAddressBytes()) {
            throw new IllegalArgumentException(
                    "IPv4 addresses must be 4 bytes long. "
                            + "Your address has " + inAddress.length
                            + " bytes.");
        }
    }

    /**
     * Creates a new IP address from the address bytes.
     *
     * @param inAddress a String containing the IP address in a
     *                  notation supported by {@linkplain IPParsers}.
     * @see IPParsers#DOTTED_DECIMAL
     */
    public IPv4(final String inAddress) {
        this(IPParsers.DOTTED_DECIMAL.parseAsBytes(inAddress));
    }

    /**
     * Gets the IP version used in this IP address.
     * @return the IP version of this address.
     */
    public IPVersion getIPVersion() {
            return IPVersion.IPV4;
    }

    @Override
    protected IPv4 newInstance(byte[] address) {
        return new IPv4(address);
    }

    @Override
    protected AddressFormat<IPv4> defaultAddressFormat() {
        return IPFormats.DOTTED_DECIMAL;
    }
}
