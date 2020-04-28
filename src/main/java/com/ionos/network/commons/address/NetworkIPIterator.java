package com.ionos.network.commons.address;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Iterator over the IP addresses of {@code this} network.
 * Try not to use this for performance issues.
 */
final class NetworkIPIterator<T extends IP> implements Iterator<IP> {

    /** The network to iterate in. */
    private final Network<T> network;
    /** The current IP. */
    private T current;

    /** Constructs a new network iterator starting at the
     * start address.
     * @param inNetwork the network to create an iterator for.
     * */
    NetworkIPIterator(final Network<T> inNetwork) {
        this.network = inNetwork;
        current = inNetwork.getAddress();
    }

    @Override
    public boolean hasNext() {
        return current.compareTo(network.getAddressEnd()) <= 0;
    }

    @Override
    public T next() {
        final T result = current;

        if (!hasNext()) {
            throw new NoSuchElementException("Already at end of network");
        }

        current = (T)current.add(1);
        return result;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("method not supported");
    }
}
