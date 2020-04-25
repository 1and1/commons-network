package com.ionos.network.commons.address;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static com.ionos.network.commons.address.BitsAndBytes.BYTE_MASK;
import static com.ionos.network.commons.address.BitsAndBytes.BITS_PER_BYTE;

/**
 * IP in either the IPv4 or IPv6 protocol.
 * Stores the address bytes and has methods to calculate with IPs.
 * The IP class has some methods for calculation with IP numbers, for example
 * {@link #add(byte[])}, {@link #add(long)},
 * {@link AbstractAddress#compareTo(AbstractAddress)}.
 * <br>
 * Objects of the IP class are immutable!
 *
 * @see <a href="http://tools.ietf.org/html/rfc4291#section-2.2">RFC 4291,
 * Textual IPv6 representations</a>
 * @see java.net.InetAddress
 * @see IPVersion
 * @author Stephan Fuhrmann
 *
 **/
public final class IP extends AbstractAddress implements Address, Serializable {

    /** The IPv4 address 0.0.0.0. */
    public static final IP IPV4_ALL_ZERO =
            new IP(BitsAndBytes.newArrayOf(4, (byte)0));

    /** The IPv4 address 255.255.255.255. */
    public static final IP IPV4_ALL_ONE =
            new IP(BitsAndBytes.newArrayOf(4, (byte)255));

    /** The IPv6 address 0::0. */
    public static final IP IPV6_ALL_ZERO =
            new IP(BitsAndBytes.newArrayOf(16, (byte)0));

    /** The IPv6 address ffff:...:ffff. */
    public static final IP IPV6_ALL_ONE =
            new IP(BitsAndBytes.newArrayOf(16, (byte)255));

    /** The version number of this class. */
    private static final long serialVersionUID = 5334743380391791729L;

    /**
     * The number of bytes in an IPv4 address.
     * This is here because {@link IPVersion} initializes static IPs.
     */
    private static final int IPV4_BYTES = 4;

    /** The number of bytes in an IPv6 address. */
    private static final int IPV6_BYTES = 16;

    /**
     * Creates a new IP address from the address bytes.
     *
     * @param inAddress a 4 or 16 byte address in network byte order.
     */
    public IP(final byte[] inAddress) {
        super(inAddress);
        if (inAddress.length != IPV4_BYTES && inAddress.length != IPV6_BYTES) {
            throw new IllegalArgumentException(
                    "IP addresses must be of 4 or 16 bytes long. "
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
    public IP(final String inAddress) {
        super(IPParser.INSTANCE.parseAsBytes(inAddress));
    }

    /**
     * Gets the IP version used in this IP address.
     * @return the IP version of this address.
     */
    public IPVersion getIPVersion() {
        if (address.length == IPVersion.IPV4.getAddressBytes()) {
            return IPVersion.IPV4;
        }
        if (address.length == IPVersion.IPV6.getAddressBytes()) {
            return IPVersion.IPV6;
        }
        throw new IllegalStateException("Address length of "
                + address.length + " bytes is illegal");
    }

    /**
     * Invert the address. This will flip every bit from 1 to 0 and vice
     * versa.
     *
     * @return the inverted IP address. Example: For the input "255.255.0.0"
     * will return "0.0.255.255".
     */
    public IP invert() {
        final byte[] inverted = new byte[address.length];
        for (int i = 0; i < inverted.length; i++) {
            inverted[i] = (byte) ~address[i];
        }
        return new IP(inverted);
    }

    /**
     * Add the given values to the base address, creating a new {@link IP}
     * address.
     *
     * @param offset an array of values shorter or equal to the size of the
     *               address. Will get added component by component
     *               to this address, starting at the <em>end of the array</em>.
     * @return the new IP built from the base IP and the offset given in the
     * values. Note that the addition uses
     * unsigned bytes. Note: The addition will only take place in
     * offset.length bytes!
     * Example: <code>new IP("192.168.1.1").add(new byte[] {1})
     * .equals(new IP("192.168.1.2"))</code>
     * @throws IllegalArgumentException if the offset array is larger than
     * the address itself.
     */
    public IP add(final byte[] offset) {
        final IP result = new IP(address);

        if (offset.length > address.length) {
            throw new IllegalArgumentException(
                    "Offset array is larger than the address itself");
        }

        int carry = 0;
        int addressOffset = 0;
        for (int i = 0; i < offset.length && i < address.length; i++) {
            // from lsb to msb
            int offsetVal = BYTE_MASK & offset[offset.length - 1 - i];
            addressOffset = address.length - 1 - i;
            // from lsb
            int addressVal = BYTE_MASK & address[addressOffset];

            int sum = offsetVal + addressVal + carry;
            carry = sum >> BITS_PER_BYTE;
            result.address[addressOffset] = (byte) sum;
        }

        while (carry != 0 && addressOffset > 0) {
            addressOffset--;
            int sum = (BYTE_MASK & result.address[addressOffset]) + carry;
            carry = sum >> BITS_PER_BYTE;
            result.address[addressOffset] = (byte) sum;
        }

        return result;
    }

    /**
     * Bitwise AND the address and the mask given in the byte array.
     *
     * @param mask an array of values shorter or equal to the size of the
     *             address.
     *             Will get ANDed component by component
     *             to this address, starting at the <em>end of the array</em>.
     * @return the new IP built from the base IP and the offset
     * given in the values.
     * Example: <code>new IP("192.168.1.1").and(new byte[] {255,0})
     * .equals(new IP("192.168.1.0"))</code>
     */
    public IP and(final byte[] mask) {
        final IP result = new IP(address);

        for (int i = 0; i < mask.length; i++) {
            result.address[address.length - 1 - i] &= mask[mask.length - 1 - i];
        }

        return result;
    }

    /**
     * Add the given offset to the base address, creating a new
     * {@link IP} address.
     *
     * @param offset the number of IP addresses to go forward.
     * @return the new IP built from the base IP and the offset given.
     * Note that the addition uses
     * unsigned bytes.
     * Example: <code>new IP("192.168.1.1").add(1)
     * .equals(new IP("192.168.1.2"))</code>
     */
    public IP add(final long offset) {
        final IP result = new IP(address);
        long myOffset = offset;

        int carry = 0;
        int i = 0;
        while ((myOffset != 0 || carry != 0) && i < address.length) {
            // from LSB
            int offsetValue = BYTE_MASK & (int) myOffset;
            int addressOffset = address.length - 1 - i;
            // from LSB
            int addressValue = BYTE_MASK & address[addressOffset];

            int sum = offsetValue + addressValue + carry;
            carry = sum >> BITS_PER_BYTE;
            result.address[addressOffset] = (byte) sum;

            myOffset >>>= BITS_PER_BYTE;
            i++;
        }

        return result;
    }

    /**
     * Returns the IP representation of the form <code>a.b.c.d</code>
     * or <code>a:b:c:d:e:f:g:h</code>.
     * @see IPFormats#DOTTED_DECIMAL for {@link IPVersion#IPV4}
     * @see IPFormats#COLON_SEPARATED_HEXTETS
     * for {@link IPVersion#IPV6}
     */
    @Override
    public String toString() {
        switch (address.length) {
        case IPV4_BYTES:
            // IPv4 style
            // decimals with dot separators
            return IPFormats
                    .DOTTED_DECIMAL.format(this);
        case IPV6_BYTES:
            return IPFormats
                    .COLON_SEPARATED_HEXTETS
                    .format(this);
        default:
            throw new IllegalStateException();
        }
    }

    /**
     * Convert the IP address to an {@link InetAddress}.
     * @return the corresponding InetAddress object
     * created using {@linkplain InetAddress#getByAddress(byte[])}.
     */
    public InetAddress toInetAddress() {
        try {
            return InetAddress.getByAddress(getBytes());
        } catch (UnknownHostException e) {
            // only if the size of the address is illegal
            // which is prevented in the constructors
            throw new IllegalStateException(e);
        }
    }
}
