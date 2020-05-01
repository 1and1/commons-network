/** Classes that contain models for IP network related
 * types like
 * {@link com.ionos.network.commons.address.IP},
 * {@link com.ionos.network.commons.address.Network} and
 * {@link com.ionos.network.commons.address.MAC}.
 *
 * <h2>Address classes</h2>
 *
 * There are immutable classes containing addresses:
 * <ul>
 *     <li>{@link com.ionos.network.commons.address.IP}:
 *     An IP of a certain
 *     {@link com.ionos.network.commons.address.IPVersion}.
 *     There are implementations for type safe distinguishing between
 *     {@link com.ionos.network.commons.address.IPv4} and
 *     {@link com.ionos.network.commons.address.IPv6}.
 *     </li>
 *     <li>{@link com.ionos.network.commons.address.Network}:
 *     An IP network containing network addresses.</li>
 *     <li>{@link com.ionos.network.commons.address.MAC}:
 *     A Media-Access-Control address.</li>
 * </ul>
 * <h2>Formatting addresses</h2>
 * There are classes for formatting the addresses into multiple
 * string representations:
 * <ul>
 *     <li>{@link com.ionos.network.commons.address.IPFormats}:
 *     Multiple formatters for IP addresses.</li>
 *     <li>{@link com.ionos.network.commons.address.MACFormats}:
 *     Multiple formatters for MAC addresses.</li>
 * </ul>
 * All hexadecimal characters are lower-case in this library.
 *
 * <h2>Parsing textual address representations</h2>
 * There are classes for parsing Strings into addresses:
 * <ul>
 *     <li>{@link com.ionos.network.commons.address.IPParsers}:
 *     Parser implementations for converting Strings into an
 *     {@linkplain com.ionos.network.commons.address.IP}
 *     address.</li>
 *     <li>{@link com.ionos.network.commons.address.MACParsers}:
 *     Parser implementations for converting Strings into a
 *     {@linkplain com.ionos.network.commons.address.MAC}
 *     address.</li>
 * </ul>
 *
 * <h3>Miscellaneous</h3>
 * There's a {@link com.ionos.network.commons.address.EUI64} conversion class
 * that can convert MAC addresses
 * into the corresponding IPv6 addresses using the EUI-64 / SLAAC mechanism.
 * */
package com.ionos.network.commons.address;
