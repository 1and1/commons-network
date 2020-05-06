package com.ionos.network.commons.address;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

/** JUnit test for {@link IPParsers}.
 * @author Stephan Fuhrmann
 * */
public class IPParsersTest {

    @Test
    public void testParseStringIPV4() {
        IP<?> ip = IPParsers.DOTTED_DECIMAL.parse("1.2.3.4");
        assertArrayEquals(new byte[]{1, 2, 3, 4}, ip.getBytes());

        // zero
        ip = IPParsers.DOTTED_DECIMAL.parse("0.0.0.0");
        assertArrayEquals(new byte[]{0, 0, 0, 0}, ip.getBytes());

        // misc example
        ip = IPParsers.DOTTED_DECIMAL.parse("172.17.16.18");
        assertArrayEquals(new byte[]{(byte) 172, 17, 16, 18}, ip.getBytes());

        // with hi bit set
        ip = IPParsers.DOTTED_DECIMAL.parse("222.211.231.213");
        assertArrayEquals(new byte[]{(byte) 222, (byte) 211, (byte) 231, (byte) 213}, ip.getBytes());

        // full house
        ip = IPParsers.DOTTED_DECIMAL.parse("255.255.255.255");
        assertArrayEquals(new byte[]{(byte) 255, (byte) 255, (byte) 255, (byte) 255}, ip.getBytes());
    }

    @Test
    public void testParseStringIPV4Errors() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            IPParsers.DOTTED_DECIMAL.parse("");
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            IPParsers.DOTTED_DECIMAL.parse("222.222.222.222.222");
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            IPParsers.DOTTED_DECIMAL.parse("256.255.255.255");
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            IPParsers.DOTTED_DECIMAL.parse("255.255.255.256");
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            IPParsers.DOTTED_DECIMAL.parse("-1.-1.-1.-1");
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            IPParsers.DOTTED_DECIMAL.parse("a.a.a.a");
        });
    }

    @Test
    public void testParseStringIPV6Type3() {
        IP<?> ip = IPParsers.RFC4291_3_FULL.parse("0000:1111:2222:3333:4444:5555:127.128.129.130");
        assertArrayEquals(new byte[]{0x00, 0x00, 0x11, 0x11, 0x22, 0x22, 0x33, 0x33, 0x44, 0x44, 0x55, 0x55, 0x7f, (byte) 0x80, (byte) 0x81, (byte) 0x82}, ip.getBytes());
        ip = IPParsers.RFC4291_3_FULL.parse("0000:1111:2222:3333:4444:5556:127.1.1.255");
        assertArrayEquals(new byte[]{0x00, 0x00, 0x11, 0x11, 0x22, 0x22, 0x33, 0x33, 0x44, 0x44, 0x55, 0x56, 0x7f, (byte) 0x01, (byte) 0x1, (byte) 0xff}, ip.getBytes());

        ip = IPParsers.RFC4291_3_COMPRESSED.parse("0000:1111::3333:4444:5556:127.1.1.255");
        assertArrayEquals(new byte[]{0x00, 0x00, 0x11, 0x11, 0x00, 0x00, 0x33, 0x33, 0x44, 0x44, 0x55, 0x56, 0x7f, (byte) 0x01, (byte) 0x1, (byte) 0xff}, ip.getBytes());

        ip = IPParsers.RFC4291_3_COMPRESSED.parse("::127.1.1.255");
        assertArrayEquals(new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x7f, (byte) 0x01, (byte) 0x1, (byte) 0xff}, ip.getBytes());

        ip = IPParsers.RFC4291_3_COMPRESSED.parse("1234::127.1.1.255");
        assertArrayEquals(new byte[]{0x12, 0x34, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x7f, (byte) 0x01, (byte) 0x1, (byte) 0xff}, ip.getBytes());

        ip = IPParsers.RFC4291_3_COMPRESSED.parse("1234::affe:127.1.1.255");
        assertArrayEquals(new byte[]{0x12, 0x34, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0xaf, (byte) 0xfe, 0x7f, (byte) 0x01, (byte) 0x1, (byte) 0xff}, ip.getBytes());

        ip = IPParsers.RFC4291_3_COMPRESSED.parse("::1111:127.1.1.255");
        assertArrayEquals(new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x11, 0x11, 0x7f, (byte) 0x01, (byte) 0x1, (byte) 0xff}, ip.getBytes());
    }

    @Test
    public void testParseStringIPV6Type3Errors() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            IPParsers.RFC4291_3_FULL.parse("0000:1111:2222:3333:4444:5555:127.128.129.256");
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            IPParsers.RFC4291_3_FULL.parse(":127.128.129.253");
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            IPParsers.RFC4291_3_FULL.parse("0000:1111:0000:1111:0000:1111:0000:127.128.129.253");
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            IPParsers.RFC4291_3_FULL.parse("hallo");
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            IPParsers.RFC4291_3_COMPRESSED.parse("1234:127.128.129.253");
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            IPParsers.RFC4291_3_COMPRESSED.parse("hallo");
        });
    }

    @Test
    public void testParseStringIPV6() {
        IP<?> ip = IPParsers.RFC4291_1.parse("0000:1111:2222:3333:4444:5555:6666:7777");
        assertArrayEquals(new byte[]{0x00, 0x00, 0x11, 0x11, 0x22, 0x22, 0x33, 0x33, 0x44, 0x44, 0x55, 0x55, 0x66, 0x66, 0x77, 0x77}, ip.getBytes());

        // zero
        ip = IPParsers.RFC4291_1.parse("0:0:0:0:0:0:0:0");
        assertArrayEquals(new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}, ip.getBytes());

        // with hi bit set ... mungwall, you kicked ass
        ip = IPParsers.RFC4291_1.parse("8000:abad:cafe:dead:f00d:dead:beef:1234");
        assertArrayEquals(new byte[]{(byte) 0x80, 0x00, (byte) 0xab, (byte) 0xad, (byte) 0xca, (byte) 0xfe, (byte) 0xde, (byte) 0xad, (byte) 0xf0, 0x0d, (byte) 0xde, (byte) 0xad, (byte) 0xbe, (byte) 0xef, 0x12, 0x34}, ip.getBytes());

        // signedness and sign extension
        ip = IPParsers.RFC4291_1.parse("f:ff:fff:ffff:3:33:333:3333");
        assertArrayEquals(new byte[]{0, (byte) 0xf, 0, (byte) 0xff, (byte) 0x0f, (byte) 0xff, (byte) 0xff, (byte) 0xff, 0, (byte) 0x3, 0, (byte) 0x33, (byte) 0x03, (byte) 0x33, (byte) 0x33, (byte) 0x33}, ip.getBytes());

        // full house
        ip = IPParsers.RFC4291_1.parse("ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff");
        assertArrayEquals(new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff}, ip.getBytes());

        // short for 0::0
        ip = IPParsers.RFC4291_2.parse("::");
        assertArrayEquals(new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}, ip.getBytes());

        // short for 1::0
        ip = IPParsers.RFC4291_2.parse("1::");
        assertArrayEquals(new byte[]{0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}, ip.getBytes());

        // short for 0::1
        ip = IPParsers.RFC4291_2.parse("::1");
        assertArrayEquals(new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01}, ip.getBytes());

        // short for 1::2
        ip = IPParsers.RFC4291_2.parse("1::2");
        assertArrayEquals(new byte[]{0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x02}, ip.getBytes());

        // short for 1:3::2
        ip = IPParsers.RFC4291_2.parse("1:3::2");
        assertArrayEquals(new byte[]{0x00, 0x01, 0x00, 0x03, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x02}, ip.getBytes());

        // short for 1::3:2
        ip = IPParsers.RFC4291_2.parse("1::3:2");
        assertArrayEquals(new byte[]{0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x03, 0x00, 0x02}, ip.getBytes());

        ip = IPParsers.RFC4291_2.parse("ffff::3:2");
        assertArrayEquals(new byte[]{(byte) 0xff, (byte) 0xff, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x03, 0x00, 0x02}, ip.getBytes());
    }

    @Test
    public void testParseStringIPV6Errors() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            IPParsers.RFC4291_1.parse("");
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            IPParsers.RFC4291_1.parse("ff:ff:ff:ff");
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            IPParsers.RFC4291_1.parse("1:1:1:1:1:1:1:q");
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            IPParsers.RFC4291_1.parse("fffff:1:1:1:1:1:1:1");
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            IPParsers.RFC4291_1.parse("1:2:3:4:5:6:7:8:9");
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            IPParsers.RFC4291_1.parse("1:::");
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            IPParsers.RFC4291_2.parse("1:::");
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            IPParsers.DEFAULT.parse(":::1");
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            IPParsers.RFC4291_1.parse(":::1");
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            IPParsers.RFC4291_2.parse(":::1");
        });


        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            IPParsers.DEFAULT.parse("1::1::1");
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            IPParsers.DEFAULT.parse("1::2:3:4:5:6:7:8");
        });
    }
}
