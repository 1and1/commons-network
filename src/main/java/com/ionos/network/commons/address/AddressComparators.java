package com.ionos.network.commons.address;

import java.util.Comparator;

import static com.ionos.network.commons.address.BitsAndBytes.BYTE_MASK;

/** Provides address {@linkplain Comparator} instances. */
public final class AddressComparators {

    private AddressComparators() {
        // no instance
    }

    /**
     * Compares the addresses component by component. Each component is
     * regarded as being an <em>unsigned</em> byte.
     */
    public static final Comparator<Address> UNSIGNED_BYTE_COMPARATOR =
            (o1, o2) -> {
                byte[] o1Address = AbstractAddress.getBytesForReading(o1);
                byte[] o2Address = AbstractAddress.getBytesForReading(o2);

                for (int i = 0; i < o1Address.length; i++) {
                    int v1 = o1Address[i] & BYTE_MASK;
                    int v2 = o2Address[i] & BYTE_MASK;
                    if (v1 == v2) {
                        continue;
                    }
                    if (v1 < v2) {
                        return -1;
                    } else {
                        return 1;
                    }
                }
                return 0;
            };
}
