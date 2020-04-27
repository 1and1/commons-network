package com.ionos.network.commons.address;

import java.util.Comparator;

import static com.ionos.network.commons.address.BitsAndBytes.BYTE_MASK;

/** Contains address {@linkplain Comparator} instances. */
public class AddressComparators {

    private AddressComparators() {
        // no instance
    }

    /**
     * Compares the addresses component by component. Each component is
     * regarded as being an <em>unsigned</em> byte.
     */
    public static final Comparator<Address> UNSIGNED_BYTE_COMPARATOR =
            (o1, o2) -> {
                byte[] o1Address;
                byte[] o2Address;

                // don't call getBytes() if not necessary.
                // getBytes() creates a copy of the array.
                if (o1 instanceof AbstractAddress) {
                    o1Address = ((AbstractAddress)o1).address;
                } else {
                    o1Address = o1.getBytes();
                }

                if (o2 instanceof AbstractAddress) {
                    o2Address = ((AbstractAddress)o2).address;
                } else {
                    o2Address = o2.getBytes();
                }

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
