/** Classes that contain models for IP network related
 * types like
 * {@link com.ionos.network.commons.IP},
 * {@link com.ionos.network.commons.Network} and
 * {@link com.ionos.network.commons.MAC}.
 *
 * There are immutable classes containing addresses:
 * <ul>
 *     <li>{@link com.ionos.network.commons.IP}:
 *     An IP of a certain {@link com.ionos.network.commons.IPVersion}.</li>
 *     <li>{@link com.ionos.network.commons.Network}:
 *     An IP network containing network addresses.</li>
 *     <li>{@link com.ionos.network.commons.MAC}:
 *     A Media-Access-Control address.</li>
 * </ul>
 *
 * There are classes for formatting the addresses into multiple forms:
 * <ul>
 *     <li>{@link com.ionos.network.commons.IPFormats}:
 *     Multiple formatters for IP addresses.</li>
 *     <li>{@link com.ionos.network.commons.MACFormats}:
 *     Multiple formatters for MAC addresses.</li>
 * </ul>
 *
 * There are classes for parsing Strings into addresses:
 * <ul>
 *     <li>{@link com.ionos.network.commons.IPParser}:
 *     Parses a String into an {@linkplain com.ionos.network.commons.IP}.</li>
 *     <li>{@link com.ionos.network.commons.MACParser}:
 *     Parses a String into a {@linkplain com.ionos.network.commons.MAC}.</li>
 * </ul>
 *
 * There's a {@link com.ionos.network.commons.EUI64} conversion class
 * that can convert MAC addresses
 * into the corresponding IPv6 addresses using the EUI-64 / SLAAC mechanism.
 * */
package com.ionos.network.commons;
