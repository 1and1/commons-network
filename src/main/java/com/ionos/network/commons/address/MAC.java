package com.ionos.network.commons.address;

import java.io.Serializable;

/**
 * The Media Access Control address of a network interface.
 * Objects of the MAC class are immutable!
 *
 * @see MACFormats
 * @see MACParsers
 * @author Stephan Fuhrmann
 **/
public final class MAC extends AbstractAddress
        implements Address, Serializable {
    /** The version number of this class. */
    private static final long serialVersionUID = 7743380391792341L;

    /** The number of bytes in a MAC address. May change! */
    protected static final int MAC_LENGTH = 6;

    /**
     * Creates a MAC by parsing the given String.
     *
     * @param mac the String containing the MAC. See {@link MACParsers#DEFAULT}.
     * @throws java.lang.IllegalArgumentException if the specified MAC could
     * not be parsed
     */
    public MAC(final String mac) {
        super(MACParsers.DEFAULT.parseAsBytes(mac), MAC_LENGTH);
    }

    /**
     * Creates a MAC from the given byte array.
     *
     * @param mac the MAC data
     * @throws java.lang.IllegalArgumentException if the specified MAC has an
     * illegal length
     */
    public MAC(final byte[] mac) {
        super(mac, MAC_LENGTH);
    }

    /**
     * Returns a single byte hex representation of {@code this} MAC.
     * <br>
     * <i>Example:</i> {@code 00:ac:ac:dd:00:12}.
     * @see MACFormats#COLON_SEPARATED_HEX_FORMAT
     * @return single byte hex representation of {@code this} MAC
     */
    @Override
    public String toString() {
        return MACFormats.COLON_SEPARATED_HEX_FORMAT.format(this);
    }
}
