package com.ionos.network.commons.address;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.ionos.network.commons.address.BitsAndBytes.BITS_PER_BYTE;

/**
 * An IP network that consists of a IP prefix and a prefix length.
 * <br>
 * Example: 1.2.3.4/24
 * <br>
 * <ul>
 * <li>{@code 1.2.3.0} is the first IP in the network, the network address
 * (returned by {@link #getAddress()})</li>
 * <li>{@code 24} is the network prefix length (returned by
 * {@link #getPrefix()})
 * and used as index in all static get* methods</li>
 * <li>{@code 255.255.255.0} is the subnet mask for this network returned
 * by {@link #getSubnetMask()}</li>
 * <li>{@code 0.0.0.255} is the inverse network mask for this network,
 * see {@link #getInverseSubnetMask(Class, int)}</li>
 * </ul>
 *
 * Objects of the Network class are immutable!
 * @param <T> the IP address type this network is defined for.
 * @author Stephan Fuhrmann
 **/
public final class Network<T extends IP<T>>
        implements Iterable<T>, Serializable {
    /** The version number of this class. */
    private static final long serialVersionUID = 123816891688169L;

    /** Error message for null ip versions. */
    private static final String ERROR_IP_VERSION_NOT_NULL =
            "the ip version may not be null";

    /** Pre-calculated network masks for IPv4. */
    private static final NetworkMaskData[] IP_V4_NETWORK_MASK_DATA;

    /** Pre-calculated network masks for IPv6. */
    private static final NetworkMaskData[] IP_V6_NETWORK_MASK_DATA;

    static {
        IP_V4_NETWORK_MASK_DATA = new NetworkMaskData[
                IPVersion.IPV4.getAddressBits() + 1];
        for (int i = 0; i <= IPVersion.IPV4.getAddressBits(); i++) {
            byte[] data = new byte[IPVersion.IPV4.getAddressBytes()];
            BitsAndBytes.setLeadingBits(data, i);
            IP_V4_NETWORK_MASK_DATA[i] =
                    new NetworkMaskData<>(new IPv4(data));
        }

        IP_V6_NETWORK_MASK_DATA = new NetworkMaskData[
                IPVersion.IPV6.getAddressBits() + 1];
        for (int i = 0; i <= IPVersion.IPV6.getAddressBits(); i++) {
            byte[] data = new byte[IPVersion.IPV6.getAddressBytes()];
            BitsAndBytes.setLeadingBits(data, i);
            IP_V6_NETWORK_MASK_DATA[i] =
                    new NetworkMaskData<>(new IPv6(data));
        }
    }

    /** Compares networks {@link Network#getAddress() start} IPs. */
    private static final Comparator<Network<?>> NETWORK_START_COMPARATOR =
            (network1, network2) -> {
                final int startIpComparison = network1.getAddress()
                        .compareTo(network2.getAddress());
                if (startIpComparison != 0) {
                    return startIpComparison;
                }
                return network1.getPrefix() - network2.getPrefix();
            };

    /**
     * The list of private IPv4 networks as according to RFC 1918.
     * @see #isRFC1918(IPv4)
     * @see <a href="http://www.faqs.org/rfcs/rfc1918.html">
     *     Address Allocation for Private Internets</a>
     */
    private static final List<Network<IPv4>> RFC_1918_NETWORKS = Arrays.asList(
            new Network<>(
                    IPParsers.DOTTED_DECIMAL.parse(
                            "10.0.0.0"), 8),  //NOSONAR
            new Network<>(
                    IPParsers.DOTTED_DECIMAL.parse(
                            "172.16.0.0"), 12),  //NOSONAR
            new Network<>(
                    IPParsers.DOTTED_DECIMAL.parse(
                            "192.168.0.0"), 16)  //NOSONAR
    );

    /** The prefix size in bits. */
    private int prefix;

    /**
     * First IP in the network.
     *
     * @see #ipBroadcast
     * @see #getAddress()
     */
    private T ipAddress;

    /**
     * Last IP in the network.
     *
     * @see #ipAddress
     * @see #getBroadcast()
     */
    private transient T ipBroadcast;

    /**
     * Creates an instance.
     * @param inIP the network address of the network.
     * @param inPrefix the prefix size of the network in number of bits.
     * @throws NullPointerException if the ip is {@code null}.
     * @throws IllegalArgumentException if the prefix size
     * does not match the IP protocol version.
     */
    public Network(final IP<T> inIP,
                   final int inPrefix) {
        Objects.requireNonNull(inIP, "ip is null");
        this.prefix = requireValidPrefix(inIP.getIPVersion(), inPrefix);

        final NetworkMaskData<T> maskData =
                getNetworkMaskData(inIP.getIPVersion(), prefix);

        this.ipAddress = inIP.and(
                maskData.subnetMask
                        .address);

        this.ipBroadcast = ipBroadcastFor(ipAddress, prefix);
    }

    private static <U extends IP<U>> U ipBroadcastFor(final U startAddress,
                                                   final int prefix) {
            return startAddress.add(
                    getNetworkMaskData(startAddress.getIPVersion(), prefix)
                            .inverseSubnetMask
                            .address);
    }

    /**
     * Constructor for a new network.
     * @param networkWithPrefix a network in the format {@code 1.2.3.4/23}.
     */
    public Network(final String networkWithPrefix) {
        this(networkPartOf(networkWithPrefix),
                prefixPartOf(networkWithPrefix));
    }

    /** Calculate the network part of a network/prefix string.
     * @param networkWithPrefix the network, for example {@code "1.2.3.4/24"}.
     * @param <U> the type of IP address to get the network part for.
     * @return the network part as an IP, in the above example {@code 1.2.3.4}.
     * */
    private static <U extends IP<U>> U networkPartOf(
            final String networkWithPrefix) {
        Objects.requireNonNull(networkWithPrefix, "network is null");
        final int index = networkWithPrefix.indexOf('/');
        if (index == -1) {
            throw new IllegalArgumentException(
                    "no '/' found in network '" + networkWithPrefix + "'");
        }
        final String sIP = networkWithPrefix.substring(0, index);
        return (U) IPParsers.DEFAULT.parse(sIP);
    }

    /** Calculate the network part of a network/prefix string.
     * @param networkWithPrefix the network, for example {@code "1.2.3.4/24"}.
     * @return the prefix size as number of bits,
     * in the above example {@code 24}.
     * */
    private static int prefixPartOf(final String networkWithPrefix) {
        Objects.requireNonNull(networkWithPrefix, "network is null");
        final int index = networkWithPrefix.indexOf('/');
        if (index == -1) {
            throw new IllegalArgumentException(
                    "no '/' found in network '" + networkWithPrefix + "'");
        }
        final String sPrefix = networkWithPrefix.substring(index + 1);
        try {
            return Integer.parseInt(sPrefix);
        } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException(
                    "could not parse prefix '" + sPrefix + "'");
        }

    }

    private static int requireValidPrefix(
            final IPVersion ipVersion,
            final int prefix) {
        if (prefix < 0) {
            throw new IllegalArgumentException("Prefix " + prefix
            + "is < 0");
        }
        if (prefix > ipVersion.getAddressBits()) {
            throw new IllegalArgumentException("Prefix " + prefix
                    + "is > "
                    + ipVersion.getAddressBits());
        }
        return prefix;
    }

    /** Constructs a new network from a network prefix and a network mask.
     * @param networkAddress the network prefix to use.
     *                       Example {@code 192.168.0.0}.
     * @param networkMask the network mask to use.
     *                    Example {@code 255.255.0.0}.
     * */
    public Network(final T networkAddress, final T networkMask) {
        this(networkAddress, getPrefix(networkMask));
    }

    /**
     * Returns {@code true} if the specified IP is within a private network
     * {@link #RFC_1918_NETWORKS}, otherwise {@code false}.
     * <br>
     * If the specified IP is {@code null} returns {@code false}.
     *
     * @param ip the IP to check
     * @return {@code true} if the specified IP is within a
     * private network, otherwise {@code false}
     */
    public static boolean isRFC1918(final IPv4 ip) {
        boolean result = false;
        if (ip != null) {
            for (final Network<IPv4> privateNetwork : RFC_1918_NETWORKS) {
                if (privateNetwork.contains(ip)) {
                    result = true;
                }
            }
        }
        return result;
    }

    /**
     * Get the network mask for a IP version and a number of network bits.
     * @param ipClass the IP class to get the network mask for.
     * @param prefix    the bit length of the prefix.
     * @param <U> the IP class to get the subnet mask for.
     *           Can be {@linkplain IPv4} or {@linkplain IPv6}.
     * @return the network mask as an IP, for example {@code 255.255.255.0 }.
     */
    public static <U extends IP<U>> U getSubnetMask(
            final Class<U> ipClass,
            final int prefix) {
        Objects.requireNonNull(ipClass, ERROR_IP_VERSION_NOT_NULL);
        IPVersion version = IP.getIPVersion(ipClass);
        return (U) getNetworkMaskData(
                version,
                requireValidPrefix(version, prefix)).subnetMask;
    }

    /**
     * Get the inverse network mask for a IP version and a number
     * of network bits.
     * @param ipClass the IP class to get the inverse network mask for.
     * @param prefix    the bit length of the prefix.
     * @param <U> the IP class to get the inverse subnet mask for.
     *           Can be {@linkplain IPv4} or {@linkplain IPv6}.
     * @return the inverse network mask as an IP, for
     * example {@code 0.0.0.255 }.
     */
    public static <U extends IP<U>> U getInverseSubnetMask(
                                          final Class<U> ipClass,
                                          final int prefix) {
        Objects.requireNonNull(ipClass, ERROR_IP_VERSION_NOT_NULL);
        IPVersion version = IP.getIPVersion(ipClass);
        return (U) getNetworkMaskData(
                version,
                requireValidPrefix(version, prefix)).inverseSubnetMask;
    }

    /**
     * Get the prefix size in bits.
     *
     * @param netMask the network mask to get the size for.
     * @return the prefix size in bits.
     * @throws IllegalArgumentException if it's not a legal netMask.
     */
    public static int getPrefix(final IP<?> netMask) {
        final NetworkMaskData[] data =
                getNetworkMaskData(netMask.getIPVersion());
        for (int i = 0; i < data.length; i++) {
            if (data[i].subnetMask.equals(netMask)) {
                return i;
            }
        }
        throw new IllegalArgumentException("Netmask "
                + netMask + " is no legal CIDR netMask");
    }

    /** Get the network mask buffer array for this IP version.
     * @param version the IP version to get the mask data for.
     * @return the network mask data for the ip version and prefix.
     * */
    private static NetworkMaskData[] getNetworkMaskData(
            final IPVersion version) {
        Objects.requireNonNull(version, ERROR_IP_VERSION_NOT_NULL);
        switch (version) {
            case IPV4:
                return IP_V4_NETWORK_MASK_DATA;
            case IPV6:
                return IP_V6_NETWORK_MASK_DATA;
            default:
                throw new IllegalStateException();
        }
    }

    /** Get the network mask buffer array for this IP version.
     * @param version the IP version to get the mask data for.
     * @param prefixBits the prefix size in bits.
     * @param <U> the IP type of the address.
     * @return the network mask data for the ip version and prefix.
     * */
    private static <U extends IP<U>> NetworkMaskData<U> getNetworkMaskData(
            final IPVersion version,
            final int prefixBits) {
        Objects.requireNonNull(version, ERROR_IP_VERSION_NOT_NULL);
        requireValidPrefix(version, prefixBits);
        switch (version) {
            case IPV4:
                return IP_V4_NETWORK_MASK_DATA[prefixBits];
            case IPV6:
                return IP_V6_NETWORK_MASK_DATA[prefixBits];
            default:
                throw new IllegalStateException();
        }
    }

    /**
     * Create a list of Networks from the start to the end IP address.
     * A range of IPs does not necessarily start or end at the boundary of
     * a network in slash notation. This means you will get one network
     * if you can also write the range in slash notation (192.168.1.0/24),
     * or multiple networks otherwise.
     *
     * @param inStartIP the first IP address of the range, inclusive.
     * @param inEndIP   the end IP address of the range, inclusive.
     * @param <T> the IP type that both IP addresses and the resulting
     *           networks are in.
     * @return the list of Networks with the guaranteed size greater or
     * equal 1. The order of the networks is in ascending IP order.
     * @throws NullPointerException if one of the arguments
     * is <code>null</code>.
     * @throws IllegalArgumentException if inStartIP is larger than
     * inEndIP, or the IP versions don't match.
     */
    public static <T extends IP<T>> List<Network<T>> rangeFrom(
            final T inStartIP,
            final T inEndIP) {
        Objects.requireNonNull(inStartIP, "start IP is null");
        Objects.requireNonNull(inEndIP, "end IP is null");
        if (inStartIP.compareTo(inEndIP) > 0) {
            throw new IllegalArgumentException(
                    "start IP must be smaller or equal to end IP");
        }
        if (!inStartIP.getIPVersion().equals(inEndIP.getIPVersion())) {
            throw new IllegalArgumentException(
                    "IP versions of start and end do not match");
        }
        if (inStartIP.equals(inEndIP)) { //NOSONAR
            return Collections.singletonList(
                    new Network<T>(inStartIP,
                            inStartIP.getIPVersion().getAddressBits()));
        }
        // ip address bit width
        final int adrBits = inStartIP.getIPVersion().getAddressBits();

        final List<Network<T>> result = new ArrayList<>();

        final T endIPExclusive = inEndIP.add(1);

        // two complement negation: this is (- startIP)
        final T firstNegated = inStartIP.invert().add(1);
        // example: for a.b.c.0-a.b.c.255 we now have 0.0.0.255
        final T net = endIPExclusive.add(firstNegated.address);
        // the ip count as a byte array integer. For our example, this is 256.
        byte[] ipcount = net.address;
        // the increment byte array for adding the just processed network
        final byte[] increment = new byte[ipcount.length];

        // current ip position
        T cur = inStartIP;
        T last = null;

        // go thru the IPs and add the biggest possible network.
        while (cur.compareTo(inEndIP) <= 0
                && (last == null || last.compareTo(cur) < 0)) {
            int currentBit = BitsAndBytes.getLowestBitSet(cur.address);

            if (currentBit == -1) {
                currentBit = adrBits;
            }
            final int ipCountHigh = BitsAndBytes.getHighestBitSet(ipcount);
            if (ipCountHigh != -1 && currentBit > ipCountHigh) {
                // too high, need to add something else
                currentBit = ipCountHigh;
            }

            result.add(new Network<T>(cur, adrBits - currentBit));

            // book keeping work
            if (currentBit < adrBits) {
                final int byteOfs = (adrBits - currentBit - 1)
                        >> BitsAndBytes.BIT_SHIFT_BYTE;
                final int bitOfs = currentBit & BitsAndBytes.BIT_MASK_TRIPLE;
                last = cur;
                increment[byteOfs] |= (1 << bitOfs);
                cur = cur.add(increment);
                increment[byteOfs] &= ~(1 << bitOfs);

                // two complement negation: this is (- startIP)
                final T curNegated = cur.invert().add(1);
                // example: for a.b.c.0-a.b.c.255 we now have 0.0.0.255
                T curNetnet = endIPExclusive.add(curNegated.address);
                // the ip count as a byte array integer.
                // For our example, this is 256.
                ipcount = curNetnet.address;
            } else {
                break;
            }
        }

        return result;
    }

    /**
     * Merges a list of networks removing networks that are contained in
     * others.
     *
     * @param networks the input networks to be merged.
     * @param <T> the IP sub type the networks are in.
     * @return the list of networks without one network containing the other.
     * @see #mergeNeighbors(Collection)
     */
    public static <T extends IP<T>> Set<Network<T>> mergeContaining(
            final Collection<Network<T>> networks) {
        List<Network<T>> nets = new ArrayList<>(networks);
        Set<Network<T>> result = new HashSet<>();

        // sort the networks by their size. this way we only need
        // to compare O(n*lg n) times
        nets.sort(
                (o1, o2) -> o2.getPrefix() - o1.getPrefix());

        int increment = 0;
        for (int i = 0; i < nets.size(); i += increment) {
            boolean contained = false;

            for (int j = i + 1; j < nets.size(); j++) {
                if (nets.get(j).contains(nets.get(i))) {
                    contained = true;
                    break;
                }
            }

            if (contained) {
                nets.remove(i);
                increment = 0;
            } else {
                result.add(nets.get(i));
                increment = 1;
            }
        }

        return result;
    }

    /**
     * Merges a list of networks joining neighbor networks.
     * Joining happens when either two same-sized neighbors
     * are in the list, or when one network contains the other.
     * Example: {@code 192.168.0.0/25}
     * and {@code 192.168.0.128/25} will
     * be merged to {@code 192.168.0.0/24}.
     *
     * @param <U> the IP subclass this network is in.
     * @param networks the input networks to be joined.
     * @return a new network list with neighbor networks merged.
     * @see #mergeContaining(Collection)
     */
    public static <U extends IP<U>> List<Network<U>> mergeNeighbors(
            final Collection<Network<U>> networks) {
        final List<Network<U>> from = new ArrayList<>(networks);
        final List<Network<U>> to = new ArrayList<>(networks.size());
        from.sort(NETWORK_START_COMPARATOR);
        boolean reduction;
        do {
            to.clear();
            reduction = mergeNeighborsTryReduce(from, to);
            if (reduction) {
                // prepare for next iteration
                from.clear();
                from.addAll(to);
            }
        } while (reduction);
        return to;
    }

    /** Do one reduction step over the list of networks.
     * @param from the input list of networks. Read-only. Needs to be sorted.
     * @param to the empty list of network that gets filled by this method.
     * @param <U> the IP type of the networks processed.
     * @return {@code true} if the list from was reduced somehow.
     * @see #mergeNeighbors(Collection)
     * */
    private static <U extends IP<U>> boolean mergeNeighborsTryReduce(
            final List<Network<U>> from,
            final List<Network<U>> to) {
        boolean reduction = false;
        boolean skip = false;
        for (int i = 0; i < from.size(); i++) {
            Network<U> left = from.get(i);
            boolean merged = false;

            // the network to add after this iteration
            Network<U> add = left;

            if (skip) {
                // left was a right network that got reduced
                skip = false;
                continue;
            }

            if (i + 1 < from.size()) {
                Network<U> right = from.get(i + 1);
                // left contains right
                if (left.getPrefix() <= right.getPrefix()
                        && left.contains(right)) {
                    add = left;
                    merged = true;
                }

                if (!merged && left.getPrefix() == right.getPrefix()
                        && left.getBroadcast()
                        .add(1).equals(right.ipAddress)) {
                    Network<U> joint = new Network<>(left.getAddress(),
                            left.getPrefix() - 1);
                    if (joint.contains(left) && joint.contains(right)) {
                        add = joint;
                        merged = true;
                    }
                }
            }

            to.add(add);
            if (merged) {
                skip = true;
                reduction = true;
            }
        }
        return reduction;
    }

    /**
     * Returns the network mask of {@code this} network.
     *
     * @return the network mask of {@code this} network as an {@link IP} object.
     * @see #getSubnetMask(Class, int)
     */
    public T getSubnetMask() {
        return getSubnetMask((Class<T>) getAddress().getClass(), getPrefix());
    }

    /**
     * Returns the number of bits of {@code this} network prefix.
     *
     * @return the number of bits in the prefix. For example
     * for {@code 1.2.3.4/24}
     * returns 24.
     */
    public int getPrefix() {
        return prefix;
    }

    /**
     * Get the start IP of {@code this} network.
     *
     * @return the start IP of this network (inclusive).
     */
    public T getAddress() {
        return ipAddress;
    }

    /**
     * Returns the broadcast IP of {@code this} network.
     *
     * @return the broadcast IP of this network (inclusive).
     * Example: For a {@code 192.168.4.0/24} network this would be
     * {@code 192.168.4.255}.
     */
    public T getBroadcast() {
        return ipBroadcast;
    }

    /** Get the IP version of this network.
     * @return the IP version of this network.
     * */
    public IPVersion getIPVersion() {
        return ipAddress.getIPVersion();
    }

    /**
     * Tests whether the given Network is completely contained
     * in {@code this} network.
     *
     * @param network the network to test
     * @return {@code true} if the specified network is
     * included in {@code this} network, otherwise {@code false}
     */
    public boolean contains(final Network<?> network) {
        Objects.requireNonNull(network, "Network is null");
        byte[] thisNetMask = getSubnetMask().address;
        byte[] thisNetworkBytes = getAddress().address;
        byte[] otherNetworkBytes = network.getAddress().address;

        // bigger prefix network can not contain smaller prefix network
        if (prefix > network.prefix) {
            return false;
        }
        return BitsAndBytes.equalsWithMask(thisNetworkBytes,
                otherNetworkBytes,
                thisNetMask);
    }

    /**
     * Tests whether the given IP address is contained in this {@link Network}.
     *
     * @param ip the ip address to test.
     * @return {@code true} if this network contains the given IP,
     * {@code false} otherwise. Networks
     * do not contain ip addresses of different versions (IPV4 vs. IPV6).
     * This method will return {@code false} in such a case.
     */
    public boolean contains(final IP<?> ip) {
        Objects.requireNonNull(ip, "IP is null");

        // different IP version?
        if (ipAddress.length() != ip.length()) {
            return false;
        }

        byte[] netMask = getSubnetMask().address;
        byte[] ipBytes = ip.address;
        byte[] networkBytes = getAddress().address;

        return BitsAndBytes.equalsWithMask(networkBytes, ipBytes, netMask);
    }

    /**
     * Split the network up into smaller parts.
     *
     * @param targetPrefix the target prefix length of the smaller
     *               parts in bits,
     *               must be smaller than the original size.
     *               Example: A network with a prefix of
     *               24 can be split into two networks
     *               of the size 25 or
     *               four networks of the size 26.
     * @return a collection of networks, each being a
     * {@code IP/targetPrefix} network.
     * @throws IllegalArgumentException if the targetPrefix parameter
     * does not match the ip version.
     */
    public List<Network<T>> split(final int targetPrefix) {
        // +++ stfu 2007-10-30: this should return an iterator instead
        IPVersion ipVersion = getIPVersion();

        if (prefix > targetPrefix) {
            throw new IllegalArgumentException(
                    "Splitting not allowed to bigger networks");
        }
        if (targetPrefix < 0 || targetPrefix > ipVersion.getAddressBits()) {
            throw new IllegalArgumentException(
                    "Too big for this kind of address type");
        }
        int iterateBitLength = targetPrefix - prefix;
        byte[] incrementBytes = new byte[ipVersion.getAddressBytes()];
        int byteOfs = incrementBytes.length - 1
                - ((ipVersion.getAddressBits() - targetPrefix)
                >> BitsAndBytes.BIT_SHIFT_BYTE);
        int bitOfs = (ipVersion.getAddressBits() - targetPrefix)
                & BitsAndBytes.BIT_MASK_TRIPLE;
        incrementBytes[byteOfs] = (byte) (1 << bitOfs);

        List<Network<T>> resultCollection =
                new ArrayList<>(1 << iterateBitLength);

        for (T curIP = getAddress();
             contains(curIP);
             curIP = curIP.add(incrementBytes)) {
            Network<T> current = new Network<>(curIP, targetPrefix);
            resultCollection.add(current);
        }

        return resultCollection;
    }

    /**
     * Returns an iterator over the IP addresses within {@code this} network.
     * <br>
     * Try to avoid the usage of this method for performance reasons.
     * There could be more of IPs in a network than your computer can process.
     *
     * @return iterator over the IP addresses within {@code this} network. The
     * iterator starts at the {@linkplain #getAddress() start address} and
     * ends at the {@linkplain #getBroadcast() broadcast address}.
     * @see #stream()
     */
    @Override
    public Iterator<T> iterator() {
        return new NetworkIPIterator<>(this);
    }

    /**
     * Returns a stream of IPs that are contained in this network.
     * <br>
     * Try to avoid the usage of this method for performance reasons.
     * There could be more of IPs in a network than your computer can process.
     * @return a stream of IPs. The
     * iterator starts at the {@linkplain #getAddress() start address} and
     * ends at the {@linkplain #getBroadcast() broadcast address}.
     * @see #iterator()
     */
    public Stream<T> stream() {
        return StreamSupport.stream(new NetworkIPSpliterator<>(this), false);
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (o instanceof Network) {
            final Network<?> ian = (Network) o;
            return (ipAddress.equals(ian.ipAddress))
                    && (prefix == ian.prefix);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return ipAddress.hashCode() ^ (prefix << BITS_PER_BYTE);
    }

    @Override
    public String toString() {
        return ipAddress.toString() + "/" + prefix;
    }

    /** Pre-calculated network masks for one {@link IPVersion} flavor.
     * @param <T> the IP address type this mask data is for.
     **/
    private static class NetworkMaskData<T extends IP<T>> {

        /** The network mask as an IP object.
         * Example: {@code 255.255.255.0}. */
        private final T subnetMask;

        /** The inverse network mask as an IP object.
         * Example: {@code 0.0.0.255}. */
        private final T inverseSubnetMask;

        /** Constructor for a network mask in bits.
         * @param inSubnetMask the IP having a subnetmask,
         *                     for example {@code 255.255.0.0}.
         * */
        NetworkMaskData(final T inSubnetMask) {
            subnetMask = inSubnetMask;
            inverseSubnetMask = subnetMask.invert();
        }
    }

    /** Custom serialization for writing an address.
     * @param s the stream to write the object to.
     * @throws IOException if there's a problem in writing to the stream.
     * */
    private void writeObject(final ObjectOutputStream s) throws IOException {
        s.writeInt(ipAddress.address.length);
        s.write(ipAddress.address);
        s.writeInt(prefix);
    }

    /** Custom deserialization for reading an address.
     * @param s the stream to read the object from.
     * @throws IOException if there's a problem in reading from the stream.
     * */
    private void readObject(final ObjectInputStream s) throws IOException {
        int length = s.readInt();
        byte[] data = new byte[length];
        s.readFully(data);

        if (length == IPVersion.IPV4.getAddressBytes()) {
            ipAddress = (T) new IPv4(data);
        } else if (length == IPVersion.IPV6.getAddressBytes()) {
            ipAddress = (T) new IPv6(data);
        } else {
            throw new IllegalStateException();
        }

        prefix = requireValidPrefix(ipAddress.getIPVersion(), s.readInt());
        ipBroadcast = ipBroadcastFor(ipAddress, prefix);
    }
}
