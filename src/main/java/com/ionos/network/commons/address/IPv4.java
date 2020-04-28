package com.ionos.network.commons.address;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static com.ionos.network.commons.address.BitsAndBytes.BITS_PER_BYTE;
import static com.ionos.network.commons.address.BitsAndBytes.BYTE_MASK;

/**
 * IP in the IPv4 protocol.
 * Stores the address bytes and has methods to calculate with IPs.
 * The IP class has some methods for calculation with IP numbers, for example
 * {@link #add(byte[])}, {@link #add(long)},
 * {@link AbstractAddress#compareTo(AbstractAddress)}.
 * <br>
 * Objects of the IPv4 class are immutable!
 *
 * @see <a href="http://tools.ietf.org/html/rfc4291#section-2.2">RFC 4291,
 * Textual IPv6 representations</a>
 * @see java.net.Inet4Address
 * @see IPVersion
 * @author Stephan Fuhrmann
 *
 **/
public final class IPv4 extends IP<IPv4> implements Address, Serializable {
    /** The version number of this class. */
    private static final long serialVersionUID = 1212884661391791729L;

    /** The IPv4 address 0.0.0.0. */
    public static final IPv4 IPV4_ALL_ZERO =
            new IPv4(BitsAndBytes.newArrayOf(IPVersion.IPV4.getAddressBytes(), (byte)0));

    /** The IPv4 address 255.255.255.255. */
    public static final IPv4 IPV4_ALL_ONE =
            new IPv4(BitsAndBytes.newArrayOf(IPVersion.IPV4.getAddressBytes(), (byte)255));

    /**
     * Creates a new IP address from the address bytes.
     *
     * @param inAddress a 4 or 16 byte address in network byte order.
     */
    protected IPv4(final byte[] inAddress) {
        super(inAddress);
        if (inAddress.length !=  IPVersion.IPV4.getAddressBytes()) {
            throw new IllegalArgumentException(
                    "IP addresses must be 4 bytes long. "
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
    public IPv4(final String inAddress) {
        this(IPParser.INSTANCE.parseAsBytes(inAddress));
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
