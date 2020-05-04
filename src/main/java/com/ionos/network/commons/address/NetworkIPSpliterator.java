package com.ionos.network.commons.address;

import java.util.Comparator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

/** A spliterator that returns the {@linkplain IP IPs}
 * in a {@linkplain Network}.
 * @param <T> the type of IP address to return.
 * */
final class NetworkIPSpliterator<T extends IP> implements Spliterator<T> {

    /** The number of bits a {@linkplain Long} has. */
    private static final int BIT_SIZE_LONG = 64;

    /** The network we're returning IPs from. */
    private Network<T> network;

    /** The next IP that is going to be returned. */
    private T currentIP;

    NetworkIPSpliterator(final Network<T> inNetwork) {
        this.network = inNetwork;
        this.currentIP = inNetwork.getAddress();
    }

    @Override
    public boolean tryAdvance(final Consumer<? super T> consumer) {
        if (currentIP.compareTo(network.getBroadcast()) <= 0) {
            consumer.accept(currentIP);
            currentIP = (T) currentIP.add(1);
            return currentIP.compareTo(network.getBroadcast()) <= 0;
        }
        return false;
    }

    @Override
    public Spliterator<T> trySplit() {
        if (network.getPrefix()
                < network.getIPVersion().getAddressBits()) {
            List<Network<T>> split =
                    network.split(network.getPrefix() + 1);

            if (split.size() != 2) {
                throw new IllegalStateException();
            }

            Network left = split.get(0);
            Network right = split.get(1);
            // currentIP already in second half
            if (right.contains(currentIP)) {
                return null;
            }

            network = left;
            return new NetworkIPSpliterator(right);
        }
        return null;
    }

    @Override
    public long estimateSize() {
        int networkBits = network.getIPVersion().getAddressBits()
                - network.getPrefix();
        if (networkBits < BIT_SIZE_LONG) {
            return 1 << networkBits;
        } else {
            return Long.MAX_VALUE;
        }
    }

    @Override
    public int characteristics() {
        return ORDERED | DISTINCT | SORTED | NONNULL
                | IMMUTABLE | SIZED | SUBSIZED;
    }

    @Override
    public Comparator<? super IP> getComparator() {
        return AddressComparators.UNSIGNED_BYTE_COMPARATOR;
    }
}
