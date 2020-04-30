package com.ionos.network.commons.address;


import java.util.Objects;
import static com.ionos.network.commons.address.BitsAndBytes.BITS_PER_BYTE;

/**
 * Utility class for converting a MAC address into an IPv6 Address using the
 * EUI-64 / SLAAC conversion.
 *
 * @see <a href="https://bit.ly/2yHzt5u">
 *     Converting 48-Bit MAC Addresses to IPv6 Modified EUI-64 Identifiers</a>
 * @author Stephan Fuhrmann
 *
 **/
public final class EUI64 {

    /** Prefix for link-local IPv6 addresses, that are only valid within
     * closed network segments. */
    private static final IPv6 LINK_LOCAL = IPParsers.IPV6.parse("FE80::");

    /** Private constructor preventing instantiation. */
    private EUI64() {
    }

    /**
     * Converts the given MAC into a link local address using EUI-64
     * conversion and IP prefix {@link #LINK_LOCAL}.
     *
     * @param mac the MAC address to be taken.
     * @return a new IPv6 Address calculated from the MAC address
     * using EUI-64 mechanism
     * @throws NullPointerException     if one of the parameters is null
     * @throws IllegalArgumentException if the IP address is not IPv6
     */
    public static IPv6 convertMac(final MAC mac) {
        return convertMac(mac, LINK_LOCAL);
    }

    /** Length of the network prefix. */
    private static final int PREFIX_LENGTH = 8;

    /** Offset of the OUI part in the MAC address. */
    private static final int MAC_OUI_OFFSET = 0;
    /** Offset of the NIC part in the MAC address. */
    private static final int MAC_NIC_OFFSET = 3;
    /** Length of the OUI part in the MAC address. */
    private static final int MAC_OUI_LENGTH = 3;
    /** Length of the NIC part in the MAC address. */
    private static final int MAC_NIC_LENGTH = 3;

    /** Offset of the prefix in the EUI address.
     * @see #PREFIX_LENGTH
     * */
    private static final int EUI_PREFIX_OFFSET = 0;

    /** Offset of the OUI MAC part in the EUI address.
     * @see #MAC_OUI_LENGTH
     * */
    private static final int EUI_OUI_OFFSET = PREFIX_LENGTH;

    /** Offset of the special word in the EUI address.
     * */
    private static final int EUI_SPECIAL_OFFSET =
            EUI_OUI_OFFSET + MAC_OUI_LENGTH;

    /** Offset of the NIC MAC part in the EUI address.
     * @see #MAC_NIC_LENGTH
     * */
    private static final int EUI_NIC_OFFSET =
            EUI_OUI_OFFSET + MAC_OUI_LENGTH + 2;

    /** Special word in EUI 64. */
    private static final int EUI_SPECIAL = 0xfffe;

    /**
     * Converts the given MAC into a link local address using EUI-64 conversion.
     *
     * @param mac    the MAC address to be taken into the generated address
     * @param prefix the IPv6 network prefix to use as a prefix for the
     *               address generation
     * @return a new IPv6 Address calculated from the MAC address
     * using EUI-64 mechanism
     * @throws NullPointerException     if one of the parameters is null
     * @throws IllegalArgumentException if the IP address is not IPv6
     */
    public static IPv6 convertMac(final MAC mac, final IPv6 prefix) {
        Objects.requireNonNull(mac, "mac may not be null");
        Objects.requireNonNull(prefix, "prefix may not be null");
        if (prefix.getIPVersion() != IPVersion.IPV6) {
            throw new IllegalArgumentException(
                    "prefix must be an IPv6 address");
        }

        final byte[] result = new byte[IPVersion.IPV6.getAddressBytes()];

        // first copy the prefix (bytes 0..7)
        final byte[] network = prefix.address;
        System.arraycopy(network, 0, result, EUI_PREFIX_OFFSET, PREFIX_LENGTH);

        // copy MAC bytes
        final byte[] macBytes = mac.address;

        // then copy the suffix (bytes 8..15)
        System.arraycopy(macBytes,
                MAC_OUI_OFFSET,
                result,
                EUI_OUI_OFFSET,
                MAC_OUI_LENGTH);
        System.arraycopy(macBytes,
                MAC_NIC_OFFSET,
                result,
                EUI_NIC_OFFSET,
                MAC_NIC_LENGTH);

        // fill in fffe into bytes 3 and 4
        result[EUI_SPECIAL_OFFSET] =
                (byte) (EUI_SPECIAL >>> BITS_PER_BYTE);
        result[EUI_SPECIAL_OFFSET + 1] = (byte) EUI_SPECIAL;

        // toggle bit 7
        result[EUI_OUI_OFFSET] ^= 2;

        return new IPv6(result);
    }
}
