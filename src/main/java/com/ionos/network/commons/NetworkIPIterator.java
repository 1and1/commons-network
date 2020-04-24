package com.ionos.network.commons;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Iterator over the IP addresses of {@code this} network.
 * Try not to use this for performance issues.
 */
class NetworkIPIterator implements Iterator<IP> {

    /** The network to iterate in. */
    private final Network network;
    /** The current IP. */
    private IP current;

    /** Constructs a new network iterator starting at the
     * start address.
     * @param inNetwork the network to create an iterator for.
     * */
    NetworkIPIterator(final Network inNetwork) {
        this.network = inNetwork;
        current = inNetwork.getAddress();
    }

    @Override
    public boolean hasNext() {
        return current.compareTo(network.getAddressEnd()) <= 0;
    }

    @Override
    public IP next() {
        final IP result = current;

        if (!hasNext()) {
            throw new NoSuchElementException("Already at end of network");
        }

        current = current.add(1);
        return result;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("method not supported");
    }
}
