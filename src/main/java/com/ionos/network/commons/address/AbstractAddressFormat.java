package com.ionos.network.commons.address;

import java.io.IOException;

/** Adds a default format method to simplify implementation of the interface.
 * @param <T> the address class this class offers format functionality for.
 * @see #format(Address)
 * @author Stephan Fuhrmann
 *
 **/
abstract class AbstractAddressFormat<T extends Address>
        implements AddressFormat<T> {

    @Override
    public String format(final T address) {
        try {
            return format(address, new StringBuilder()).toString();
        } catch (IOException e) {
            // this should not happen in the StringBuilder
            throw new IllegalStateException("This should not happen in "
                    + "StringBuilder!", e);
        }
    }
}
