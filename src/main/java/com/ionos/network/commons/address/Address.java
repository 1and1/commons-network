package com.ionos.network.commons.address;

/** A machine- or node-address of some kind.
 * @see AddressComparators
 * @see AddressFormat
 * @see AddressParser
 * @author Stephan Fuhrmann
 * */
public interface Address {

    /**
     * Get a copy of the bytes representing this address.
     *
     * @return a copy of the bytes in this address in network byte order
     * or big endian byte order.
     */
    byte[] getBytes();
}
