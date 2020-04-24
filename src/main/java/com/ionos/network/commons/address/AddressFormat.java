package com.ionos.network.commons.address;

import java.io.IOException;

/** Formats addresses to human readable form.
 * @param <T> the concrete type of address that is being passed
 *           to {@link #format(Address)}.
 * @author Stephan Fuhrmann
 *
 **/
public interface AddressFormat<T extends Address> {

    /** Formats an address in the notation that this AddressFormat is for.
     * @param address the address to format.
     * @return the String representation of the address parameter.
     * @throws NullPointerException if the address was {@code null}.
     * */
    String format(T address);

    /** Formats an address in the notation that this AddressFormat is for into
     * the given Appendable.
     * @param address the address to format.
     * @param toAppendTo the appendable subclass instance to append the
     *                   formatted address to.
     * @param <A> a subclass of Appendable to add the data to.
     * @return the appendable subclass instance {@code toAppendTo} with the
     * new address text added.
     * @throws IOException if there is an IO error while outputting
     * characters to the Appendable.
     * @throws NullPointerException if the address was {@code null}.
     * */
    <A extends Appendable> A format(T address, A toAppendTo) throws IOException;
}
