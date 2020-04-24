package com.ionos.network.commons;

/** Parses addresses to machine readable form.
 * @param <T> the address type that is being returned by the
 * {@link #parse(String)} method.
 * @author Stephan Fuhrmann
 *
 * */
public interface AddressParser<T extends Address> {

    /** Parses an address from a text representation.
     * @param address the text representation of the address to parse.
     * @return the parsed address as an object.
     * @throws NullPointerException if the address was {@code null}.
     * @throws IllegalArgumentException if the input address was malformed.
     * */
    T parse(String address);

    /** Parses an address from a text representation.
     * @param address the text representation of the address to parse.
     * @return the parsed address as a byte array in network byte order.
     * @throws NullPointerException if the address was {@code null}.
     * @throws IllegalArgumentException if the input address was malformed.
     * */
    byte[] parseAsBytes(String address);
}
