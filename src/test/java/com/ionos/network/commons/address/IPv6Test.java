package com.ionos.network.commons.address;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test cases for {@link IPv6}.
 */
public class IPv6Test {

    /** This tests the toString methods.
     * @see IPFormats#DOTTED_DECIMAL default for IPv4
     * @see IPFormats#COLON_SEPARATED_HEXTETS default for IPv6
     *  */
    @Test
    public void testToString() {
        assertEquals("abcd:1234:ffff:1234:9999:1234:0:0", new IPv6("abcd:1234:ffff:1234:9999:1234:0000:0000").toString().toLowerCase());
        assertEquals("abcd:1234:ffff:1234:9999:1234:8000:8000", new IPv6("abcd:1234:ffff:1234:9999:1234:8000:8000").toString().toLowerCase());
        assertEquals("0:1234:ffff:1234:9999:1234:0:0", new IPv6("0:1234:ffff:1234:9999:1234:0000:0000").toString().toLowerCase());
    }

    @Test
    public void testAddLong() {
        assertEquals(new IPv6("ff:0:0:0:1100:0:0:ff"), new IPv6("ff::ff").add(0x1100000000000000L));
    }

    @Test
    public void testCompareTo() {
        assertEquals(0, new IPv6("::").compareTo(new IPv6("0:0:0:0:0:0:0:0")));
    }

    @Test
    public void testConstructIllegalIP() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new IPv6(new byte[]{0}));
    }

    @Test
    public void testGetIPv6Version() {
        IP ip = new IPv6("2001:7f8::9be8:0:1");
        assertEquals(IPVersion.IPV6, ip.getIPVersion());
    }

    @Test
    public void testGetNullIP() {
        Assertions.assertThrows(NullPointerException.class,
                () -> new IPv6((String) null));
    }

    @Test
    public void testHashCode() {
        assertEquals(new IPv6("::").hashCode(), new IPv6("::").hashCode());
    }

    @Test
    public void testEqualsWithMatch() {
        IP ip1 = new IPv6("::");
        IP ip2 = new IPv6("::");

        assertEquals(ip1, ip2);
    }

    @Test
    public void testEqualsWithMismatch() {
        IP ip1 = new IPv6("ffff::");
        IP ip2 = new IPv6("fffe::");

        assertNotEquals(ip1, ip2);
        assertNotEquals(null, ip1);
    }

    @Test
    public void testEqualsWithMismatchIPVersion() {
        IP ip1 = new IPv4("192.168.0.1");
        IP ip2 = new IPv6("2001:7f8::9be8:0:1");

        assertNotEquals(ip1, ip2);
        assertNotEquals(ip2, ip1);
    }

    @Test
    public void testEqualsWithOtherClass() {
        IP ip1 = new IPv6("fffe::");

        assertNotEquals("foobar", ip1);
    }

    @Test
    public void testCompareIpv4ToIpv6() {
        IP ip1 = new IPv4("192.168.0.1");
        IP ip2 = new IPv6("2001:7f8::9be8:0:1");

        assertEquals(1, ip1.compareTo(ip2));
        assertEquals(-1, ip2.compareTo(ip1));
    }

    @Test
    public void testToInetAddress() {
        IP ip1 = new IPv6("fffe::");
        InetAddress inetAddress = ip1.toInetAddress();

        assertArrayEquals(inetAddress.getAddress(), ip1.getBytes());
    }

    @Test
    public void testSerialize() throws IOException {
        byte[] data;
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(new IPv6("fffe::"));
            objectOutputStream.close();
            data = byteArrayOutputStream.toByteArray();
        }

        assertNotNull(data);
        assertNotEquals(0, data.length);
    }

    @Test
    public void testDeserialize() throws IOException, ClassNotFoundException {
        try (
                InputStream inputStream = Files.newInputStream(Paths.get("src/test/resources/ipv6_fffe"));
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)) {
            IP ip = (IP)objectInputStream.readObject();
            assertEquals(new IPv6("fffe::"), ip);
        }
    }
}
