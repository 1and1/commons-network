package com.ionos.network.commons.address;

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
import static com.ionos.network.commons.address.BitsAndBytes.BYTE_MASK;

/**
 * An immutable IP network that consists of a IP prefix and a prefix length.
 * <br>
 * Example: 1.2.3.4/24
 * <br>
 * <ul>
 * <li>{@code 1.2.3.0} is the first IP in the network, the network address
 * (returned by {@link #getAddress()} ()})</li>
 * <li>{@code 24} is the network prefix length (returned by {@link #getPrefix()})
 * and used as index in all static get* methods</li>
 * <li>{@code 255.255.255.0} is the subnet mask for this network returned
 * by {@link #getSubnetMask()}</li>
 * <li>0.0.0.255 is the inverse network mask for this network</li>
 * </ul>
 *
 * Objects of the Network class are immutable!
 * @author Stephan Fuhrmann
 *
 **/
public final class Network implements Iterable<IP> {

    /** Pre-calculated network masks for IPv4. */
    private static final NetworkMaskData[] IP_V4_NETWORK_MASK_DATA;

    /** Pre-calculated network masks for IPv6. */
    private static final NetworkMaskData[] IP_V6_NETWORK_MASK_DATA;

    static {
        IP_V4_NETWORK_MASK_DATA = new NetworkMaskData[
                IPVersion.IPV4.getAddressBits() + 1];
        for (int i = 0; i <= IPVersion.IPV4.getAddressBits(); i++) {
            IP_V4_NETWORK_MASK_DATA[i] =
                    new NetworkMaskData(IPVersion.IPV4, i);
        }

        IP_V6_NETWORK_MASK_DATA = new NetworkMaskData[
                IPVersion.IPV6.getAddressBits() + 1];
        for (int i = 0; i <= IPVersion.IPV6.getAddressBits(); i++) {
            IP_V6_NETWORK_MASK_DATA[i] =
                    new NetworkMaskData(IPVersion.IPV6, i);
        }
    }

    /** Compares networks {@link Network#getAddress() start} IPs. */
    private static final Comparator<Network> NETWORK_START_COMPARATOR =
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
     * @see #isRFC1918(IP)
     * @see <a href="http://www.faqs.org/rfcs/rfc1918.html">
     *     Address Allocation for Private Internets</a>
     */
    private static final Network[] RFC_1918_NETWORKS = new Network[]{
            new Network(IPParser.INSTANCE.parse("10.0.0.0"), 8),
            new Network(IPParser.INSTANCE.parse("172.16.0.0"), 12),
            new Network(IPParser.INSTANCE.parse("192.168.0.0"), 16)
    };

    /** The prefix size in bits. */
    private final int prefix;

    /**
     * First IP in the network.
     *
     * @see #ipEnd
     * @see #getAddress()
     */
    private final IP ipAddress;

    /**
     * Last IP in the network.
     *
     * @see #ipAddress
     * @see #getAddressEnd()
     */
    private final IP ipEnd;

    /**
     * Creates an instance.
     * @param inIP the network address of the network.
     * @param inPrefix the prefix size of the network in number of bits.
     * @throws NullPointerException if the ip is {@code null}.
     * @throws IllegalArgumentException if the prefix size
     * does not match the IP protocol version.
     */
    public Network(final IP inIP,
                   final int inPrefix) {
        Objects.requireNonNull(inIP, "ip is null");
        if (inPrefix > inIP.getIPVersion().getAddressBits()
                || inPrefix < 0) {
            throw new IllegalArgumentException(
                    "illegal network prefix " + inPrefix);
        }
        this.prefix = inPrefix;

        final NetworkMaskData maskData =
                getNetworkMaskData(inIP.getIPVersion())[this.prefix];

        this.ipAddress = inIP.and(
                maskData.subnetMask
                        .address);

        if (this.prefix == 0) {
            this.ipEnd = inIP.getIPVersion().getMaximumAddress();
        } else {
            this.ipEnd = this.ipAddress.add(
                    maskData.inverseSubnetMask
                    .address);
        }
    }

    /**
     * Constructor for a new network.
     * @param networkWithPrefix a network in the format {@code 1.2.3.4/23}.
     */
    public Network(final String networkWithPrefix) {
        this(networkPartOf(networkWithPrefix), prefixPartOf(networkWithPrefix));
    }

    /** Calculate the network part of a network/prefix string.
     * @param networkWithPrefix the network, for example {@code "1.2.3.4/24"}.
     * @return the network part as an IP, in the above example {@code 1.2.3.4}.
     * */
    private static IP networkPartOf(final String networkWithPrefix) {
        Objects.requireNonNull(networkWithPrefix, "network is null");
        final int index = networkWithPrefix.indexOf('/');
        if (index == -1) {
            throw new IllegalArgumentException(
                    "no '/' found in network '" + networkWithPrefix + "'");
        }
        final String sIP = networkWithPrefix.substring(0, index);
        return IPParser.INSTANCE.parse(sIP);
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

    /** Constructs a new network from a network prefix and a network mask.
     * @param networkAddress the network prefix to use. Example {@code 192.168.0.0}.
     * @param networkMask the network mask to use. Example {@code 255.255.0.0}.
     * */
    public Network(final IP networkAddress, final IP networkMask) {
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
    public static boolean isRFC1918(final IP ip) {
        boolean result = false;
        if (ip != null && ip.getIPVersion() == IPVersion.IPV4) {
            for (final Network privateNetwork : RFC_1918_NETWORKS) {
                if (privateNetwork.contains(ip)) {
                    result = true;
                }
            }
        }
        return result;
    }

    /**
     * Get the network mask for a IP version and a number of network bits.
     *
     * @param version the IP version to get the network mask for
     * @param prefix    the bit length of the prefix
     * @return the network mask as an IP.
     */
    public static IP getSubnetMask(final IPVersion version, final int prefix) {
        Objects.requireNonNull(version, "the ip version may not be null");
        return getNetworkMaskData(version, prefix).subnetMask;
    }

    /**
     * Get the network mask for a IP version and a number of network bits.
     *
     * @param version the IP version to get the network mask for.
     * @param prefix    the bit length of the prefix.
     * @return the network mask as an IP
     */
    public static IP getInverseSubnetMask(final IPVersion version,
                                          final int prefix) {
        Objects.requireNonNull(version, "the ip version may not be null");
        return getNetworkMaskData(version, prefix).inverseSubnetMask;
    }

    /**
     * Get the prefix size in bits.
     *
     * @param netMask the network mask to get the size for
     * @return the prefix size in bits.
     * @throws IllegalArgumentException if it's not a legal netMask
     */
    public static int getPrefix(final IP netMask) {
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
        Objects.requireNonNull(version, "the ip version may not be null");
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
     * @return the network mask data for the ip version and prefix.
     * */
    private static NetworkMaskData getNetworkMaskData(
            final IPVersion version,
            final int prefixBits) {
        Objects.requireNonNull(version, "the ip version may not be null");
        if (prefixBits < 0 || prefixBits > version.getAddressBits()) {
            throw new IllegalArgumentException("Prefix is illegal: "
                    + prefixBits);
        }
        return getNetworkMaskData(version)[prefixBits];
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
     * @return the list of Networks with the guaranteed size greater or
     * equal 1. The order of the networks is in ascending IP order.
     * @throws NullPointerException if one of the arguments
     * is <code>null</code>.
     * @throws IllegalArgumentException if inStartIP is larger than
     * inEndIP, or the IP versions don't match.
     */
    public static List<Network> rangeFrom(final IP inStartIP,
                                          final IP inEndIP) {
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
        if (inStartIP.equals(inEndIP)) {
            return Collections.singletonList(
                    new Network(inStartIP,
                            inStartIP.getIPVersion().getAddressBits()));
        }
        // ip address bit width
        final int adrBits = inStartIP.getIPVersion().getAddressBits();

        final List<Network> result = new ArrayList<>();

        final IP endIPExclusive = inEndIP.add(1);

        // two complement negation: this is (- startIP)
        final IP firstNegated = inStartIP.invert().add(1);
        // example: for a.b.c.0-a.b.c.255 we now have 0.0.0.255
        final IP net = endIPExclusive.add(firstNegated.address);
        // the ip count as a byte array integer. For our example, this is 256.
        byte[] ipcount = net.address;
        // the increment byte array for adding the just processed network
        final byte[] increment = new byte[ipcount.length];

        // current ip position
        IP cur = inStartIP;
        IP last = null;

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

            result.add(new Network(cur, adrBits - currentBit));

            // book keeping work
            if (currentBit < adrBits) {
                final int byteOfs = (adrBits - currentBit - 1)
                        >> BitsAndBytes.BIT_SHIFT_BYTE;
                final int bitOfs = currentBit & BitsAndBytes.BIT_MASK_BYTE;
                last = cur;
                increment[byteOfs] |= (1 << bitOfs);
                cur = cur.add(increment);
                increment[byteOfs] &= ~(1 << bitOfs);

                // two complement negation: this is (- startIP)
                final IP curNegated = cur.invert().add(1);
                // example: for a.b.c.0-a.b.c.255 we now have 0.0.0.255
                IP curNetnet = endIPExclusive.add(curNegated.address);
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
     * @return the list of networks without one network containing the other.
     * @see #mergeNeighbors(Collection)
     */
    public static Set<Network> mergeContaining(
            final Collection<Network> networks) {
        Network[] nets = networks.toArray(new Network[0]);
        Set<Network> result = new HashSet<>();

        // sort the networks by their size. this way we only need
        // to compare O(n*lg n) times
        Arrays.sort(nets,
                (o1, o2) -> o2.getPrefix() - o1.getPrefix());

        for (int i = 0; i < nets.length; i++) {
            boolean contained = false;

            if (nets[i] == null) {
                continue;
            }
            for (int j = i + 1; j < nets.length; j++) {

                if (i == j) {
                    continue;
                }
                if (nets[j] != null && nets[j].contains(nets[i])) {
                    nets[i] = null;
                    contained = true;
                    break;
                }
            }

            if (!contained) {
                result.add(nets[i]);
            }
        }

        return result;
    }

    /**
     * Merges a list of networks joining neighbor networks of the same size
     * to one bigger network. Example: {@code 192.168.0.0/25}
     * and {@code 192.168.0.128/25} will
     * be merged to {@code 192.168.0.0/24}.
     *
     * @param networks the input networks to be joined.
     * @return a new network list with neighbor networks merged.
     * @see #mergeContaining(Collection)
     */
    public static List<Network> mergeNeighbors(
            final Collection<Network> networks) {
        final List<Network> result = new ArrayList<>(networks);
        result.sort(NETWORK_START_COMPARATOR);
        for (int i = 0; i < result.size() - 1; i++) {
            Network left = result.get(i);
            Network right = result.get(i + 1);

            if (left.getPrefix() != right.getPrefix()) {
                continue;
            }
            Network joint = new Network(left.getAddress(),
                    left.getPrefix() - 1);
            if (joint.contains(left) && joint.contains(right)) {
                result.remove(i + 1);
                result.remove(i);
                result.add(i, joint);
                // rescan this so we can join recursively
                i -= 2;
                if (i < -1) {
                    i = -1;
                }
            }
        }
        return result;
    }

    /**
     * Returns the network mask of {@code this} network.
     *
     * @return the network mask of {@code this} network as an {@link IP} object
     */
    public IP getSubnetMask() {
        return getSubnetMask(getAddress().getIPVersion(), getPrefix());
    }

    /**
     * Returns the number of bits of {@code this} network prefix.
     *
     * @return the number of bits in the prefix, for example
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
    public IP getAddress() {
        return ipAddress;
    }

    /**
     * Returns the last IP of {@code this} network.
     *
     * @return the last IP of this network (inclusive).
     */
    public IP getAddressEnd() {
        return ipEnd;
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
    public boolean contains(final Network network) {
        Objects.requireNonNull(network, "Network is null");
        byte[] thisNetMask = getSubnetMask().address;
        byte[] thisNetworkBytes = getAddress().address;
        byte[] otherNetworkBytes = network.getAddress().address;

        if (thisNetworkBytes.length != otherNetworkBytes.length) {
            return false;
        }

        // bigger prefix network can not contain smaller prefix network
        if (prefix > network.prefix) {
            return false;
        }

        for (int i=0; i < thisNetworkBytes.length; i++) {
            if ((thisNetworkBytes[i] & thisNetMask[i]) != (otherNetworkBytes[i] & thisNetMask[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Tests whether the given IP address is contained in this {@link Network}.
     *
     * @param ip the ip address to test.
     * @return {@code true} if this network contains the given IP, {@code false} otherwise. Networks
     * do not contain ip addresses of different versions (IPV4 vs. IPV6).
     * This method will return {@code false} in such a case.
     */
    public boolean contains(final IP ip) {
        Objects.requireNonNull(ip, "IP is null");
        byte[] netMask = getSubnetMask().address;
        byte[] ipBytes = ip.address;
        byte[] networkBytes = getAddress().address;

        if (ip.address.length != netMask.length) {
            return false;
        }

        for (int i=0; i < ip.address.length; i++) {
            if ((ipBytes[i] & netMask[i]) != (networkBytes[i] & netMask[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Split the network up into smaller parts.
     *
     * @param length the prefix length of the smaller parts in bits,
     *               must be smaller than the original size.
     *               Example: A network with a prefix of
     *               24 can be split into two networks
     *               of the size 25 or
     *               four networks of the size 26.
     * @return a collection of networks.
     */
    public List<Network> split(final int length) {
        // +++ stfu 2007-10-30: this should return an iterator instead
        IPVersion ipVersion = getIPVersion();

        if (prefix > length) {
            throw new IllegalArgumentException(
                    "Splitting not allowed to bigger networks");
        }
        if (length < 0 || length > ipVersion.getAddressBits()) {
            throw new IllegalArgumentException(
                    "Too big for this kind of address type");
        }
        int iterateBitLength = length - prefix;
        byte[] incrementBytes = new byte[ipVersion.getAddressBytes()];
        int byteOfs = incrementBytes.length
                - 1
                - ((ipVersion.getAddressBits() - length) >> BitsAndBytes.BIT_SHIFT_BYTE);
        int bitOfs = (ipVersion.getAddressBits() - length) & BitsAndBytes.BIT_MASK_BYTE;
        incrementBytes[byteOfs] = (byte) (1 << bitOfs);

        List<Network> resultCollection =
                new java.util.ArrayList<>(1 << iterateBitLength);

        for (IP curIP = getAddress();
             contains(curIP);
             curIP = curIP.add(incrementBytes)) {
            Network test = new Network(curIP, length);
            resultCollection.add(test);
        }

        return resultCollection;
    }

    /**
     * Returns an iterator over the IP addresses within {@code this} network.
     * <br>
     * Try to avoid the usage of this method for performance reasons.
     *
     * @return iterator over the IP addresses within {@code this} network.
     * @see #stream()
     */
    @Override
    public Iterator<IP> iterator() {
        return new NetworkIPIterator(this);
    }

    /**
     * Returns a stream of IPs that are contained in this network.
     * @return a stream of IPs.
     * @see #iterator()
     */
    public Stream<IP> stream() {
        return StreamSupport.stream(new NetworkIPSpliterator(this), false);
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
            final Network ian = (Network) o;
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

    /** Pre-calculated network masks for one {@link IPVersion} flavor. */
    private static class NetworkMaskData {

        /** The network mask as an IP object.
         * Example: {@code 255.255.255.0}. */
        private final IP subnetMask;
        /** The inverse network mask as an IP object.
         * Example: {@code 0.0.0.255}. */
        private final IP inverseSubnetMask;

        /** Constructor for a network mask in bits.
         * @param ipVersion the ip version to create the network masks for.
         * @param prefix the prefix length of the network mask in bits.
         * */
        NetworkMaskData(final IPVersion ipVersion, final int prefix) {
            byte[] data = new byte[ipVersion.getAddressBytes()];
            setLeadingBits(data, prefix);
            subnetMask = new IP(data);
            inverse(data);
            inverseSubnetMask = new IP(data);
        }

        /** Sets a number of leading bits to 1.
         * @param data the array where to set the first {@code bits} to one.
         * @param bits the number of bits to set to one.
         */
        static void setLeadingBits(final byte[] data, final int bits) {
            int i = 0;
            int remainingBits;
            for (remainingBits = bits;
                 remainingBits >= BITS_PER_BYTE;
                 remainingBits -= BITS_PER_BYTE) {
                data[i++] = (byte) BYTE_MASK;
            }

            if (remainingBits != 0) {
                data[i] = (byte)
                    (BYTE_MASK
                        << (BITS_PER_BYTE - remainingBits));
            }
        }

        /** Flips the bits in all bytes.
         * @param data the array which bytes to be inverted.
         * */
        static void inverse(final byte[] data) {
            for (int i = 0; i < data.length; i++) {
                data[i] ^= BYTE_MASK;
            }
        }
    }
}
