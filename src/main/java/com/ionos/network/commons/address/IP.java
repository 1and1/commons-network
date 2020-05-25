package com.ionos.network.commons.address;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static com.ionos.network.commons.address.BitsAndBytes.BYTE_MASK;
import static com.ionos.network.commons.address.BitsAndBytes.BITS_PER_BYTE;

/**
 * An abstract IP address.
 * Stores the address bytes and has methods to calculate with IPs.
 * The IP class has some methods for calculation with IP numbers, for example
 * {@link #add(byte[])}, {@link #add(long)},
 * {@link AbstractAddress#compareTo(AbstractAddress)}.
 * <br>
 * Objects of the IP class are immutable!
 * @param <T> the concrete IP subclass that is used.
 * @see java.net.InetAddress
 * @see IPVersion
 * @see IPFormats
 * @see IPParsers
 * @author Stephan Fuhrmann
 *
 **/
public abstract class IP<T extends IP<T>>
        extends AbstractAddress
        implements Address, Serializable {
    /** The version number of this class. */
    private static final long serialVersionUID = 5338854380391791729L;

    /**
     * Creates a new IP address from the address bytes.
     *
     * @param inAddress a 4 or 16 byte address in network byte order.
     * @param inLength the number of expected bytes.
     */
    protected IP(final byte[] inAddress, final int inLength) {
        super(inAddress, inLength);
    }

    /**
     * Gets the IP version used in the given IP address class.
     * @param ipClass the class to determine the IPversion for.
     * @return the IP version of this address.
     */
    public static <U extends IP<U>> IPVersion getIPVersion(Class<U> ipClass) {
        IPVersion result;
        if (IPv4.class.isAssignableFrom(ipClass)) {
            result = IPVersion.IPV4;
        } else if (IPv6.class.isAssignableFrom(ipClass)) {
            result = IPVersion.IPV6;
        } else {
            throw new IllegalArgumentException("Class not suitable for IP addresses: " + ipClass.getCanonicalName());
        }
        return result;
    }


    /**
     * Gets the IP version used in this IP address.
     * @return the IP version of this address.
     */
    public abstract IPVersion getIPVersion();

    /**
     * Allocates a new instance of this class.
     * @param address the address data to initialize the constructor with.
     *                Will get copied.
     * @return a new IP address instance of the subclass with a copy
     * of the input
     * address.
     */
    protected abstract T newInstance(byte[] address);

    /** Get the default address format for this class.
     * @return the IP address format that is used for
     * the {@linkplain #toString()} method.
     * */
    protected abstract AddressFormat<T> defaultAddressFormat();

    /**
     * Invert the address. This will flip every bit from 1 to 0 and vice
     * versa.
     *
     * @return the inverted IP address.
     * Example: For the input {@code 255.255.0.0}
     * will return {@code 0.0.255.255}.
     */
    public T invert() {
        final byte[] inverted = new byte[address.length];
        for (int i = 0; i < inverted.length; i++) {
            inverted[i] = (byte) ~address[i];
        }
        return newInstance(inverted);
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
    public T add(final byte[] offset) {
        final T result = newInstance(address);

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
     * Example: {@code new IPv4("192.168.1.1").and(new byte[] {255,0})
     * .equals(new IPv4("192.168.1.0"))}
     */
    public T and(final byte[] mask) {
        final T result = newInstance(address);

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
    public T add(final long offset) {
        final T result = newInstance(address);
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
     * @see IPFormats#DOTTED_DECIMAL
     * @see IPFormats#COLON_SEPARATED_HEXTETS
     */
    @Override
    public String toString() {
        return defaultAddressFormat().format((T) this);
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
