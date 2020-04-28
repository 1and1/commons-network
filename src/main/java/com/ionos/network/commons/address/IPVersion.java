package com.ionos.network.commons.address;

/**
 * The IP version used in {@link IP}.
 *
 * @author Stephan Fuhrmann
 *
 **/
public enum IPVersion {

    /** The IP version 4. */
    IPV4(4),

    /** The IP version 6. */
    IPV6(16);

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
}
