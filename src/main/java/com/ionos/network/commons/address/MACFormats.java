package com.ionos.network.commons.address;

/**
 * Formats a MAC address to text form in multiple notations.
 * @see MAC
 * @see AddressFormat
 * @author Stephan Fuhrmann
 * */
public final class MACFormats {


    /**
     * Private constructor. No instance allowed.
     */
    private MACFormats() {
        // no instance allowed
    }

    /**
     * Returns a single byte hex representation of the MAC.
     * <br>
     * <i>Example:</i> {@code 00:ac:ac:dd:00:12}.
     * @see MACParsers#COLON_SEPARATED_HEX_FORMAT
     */
    public static final AddressFormat<MAC> COLON_SEPARATED_HEX_FORMAT =
            new HexadecimalAddressFormat<MAC>(':', 2);

    /**
     * Word hex representation of the MAC in CISCO style.
     * <br>
     * <i>Example:</i>{@code 00ac.acdd.0012}.
     */
    public static final AddressFormat<MAC> CISCO_CUSTOM_FORMAT =
            new HexadecimalAddressFormat<MAC>('.', 4);
}
