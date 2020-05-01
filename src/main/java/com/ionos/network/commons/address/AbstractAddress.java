package com.ionos.network.commons.address;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

/**
 * Abstract address is the base class that contains the bytes of the address.
 * @author Stephan Fuhrmann
 *
 * */
abstract class AbstractAddress implements Comparable<AbstractAddress>,
        Address, Serializable {

    /** The version number of this class. */
    static final long serialVersionUID = 41414698085765672L;

    /** The bytes representing the address.
     * */
    protected byte[] address;

    /**
     * Creates a new address from the address bytes.
     *
     * @param inAddress an address in network byte order.
     * @throws NullPointerException if the input array reference is null.
     */
    AbstractAddress(final byte[] inAddress) {
        Objects.requireNonNull(inAddress, "address byte array is null");
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
        return AddressComparators.UNSIGNED_BYTE_COMPARATOR.compare(this, other);
    }

    /** Custom serialization for writing an address.
     * @param s the stream to write the object to.
     * @throws IOException if there's a problem in writing to the stream.
     * */
    private void writeObject(final ObjectOutputStream s) throws IOException {
        s.writeInt(address.length);
        s.write(address);
    }

    /** Custom deserialization for reading an address.
     * @param s the stream to read the object from.
     * @throws IOException if there's a problem in reading from the stream.
     * */
    private void readObject(final ObjectInputStream s) throws IOException {
        int length = s.readInt();
        byte[] data = new byte[length];
        s.readFully(data);
        address = data;
    }

    /** A performance optimized variant for
     * {@linkplain #getBytes()} that does not
     * return a copy, but the original array.
     * This is only allowed for reading the data.
     * @param address the address that can possibly
     *                be an instance of {@linkplain AbstractAddress}
     *                or another implementation.
     * @return the byte array for reading.
     * */
    static byte[] getBytesForReading(final Address address) {
        if (address instanceof AbstractAddress) {
            return ((AbstractAddress) address).address;
        } else {
            return address.getBytes();
        }
    }
}
