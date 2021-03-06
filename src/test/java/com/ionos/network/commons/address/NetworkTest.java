package com.ionos.network.commons.address;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

/**
 */
public class NetworkTest {

    @Test
    public void testConstructor() {
        final Network<IPv4> n1 = new Network<>("192.168.0.42/24");
        assertEquals("192.168.0.0", n1.getAddress().toString());

        final Network<IPv4> n2 = new Network<>(new IPv4("192.168.0.42"), 24);
        assertEquals("192.168.0.0", n2.getAddress().toString());

        final Network<IPv4> n3 = new Network<>(new IPv4("192.168.0.42"), new IPv4("255.255.255.0"));
        assertEquals("192.168.0.0", n3.getAddress().toString());
        assertEquals("192.168.0.255", n3.getBroadcast().toString());
        assertEquals(24, n3.getPrefix());

        try {
            new Network<IPv4>("192.168.0.42");
            fail("should throw IllegalArgumentException");
        } catch (IllegalArgumentException iae) {
            // this should happen
        }
        try {
            new Network<IPv4>("192.168.0.42/33");
            fail("should throw IllegalArgumentException");
        } catch (IllegalArgumentException iae) {
            // this should happen
        }
        try {
            new Network<IPv4>("192.168.0.42/-1");
            fail("should throw IllegalArgumentException");
        } catch (IllegalArgumentException iae) {
            // this should happen
        }
        try {
            new Network<IPv4>("192.168.0.42/a");
            fail("should throw IllegalArgumentException");
        } catch (IllegalArgumentException iae) {
            // this should happen
        }
        try {
            new Network<IPv4>(new IPv4("192.168.0.42"), new IPv4("255.255.255.1"));
            fail("should throw IllegalArgumentException");
        } catch (IllegalArgumentException iae) {
            // this should happen
        }
    }

    @Test
    public void testContainsNetwork() {
        final Network<IPv4> n1 = new Network<>(new IPv4("192.168.0.42"), 24);
        final Network<IPv4> n2 = new Network<>(new IPv4("192.168.0.128"), 25);
        final Network<IPv4> n3 = new Network<>(new IPv4("192.168.0.42"), 25);
        assertTrue(n1.contains(n2));
        assertTrue(n1.contains(n3));

        assertFalse(n2.contains(n1));
        assertFalse(n2.contains(n3));

        assertFalse(n3.contains(n1));
        assertFalse(n3.contains(n2));
    }

    @Test
    public void testContains() {
        final Network<IPv4> n1 = new Network<>(new IPv4("192.168.0.42"), 24);
        final Network<IPv4> n2 = new Network<>(new IPv4("192.168.0.42"), 31);
        final IPv4 ip1 = new IPv4("192.168.0.0");
        final IPv4 ip2 = new IPv4("192.168.0.255");
        final IPv4 ip3 = new IPv4("192.168.1.0");
        final IPv4 ip4 = new IPv4("192.168.2.255");
        final IPv4 ip5 = new IPv4("192.168.0.42");
        final IPv4 ip6 = new IPv4("192.168.0.43");

        assertTrue(n1.contains(ip1));
        assertTrue(n1.contains(ip2));
        assertFalse(n1.contains(ip3));
        assertFalse(n1.contains(ip4));
        assertTrue(n1.contains(ip5));
        assertTrue(n1.contains(ip6));

        assertFalse(n2.contains(ip1));
        assertFalse(n2.contains(ip2));
        assertFalse(n2.contains(ip3));
        assertFalse(n2.contains(ip4));
        assertTrue(n2.contains(ip5));
        assertTrue(n2.contains(ip6));
    }

    @Test
    public void testContains32() {
        final Network<IPv4> n1 = new Network<>(new IPv4("192.168.0.42"), (byte) 32);
        final IPv4 ip1 = new IPv4("192.168.0.42");
        assertTrue(n1.contains(ip1));
    }

    @Test
    public void testContainsDifferentVersion() {
        final Network<IPv6> n1 = new Network<>(new IPv6("::"), (byte) 0);
        final IPv4 ip1 = new IPv4("192.168.0.42");
        assertFalse(n1.contains(ip1));
    }

    @Test
    public void isPrivate() {
        final IPv4 privateIp = new IPv4("192.168.1.1");
        assertTrue(Network.isRFC1918(privateIp));

        // google DNS
        final IPv4 publicIp = new IPv4("8.8.8.8");
        assertFalse(Network.isRFC1918(publicIp));
    }

    @Test
    public void testIPIterator() {
        Network<IPv4> n;

        // two IPs
        n = new Network<>(new IPv4("192.168.2.16"), 31);

        Iterator<IPv4> iter = n.iterator();

        assertTrue(iter.hasNext());
        assertTrue(iter.hasNext());
        assertTrue(iter.hasNext());

        assertEquals(new IPv4("192.168.2.16"), iter.next());
        assertTrue(iter.hasNext());
        assertTrue(iter.hasNext());
        assertEquals(new IPv4("192.168.2.17"), iter.next());
        assertFalse(iter.hasNext());

        // one IP
        n = new Network<>(new IPv4("192.168.2.16"), 32);
        iter = n.iterator();

        assertTrue(iter.hasNext());

        assertEquals(new IPv4("192.168.2.16"), iter.next());
        assertFalse(iter.hasNext());
        try {
            iter.next();
            fail("should throw an exception");
        }
        catch (NoSuchElementException ignored) {
        }
    }

    @Test
    public void testSplitWithSlash14ToSlash16() {
        Network<IPv4> n;
        List<Network<IPv4>> test;

        // four IPs
        n = new Network<>(new IPv4("89.12.0.0"), 14);
        test = n.split(16);
        assertEquals(4, test.size());
        assertEquals(new Network("89.12.0.0/16"), test.get(0));
        assertEquals(new Network("89.13.0.0/16"), test.get(1));
        assertEquals(new Network("89.14.0.0/16"), test.get(2));
        assertEquals(new Network("89.15.0.0/16"), test.get(3));
    }

    @Test
    public void testSplitWithSlash31ToSlash32() {
        Network<IPv4> n;
        List<Network<IPv4>> test;

        // two IPs
        n = new Network<>(new IPv4("192.168.2.16"), 31);
        test = n.split(32);
        assertEquals(2, test.size());
        assertEquals(new Network("192.168.2.16/32"), test.get(0));
        assertEquals(new Network("192.168.2.17/32"), test.get(1));
    }

    @Test
    public void testSplitEndWithNoChange() {
        Network<IPv4> n;
        List<Network<IPv4>> test;

        n = new Network<>(new IPv4("192.168.2.16"), 32);
        test = n.split(32);
        assertEquals(1, test.size());
        assertEquals(new Network("192.168.2.16/32"), test.get(0));
    }

    @Test
    public void testRangeFromWithIPv6OneNetworkSlash32() {
        // start + stop are the same -> one /32
        List<Network<IPv4>> listIPv4 = Network.rangeFrom(new IPv4("192.168.1.0"), new IPv4("192.168.1.0"));
        assertEquals(Collections.singletonList(new Network<>("192.168.1.0/32")), listIPv4);
    }

    @Test
    public void testRangeFromWithIPv4OneNetworkSlash28() {
        List<Network<IPv4>> listIPv4 = Network.rangeFrom(
                new IPv4("192.168.1.0"),
                new IPv4("192.168.1.15"));
        assertEquals(Collections.singletonList(
                new Network<>("192.168.1.0/28")),
                listIPv4);
    }

    @Test
    public void testRangeFromWithIPv4TwoNetworks() {
        List<Network<IPv4>> listIPv4 = Network.rangeFrom(
                new IPv4("192.168.1.0"),
                new IPv4("192.168.1.16"));
        assertEquals(
                Arrays.asList(
                        new Network("192.168.1.0/28"),
                        new Network("192.168.1.16/32")),
                listIPv4);
    }

    @Test
    public void testRangeFromWithIPv4ThreeNetworks() {
        List<Network<IPv4>> listIPv4 = Network.rangeFrom(new IPv4("192.168.1.0"), new IPv4("192.168.2.16"));
        assertEquals(Arrays.asList(
                new Network("192.168.1.0/24"),
                new Network("192.168.2.0/28"),
                new Network("192.168.2.16/32")),
                listIPv4);
    }

    @Test
    public void testRangeFromWithIPv4FiveNetworks2() {
        List<Network<IPv4>> listIPv4 = Network.rangeFrom(new IPv4("192.168.1.32"), new IPv4("192.168.2.16"));
        assertEquals(Arrays.asList(
                new Network("192.168.1.32/27"),
                new Network("192.168.1.64/26"),
                new Network("192.168.1.128/25"),
                new Network("192.168.2.0/28"),
                new Network("192.168.2.16/32")), listIPv4);
    }

    @Test
    public void testRangeFromWithIPv4FourNetworks() {
        List<Network<IPv4>> listIPv4 = Network.rangeFrom(new IPv4("192.168.1.32"), new IPv4("192.168.2.15"));
        assertEquals(Arrays.asList(
                new Network("192.168.1.32/27"),
                new Network("192.168.1.64/26"),
                new Network("192.168.1.128/25"),
                new Network("192.168.2.0/28")), listIPv4);
    }

    @Test
    public void testRangeFromWithIPv4SixNetworks() {
        List<Network<IPv4>> listIPv4 = Network.rangeFrom(new IPv4("192.168.1.32"), new IPv4("192.168.3.16"));
        assertEquals(Arrays.asList(
                new Network("192.168.1.32/27"),
                new Network("192.168.1.64/26"),
                new Network("192.168.1.128/25"),
                new Network("192.168.2.0/24"),
                new Network("192.168.3.0/28"),
                new Network("192.168.3.16/32")
        ), listIPv4);
    }

    @Test
    public void testRangeFromWithIPv4FiveNetworks() {
        List<Network<IPv4>> listIPv4 = Network.rangeFrom(new IPv4("192.168.1.32"), new IPv4("192.168.2.32"));
        assertEquals(Arrays.asList(
                new Network("192.168.1.32/27"),
                new Network("192.168.1.64/26"),
                new Network("192.168.1.128/25"),
                new Network("192.168.2.0/27"),
                new Network("192.168.2.32/32")
        ), listIPv4);
    }

    @Test
    public void testRangeFromWithIPv4Slash31() {
        List<Network<IPv4>> listIPv4 = Network.rangeFrom(new IPv4("255.255.255.254"), new IPv4("255.255.255.255"));
        assertEquals(Collections.singletonList(
                new Network("255.255.255.254/31")
        ), listIPv4);
    }

    @Test
    public void testRangeFromWithIPv4Slash1to32() {
        List<Network<IPv4>> listIPv4 = Network.rangeFrom(new IPv4("0.0.0.0"), new IPv4("255.255.255.254"));
        assertEquals(Arrays.asList(
                new Network("0.0.0.0/1"),
                new Network("128.0.0.0/2"),
                new Network("192.0.0.0/3"),
                new Network("224.0.0.0/4"),
                new Network("240.0.0.0/5"),
                new Network("248.0.0.0/6"),
                new Network("252.0.0.0/7"),
                new Network("254.0.0.0/8"),
                new Network("255.0.0.0/9"),
                new Network("255.128.0.0/10"),
                new Network("255.192.0.0/11"),
                new Network("255.224.0.0/12"),
                new Network("255.240.0.0/13"),
                new Network("255.248.0.0/14"),
                new Network("255.252.0.0/15"),
                new Network("255.254.0.0/16"),
                new Network("255.255.0.0/17"),
                new Network("255.255.128.0/18"),
                new Network("255.255.192.0/19"),
                new Network("255.255.224.0/20"),
                new Network("255.255.240.0/21"),
                new Network("255.255.248.0/22"),
                new Network("255.255.252.0/23"),
                new Network("255.255.254.0/24"),
                new Network("255.255.255.0/25"),
                new Network("255.255.255.128/26"),
                new Network("255.255.255.192/27"),
                new Network("255.255.255.224/28"),
                new Network("255.255.255.240/29"),
                new Network("255.255.255.248/30"),
                new Network("255.255.255.252/31"),
                new Network("255.255.255.254/32")
        ), listIPv4);
    }

    @Test
    public void testRangeFromWithIPv4SlashOne() {
        List<Network<IPv4>> listIPv4 = Network.rangeFrom(new IPv4("0.0.0.0"), new IPv4("127.255.255.255"));
        assertEquals(Collections.singletonList(
                new Network("0.0.0.0/1")
        ), listIPv4);
    }

    @Test
    public void testRangeFromWithIPv4HostRoute() {
        List<Network<IPv4>> listIPv4 = Network.rangeFrom(new IPv4("1.1.1.32"), new IPv4("1.1.1.32"));
        assertEquals(Collections.singletonList(
                new Network("1.1.1.32/32")
        ), listIPv4);
    }

    @Test
    public void testRangeFromWithIPv4BroadcastIP() {
        List<Network<IPv4>> listIPv4 = Network.rangeFrom(new IPv4("255.255.255.255"), new IPv4("255.255.255.255"));
        assertEquals(Collections.singletonList(
                new Network("255.255.255.255/32")
        ), listIPv4);
    }

    @Test
    public void testRangeFromWithIPv4EveryIP() {
        List<Network<IPv4>> listIPv4 = Network.rangeFrom(new IPv4("0.0.0.0"), new IPv4("255.255.255.255"));
        assertEquals(Collections.singletonList(
                new Network("0.0.0.0/0")
        ), listIPv4);
    }

    @Test
    public void testRangeFromWithIPv6First() {
        List<Network<IPv6>> listIPv6 = Network.rangeFrom(new IPv6("2001:08d8:01fe:0001:0000:0000:0000:0000"),
                new IPv6("2001:08d8:01fe:0001:ffff:ffff:ffff:ffff"));
        assertEquals(Collections.singletonList(new Network("2001:08d8:01fe:0001:0000:0000:0000:0000/64")), listIPv6);
    }

    @Test
    public void testRangeFromWithIPv6Second() {
        List<Network<IPv6>> listIPv6 = Network.rangeFrom(new IPv6("2001:08d8:01fe:0000:0000:0000:0000:0000"),
                new IPv6("2001:08d8:01fe:0001:ffff:ffff:ffff:ffff"));
        assertEquals(Collections.singletonList(new Network("2001:08d8:01fe:0000:0000:0000:0000:0000/63")), listIPv6);
    }

    @Test
    public void testRangeFromWithIPv6AndMultipleResults() {
        List<Network<IPv6>> listIPv6 = Network.rangeFrom(new IPv6("2001:08d8:01fe:0000:0000:0000:0000:0020"),
                new IPv6("2001:08d8:01fe:0000:0000:0000:0001:001f"));
        assertEquals(Arrays.asList(
                new Network("2001:08d8:01fe:0000:0000:0000:0000:0020/123"),
                new Network("2001:08d8:01fe:0000:0000:0000:0000:0040/122"),
                new Network("2001:08d8:01fe:0000:0000:0000:0000:0080/121"),
                new Network("2001:08d8:01fe:0000:0000:0000:0000:0100/120"),
                new Network("2001:08d8:01fe:0000:0000:0000:0000:0200/119"),
                new Network("2001:08d8:01fe:0000:0000:0000:0000:0400/118"),
                new Network("2001:08d8:01fe:0000:0000:0000:0000:0800/117"),
                new Network("2001:08d8:01fe:0000:0000:0000:0000:1000/116"),
                new Network("2001:08d8:01fe:0000:0000:0000:0000:2000/115"),
                new Network("2001:08d8:01fe:0000:0000:0000:0000:4000/114"),
                new Network("2001:08d8:01fe:0000:0000:0000:0000:8000/113"),
                new Network("2001:08d8:01fe:0000:0000:0000:0001:0000/123")
        ), listIPv6);
    }

    @Test
    public void testMergeContaining() {
        Set<Network<IPv4>> set = Network.mergeContaining(
                Arrays.asList(
                        new Network<IPv4>("172.16.0.0/31"),
                        new Network<IPv4>("172.16.0.0/32")));
        assertEquals(
                Collections.singleton(
                        new Network<>("172.16.0.0/31")),
                set);

        set = Network.mergeContaining(
                Arrays.asList(
                        new Network<>("172.16.0.0/31"),
                new Network<IPv4>("172.16.0.0/32"),
                new Network<IPv4>("172.16.0.0/24")));
        assertEquals(
                Collections.singleton(
                        new Network<>("172.16.0.0/24")),
                set);

        set = Network.mergeContaining(
                Arrays.asList(
                        new Network<IPv4>("172.16.0.0/31"),
                        new Network<IPv4>("172.16.0.0/32"),
                        new Network<IPv4>("172.16.0.0/24"),
                        new Network<IPv4>("10.128.0.0/16"),
                        new Network<IPv4>("10.128.0.0/17")));
        assertEquals(
                new HashSet<>(
                        Arrays.asList(
                                new Network<>("172.16.0.0/24"),
                                new Network<>("10.128.0.0/16"))),
                set);

        set = Network.mergeContaining(
                Arrays.asList(
                        new Network<IPv4>("172.16.0.0/31"),
                        new Network<IPv4>("172.16.0.0/32"),
                        new Network<IPv4>("172.16.0.0/24"),
                        new Network<IPv4>("10.128.0.0/16"),
                        new Network<IPv4>("10.128.0.0/17"),
                        new Network<IPv4>("127.0.0.0/8"),
                        new Network<IPv4>("127.0.0.0/24"),
                        new Network<IPv4>("127.0.0.128/25"),
                        new Network<IPv4>("127.5.0.0/16"),
                        new Network<IPv4>("127.0.0.0/32"),
                        new Network<IPv4>("172.8.5.128/25")));
        assertEquals(
                new HashSet<>(
                        Arrays.asList(
                                new Network<>("172.16.0.0/24"),
                                new Network<>("10.128.0.0/16"),
                                new Network<>("127.0.0.0/8"),
                                new Network<>("172.8.5.128/25"))),
                set);

        set = Network.mergeContaining(
                Arrays.asList(
                        new Network<IPv4>("172.16.0.0/31"),
                        new Network<IPv4>("172.16.0.0/32"),
                        new Network<IPv4>("172.16.0.0/24"),
                        new Network<IPv4>("10.128.0.0/16"),
                        new Network<IPv4>("10.128.0.0/17"),
                        new Network<IPv4>("127.0.0.0/8"),
                        new Network<IPv4>("127.0.0.0/24"),
                        new Network<IPv4>("127.0.0.128/25"),
                        new Network<IPv4>("127.5.0.0/16"),
                        new Network<IPv4>("127.0.0.0/32"),
                        new Network<>("172.8.5.128/25"),
                        new Network<IPv4>("172.16.0.0/31")));
        assertEquals(
                new HashSet<>(
                        Arrays.asList(
                                new Network<>("172.16.0.0/24"),
                                new Network<>("10.128.0.0/16"),
                                new Network<>("127.0.0.0/8"),
                                new Network<>("172.8.5.128/25"))), set);
    }

    @Test
    public void testMergeNeighbors() {
        List<Network<IPv4>> list = Network.mergeNeighbors(
                Arrays.asList(
                        new Network<IPv4>("172.16.0.0/32"),
                        new Network<>("172.16.0.1/32")));
        assertEquals(Collections.singletonList(new Network<>("172.16.0.0/31")), list);

        list = Network.mergeNeighbors(
                Arrays.asList(
                        new Network<IPv4>("172.16.0.0/24"),
                        new Network<>("172.16.0.1/24"),
                        new Network<>("172.16.0.5/24")));
        assertEquals(
                Arrays.asList(
                        new Network<IPv4>("172.16.0.0/24")),
                list);

        list = Network.mergeNeighbors(
                Arrays.asList(
                        new Network<IPv4>("172.16.0.4/24"),
                        new Network<>("172.16.0.100/24"),
                        new Network<>("172.16.0.5/24")));
        assertEquals(
                Arrays.asList(
                        new Network<IPv4>("172.16.0.0/24")),
                list);

        list = Network.mergeNeighbors(
                Arrays.asList(
                        new Network<IPv4>("172.16.0.0/20"),
                        new Network<>("172.16.0.16/24"),
                        new Network<>("172.16.0.17/24")));
        assertEquals(
                Arrays.asList(
                        new Network<IPv4>("172.16.0.0/20")),
                list);

        list = Network.mergeNeighbors(
                Arrays.asList(
                        new Network<IPv4>("172.16.0.0/14"),
                        new Network<IPv4>("172.20.0.0/14"),
                        new Network<IPv4>("172.55.0.17/24"),
                        new Network<IPv4>("172.24.0.0/14")));
        assertEquals(
                Arrays.asList(
                        new Network<IPv4>("172.16.0.0/13"),
                        new Network<IPv4>("172.24.0.0/14"),
                        new Network<IPv4>("172.55.0.17/24")),
                list);
    }

    @Test
    public void testGetSubnetMask() {
        assertEquals(new IPv4("255.255.255.0"), Network.getSubnetMask(IPv4.class, 24));
        assertEquals(new IPv6("ffff:ff00:0:0:0:0:0:0"), Network.getSubnetMask(IPv6.class, 24));
    }

    @Test
    public void testGetInverseSubnetMask() {
        assertEquals(new IPv4("0.0.0.255"), Network.getInverseSubnetMask(IPv4.class, 24));
        assertEquals(new IPv6("0:ff:ffff:ffff:ffff:ffff:ffff:ffff"), Network.getInverseSubnetMask(IPv6.class, 24));
        assertEquals(new IPv4("255.255.255.255"), Network.getInverseSubnetMask(IPv4.class, 0));
        assertEquals(new IPv4("0.255.255.255"), Network.getInverseSubnetMask(IPv4.class, 8));
        assertEquals(new IPv4("0.0.255.255"), Network.getInverseSubnetMask(IPv4.class, 16));
        assertEquals(new IPv4("0.0.0.255"), Network.getInverseSubnetMask(IPv4.class, 24));
        assertEquals(new IPv4("0.0.0.0"), Network.getInverseSubnetMask(IPv4.class, 32));
    }

    @Test
    public void testNetworkConstructBySize() {
        Network<IPv4> network = new Network<>(new IPv4("192.168.0.0"), 24);
        assertEquals(new IPv4("192.168.0.0"), network.getAddress());
        assertEquals(24, network.getPrefix());
    }

    @Test
    public void testEqualsWithNotEqual() {
        Network<IPv4> network1 = new Network<>(new IPv4("192.168.0.0"), 24);
        Network<IPv4> network2 = new Network<>(new IPv4("172.10.0.0"), 24);
        Network<IPv4> network3 = new Network<>(new IPv4("172.10.0.0"), 25);
        assertNotEquals(network1, network2);
        assertNotEquals(network1, network3);
        assertNotEquals(null, network1);
    }

    @Test
    public void testEqualsWithEqual() {
        Network<IPv4> network1 = new Network<>(new IPv4("192.168.0.0"), 24);
        Network<IPv4> network2 = new Network<>(new IPv4("192.168.0.0"), 24);
        assertEquals(network1, network1);
        assertEquals(network1, network2);
        assertEquals(network2, network1);
    }

    @Test
    public void testGetSubnetMaskWithIllegalVersion() {
        Assertions.assertThrows(NullPointerException.class, () -> Network.getSubnetMask(null, 24));
    }

    @Test
    public void testGetInverseSubnetMaskWithIllegalVersion() {
        Assertions.assertThrows(NullPointerException.class, () -> Network.getInverseSubnetMask(null, 24));
    }

    @Test
    public void testIteratorWithIllegalRemove() {
        Assertions.assertThrows(UnsupportedOperationException.class, () -> {
            Network<IPv4> network = new Network<>(new IPv4("192.168.0.0"), 24);
            network.iterator().remove();
        });
    }

    @Test
    public void testGetters() {
        Network<IPv4> network = new Network<IPv4>(new IPv4("192.168.1.2"), 24);
        assertEquals(24, network.getPrefix());
        assertEquals(new IPv4("192.168.1.0"), network.getAddress());
        assertEquals(new IPv4("192.168.1.255"), network.getBroadcast());
        assertEquals(new IPv4("255.255.255.0"), network.getSubnetMask());
    }

    @Test
    public void testStreamWithCount() {
        Network<IPv4> network = new Network<>(new IPv4("192.168.1.0"), 24);
        assertEquals(256, network.stream().count());
    }


    @Test
    public void testStreamWithBigCount() {
        String javaVersionString = System.getProperty("java.version");

        // disabled  for Java 8-9 since this can take very long
        if (javaVersionString.matches("1\\.[4-9]\\..*")) {
            return;
        }

        Network<IPv6> network = new Network<>(new IPv6("::"), 64);
        assertEquals(Long.MAX_VALUE, network.stream().count());
    }

    @Test
    public void testStreamWithFindFirst() {
        Network<IPv4> network = new Network<>(new IPv4("192.168.1.0"), 24);
        assertEquals(Optional.of(new IPv4("192.168.1.0")), network.stream().findFirst());
    }

    private static <T extends IP<T>> List<T> toIps(Network<T> network) {
        List<T> list = new ArrayList<>();
        Iterator<T> iterator = network.iterator();
        iterator.forEachRemaining(list::add);
        return list;
    }

    @Test
    public void testStreamWithCollectSmall() {
        Network<IPv4> network = new Network<>(new IPv4("192.168.1.0"), 31);
        assertEquals(toIps(network), network.stream().collect(Collectors.toList()));
    }

    @Test
    public void testStreamWithCollect() {
        Network<IPv4> network = new Network<>(new IPv4("192.168.1.0"), 24);
        assertEquals(toIps(network), network.stream().collect(Collectors.toList()));
    }

    @Test
    public void testStreamWithParallelCollect() {
        Network<IPv4> network = new Network<>(new IPv4("192.168.1.0"), 20);
        assertEquals(new HashSet<>(toIps(network)), network.stream().parallel().collect(Collectors.toSet()));
    }

    @Test
    public void testToStringWithIPv4() {
        Network<IPv4> network = new Network<>(new IPv4("192.168.1.0"), 20);
        assertEquals("192.168.0.0/20", network.toString());
    }

    @Test
    public void testToStringWithIPv6() {
        Network<IPv6> network = new Network<>(new IPv6("::"), 20);
        assertEquals("0:0:0:0:0:0:0:0/20", network.toString());
    }

    @Test
    public void testSerialize() throws IOException {
        byte[] data;
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);) {
            objectOutputStream.writeObject(new Network("192.168.24.0/24"));
            objectOutputStream.close();
            data = byteArrayOutputStream.toByteArray();
        }

        assertNotNull(data);
        assertNotEquals(0, data.length);
    }

    @Test
    public void testDeserialize() throws IOException, ClassNotFoundException {
        try (
                InputStream inputStream = Files.newInputStream(Paths.get("src/test/resources/net_192.168.24.0_24"));
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);) {
            Network network = (Network)objectInputStream.readObject();
            assertEquals(new Network("192.168.24.0/24"), network);
        }
    }
}
