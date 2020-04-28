/** Classes that contain models for IP network related
 * types like
 * {@link com.ionos.network.commons.address.IP},
 * {@link com.ionos.network.commons.address.Network} and
 * {@link com.ionos.network.commons.address.MAC}.
 *
 * There are immutable classes containing addresses:
 * <ul>
 *     <li>{@link com.ionos.network.commons.address.IP}:
 *     An IP of a certain {@link com.ionos.network.commons.address.IPVersion}.</li>
 *     <li>{@link com.ionos.network.commons.address.Network}:
 *     An IP network containing network addresses.</li>
 *     <li>{@link com.ionos.network.commons.address.MAC}:
 *     A Media-Access-Control address.</li>
 * </ul>
 *
 * There are classes for formatting the addresses into multiple string representations:
 * <ul>
 *     <li>{@link com.ionos.network.commons.address.IPFormats}:
 *     Multiple formatters for IP addresses.</li>
 *     <li>{@link com.ionos.network.commons.address.MACFormats}:
 *     Multiple formatters for MAC addresses.</li>
 * </ul>
 * All hexadecimal characters are lower-case in this library.
 *
 * There are classes for parsing Strings into addresses:
 * <ul>
 *     <li>{@link com.ionos.network.commons.address.IPParser}:
 *     Parses a String into an {@linkplain com.ionos.network.commons.address.IP}.</li>
 *     <li>{@link com.ionos.network.commons.address.MACParsers}:
 *     Parser implementations for parsing Strings into a {@linkplain com.ionos.network.commons.address.MAC}.</li>
 * </ul>
 *
 * There's a {@link com.ionos.network.commons.address.EUI64} conversion class
 * that can convert MAC addresses
 * into the corresponding IPv6 addresses using the EUI-64 / SLAAC mechanism.
 * */
package com.ionos.network.commons.address;
