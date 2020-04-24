package com.ionos.network.commons;

/**
 * The IP version used in {@link IP}.
 *
 * @author Stephan Fuhrmann
 * @version 2.0
 **/
public enum IPVersion {

    /** The IP version 4. */
    IPv4(4),

    /** The IP version 6. */
    IPv6(16);

    /** The address length in bytes. */
    private final int addressBytes;

    /** The address length in bits. */
    private final int addressBits;

    /**
     * Constructs new enumeration type.
     *
     * @param inAddressLength the address length of {@code this} IP version
     */
    IPVersion(final int inAddressLength) {
        this.addressBytes = inAddressLength;
        this.addressBits = inAddressLength * BitsAndBytes.BITS_PER_BYTE;
    }

    /**
     * Returns the bit count of an IP address of {@code this} version.
     *
     * @return the bit count of an IP address of {@code this} version.
     */
    public int getAddressBits() {
        return addressBits;
    }

    /**
     * Returns the byte length of an IP address of {@code this} version.
     *
     * @return the byte length of an IP address of {@code this} version.
     */
    public final int getAddressBytes() {
        return addressBytes;
    }

    /**
     * Returns the lowest possible IP address of {@code this} version.
     *
     * @see #getMaximumAddress()
     * @return the lowest possible IP address of {@code this} version
     */
    public IP getMinimumAddress() {
        switch (this) {
            case IPv4:
            return IP.IPV4_ALL_ZERO;
        case IPv6:
            return IP.IPV6_ALL_ZERO;
        default:
            throw new IllegalStateException();
        }
    }

    /**
     * Returns the largest possible IP address of {@code this} version.
     *
     * @see #getMinimumAddress()
     * @return the largest possible IP address of {@code this} version.
     */
    public IP getMaximumAddress() {
        switch (this) {
        case IPv4:
            return IP.IPV4_ALL_ONE;
        case IPv6:
            return IP.IPV6_ALL_ONE;
        default:
            throw new IllegalStateException();
        }
    }
}
