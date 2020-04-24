package com.ionos.network.commons;

import java.io.Serializable;
import java.util.Arrays;

import static com.ionos.network.commons.BitsAndBytes.BYTE_MASK;

/**
 * Abstract address is the base class that contains the bytes of the address.
 * @author Stephan Fuhrmann
 * @version 2.0
 * */
abstract class AbstractAddress implements Comparable<AbstractAddress>,
        Address, Serializable {

    /** The bytes representing the address.
     * */
    protected final byte[] address;

    /**
     * Creates a new address from the address bytes.
     *
     * @param inAddress an address in network byte order.
     */
    AbstractAddress(final byte[] inAddress) {
        this.address = Arrays.copyOf(inAddress, inAddress.length);
    }

    /** Returns a copy of the address bytes.
     * @return a new copy of the address bytes.
     * */
    public final byte[] getBytes() {
        return Arrays.copyOf(address, address.length);
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (o instanceof AbstractAddress) {
            final AbstractAddress other = (AbstractAddress) o;
            return Arrays.equals(address, other.address);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(address);
    }

    /**
     * Compares the address by comparing the address bytes.
     *
     * @return a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.
     */
    @Override
    public int compareTo(final AbstractAddress other) {
        if (other.address.length != address.length) {
            if (address.length < other.address.length) {
                return -1;
            } else {
                return 1;
            }
        }
        for (int i = 0; i < address.length; i++) {
            int v1 = address[i] & BYTE_MASK;
            int v2 = other.address[i] & BYTE_MASK;
            if (v1 < v2) {
                return -1;
            } else if (v1 > v2) {
                return 1;
            }
        }
        return 0;
    }
}
