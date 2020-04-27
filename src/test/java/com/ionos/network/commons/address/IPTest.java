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
 * Test cases for {@link IP}.
 */
public class IPTest {

    /** This tests the toString methods.
     * @see IPFormats#DOTTED_DECIMAL default for IPv4
     * @see IPFormats#COLON_SEPARATED_HEXTETS default for IPv6
     *  */
    @Test
    public void testToString() {
        assertEquals("0.0.0.0", new IP("0.0.0.0").toString());
        assertEquals("255.255.255.255", new IP("255.255.255.255").toString());
        assertEquals("172.20.64.1", new IP("172.20.64.1").toString());
        assertEquals("192.168.2.15", new IP("192.168.2.15").toString());

        assertEquals("abcd:1234:ffff:1234:9999:1234:0:0", new IP("abcd:1234:ffff:1234:9999:1234:0000:0000").toString().toLowerCase());
        assertEquals("abcd:1234:ffff:1234:9999:1234:8000:8000", new IP("abcd:1234:ffff:1234:9999:1234:8000:8000").toString().toLowerCase());
        assertEquals("0:1234:ffff:1234:9999:1234:0:0", new IP("0:1234:ffff:1234:9999:1234:0000:0000").toString().toLowerCase());
    }

    @Test
    public void testAddArray() {
        assertEquals(new IP("192.168.1.1"), new IP("192.168.1.1").add(new byte[]{}));
        assertEquals(new IP("192.168.1.2"), new IP("192.168.1.1").add(new byte[]{1}));
        assertEquals(new IP("192.168.1.3"), new IP("192.168.1.2").add(new byte[]{1}));
        assertEquals(new IP("192.168.2.2"), new IP("192.168.1.2").add(new byte[]{1, 0}));
        assertEquals(new IP("193.168.1.2"), new IP("192.168.1.2").add(new byte[]{1, 0, 0, 0}));
        assertEquals(new IP("192.168.2.0"), new IP("192.168.1.255").add(new byte[]{1}));
        assertEquals(new IP("193.0.0.0"), new IP("192.255.255.255").add(new byte[]{1}));

        // 192+100=292 -> 0x124
        assertEquals(new IP("193.171.1.1"), new IP("192.168.1.2").add(new byte[]{1, 2, (byte) 255, (byte) 255}));
        assertEquals(new IP("36.168.1.2"), new IP("192.168.1.2").add(new byte[]{100, 0, 0, 0}));
    }

    @Test
    public void testAddArrayWithOverLongArray() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new IP("192.168.1.1")
                        .add(new byte[]{0, 0, 0, 0, 0, 0, 0}));
    }

    @Test
    public void testAddLong() {
        assertEquals(new IP("192.168.1.1"), new IP("192.168.1.1").add(0));
        assertEquals(new IP("192.168.1.3"), new IP("192.168.1.2").add(1));
        assertEquals(new IP("192.168.2.3"), new IP("192.168.1.2").add(0x0101));
        assertEquals(new IP("193.168.1.2"), new IP("192.168.1.2").add(0x01000000));

        // 192+100=292 -> 0x124
        assertEquals(new IP("193.171.1.1"), new IP("192.168.1.2").add(0x0102ffff));
        assertEquals(new IP("36.168.1.2"), new IP("192.168.1.2").add(0x64000000));
        //                                        0011223344556677            00
        assertEquals(new IP("ff:0:0:0:1100:0:0:ff"), new IP("ff::ff").add(0x1100000000000000L));
    }

    @Test
    public void testCompareTo() {
        assertEquals(0, new IP("192.168.1.1").compareTo(new IP("192.168.1.1")));
        assertEquals(0, new IP("::").compareTo(new IP("0:0:0:0:0:0:0:0")));

        assertEquals(-1, new IP("192.168.1.1").compareTo(new IP("192.168.1.2")));
        assertEquals(-1, new IP("192.168.1.1").compareTo(new IP("192.169.1.1")));
        assertEquals(-1, new IP("192.168.1.1").compareTo(new IP("192.169.3.1")));
        assertEquals(-1, new IP("192.168.1.1").compareTo(new IP("192.168.255.255")));
        assertEquals(-1, new IP("192.168.1.1").compareTo(new IP("255.255.255.255")));
        assertEquals(-1, new IP("192.168.1.0").compareTo(new IP("192.168.1.1")));

        assertEquals(+1, new IP("192.168.2.0").compareTo(new IP("192.168.1.1")));
        assertEquals(+1, new IP("192.168.255.0").compareTo(new IP("192.168.1.1")));
        assertEquals(+1, new IP("255.255.255.255").compareTo(new IP("192.168.1.1")));
        assertEquals(+1, new IP("255.255.0.0").compareTo(new IP("192.168.1.1")));
    }

    @Test
    public void testConstructIllegalIP() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new IP(new byte[]{0}));
    }

    @Test
    public void testGetIPv6Version() {
        IP ip = new IP("2001:7f8::9be8:0:1");
        assertEquals(IPVersion.IPV6, ip.getIPVersion());
    }

    @Test
    public void testGetNullIP() {
        Assertions.assertThrows(NullPointerException.class,
                () -> new IP((String) null));
    }

    @Test
    public void testGetIllegalIP4ByLength() {
            Assertions.assertThrows(IllegalArgumentException.class,
                    () -> new IP("192.168.0.0.0"));
    }

    @Test
    public void testGetIllegalIP4ByBounds() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new IP("256.168.0.0"));
    }

    @Test
    public void testHashCode() {
        assertEquals(new IP("1.2.3.4").hashCode(), new IP("1.2.3.4").hashCode());
        assertNotEquals(new IP("0.0.0.0").hashCode(), new IP("1.2.3.4").hashCode());
    }

    @Test
    public void testEqualsWithMatch() {
        IP ip1 = new IP("192.168.0.1");
        IP ip2 = new IP("192.168.0.1");

        assertEquals(ip1, ip2);
    }

    @Test
    public void testEqualsWithMismatch() {
        IP ip1 = new IP("192.168.0.1");
        IP ip2 = new IP("192.168.0.2");

        assertNotEquals(ip1, ip2);
        assertNotEquals(null, ip1);
    }

    @Test
    public void testEqualsWithMismatchIPVersion() {
        IP ip1 = new IP("192.168.0.1");
        IP ip2 = new IP("2001:7f8::9be8:0:1");

        assertNotEquals(ip1, ip2);
        assertNotEquals(ip2, ip1);
    }

    @Test
    public void testEqualsWithOtherClass() {
        IP ip1 = new IP("192.168.0.1");

        assertNotEquals("foobar", ip1);
    }

    @Test
    public void testCompareIpv4ToIpv6() {
        IP ip1 = new IP("192.168.0.1");
        IP ip2 = new IP("2001:7f8::9be8:0:1");

        assertEquals(1, ip1.compareTo(ip2));
        assertEquals(-1, ip2.compareTo(ip1));
    }

    @Test
    public void testToInetAddress() {
        IP ip1 = new IP("192.168.0.1");
        InetAddress inetAddress = ip1.toInetAddress();

        assertArrayEquals(inetAddress.getAddress(), ip1.getBytes());
    }

    @Test
    public void testSerialize() throws IOException {
        byte[] data;
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(new IP("192.168.0.1"));
            objectOutputStream.close();
            data = byteArrayOutputStream.toByteArray();
        }

        assertNotNull(data);
        assertNotEquals(0, data.length);
    }

    @Test
    public void testDeserialize() throws IOException, ClassNotFoundException {
        try (
                InputStream inputStream = Files.newInputStream(Paths.get("src/test/resources/ip_192.168.0.1"));
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)) {
            IP ip = (IP)objectInputStream.readObject();
            assertEquals(new IP("192.168.0.1"), ip);
        }
    }
}
