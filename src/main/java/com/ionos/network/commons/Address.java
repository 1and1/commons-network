package com.ionos.network.commons;

import java.util.Arrays;
import java.util.Comparator;

import static com.ionos.network.commons.BitsAndBytes.BYTE_MASK;

/** A machine- or node-address of some kind.
 * @author Stephan Fuhrmann
 * @version 2.0
 * */
public interface Address {

    /**
     * Get a copy of the bytes representing this address}.
     *
     * @return a copy of the bytes in this address in network byte order
     * or big endian byte order.
     */
    byte[] getBytes();


    /**
     * Compares the addresses component by component. Each component is
     * regarded as being an <em>unsigned</em> byte! This is in contrast
     * to {@link Comparable#compareTo(Object)}.
     */
    Comparator<Address> COMPONENT_COMPARATOR =
            (o1, o2) -> {
                byte[] o1Address = o1.getBytes();
                byte[] o2Address = o2.getBytes();

                if (Arrays.equals(o1Address, o2Address)) {
                    return 0;
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
