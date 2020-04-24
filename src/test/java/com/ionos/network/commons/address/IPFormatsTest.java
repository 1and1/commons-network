package com.ionos.network.commons.address;

import com.ionos.network.commons.address.AddressFormat;
import com.ionos.network.commons.address.IP;
import com.ionos.network.commons.address.IPFormats;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/** JUnit test for {@link IPFormats}.
 * @author Stephan Fuhrmann
 * */
public class IPFormatsTest {

    @Test
    public void testDOTTED_DECIMALwithIPv4AndAll0() {
        final AddressFormat<IP> formatter = IPFormats.DOTTED_DECIMAL;
        final IP ip = new IP("0.0.0.0");
        assertEquals("0.0.0.0", formatter.format(ip));
    }

    @Test
    public void testDOTTED_DECIMALwithIPv4AndAll255() {
        final AddressFormat<IP> formatter = IPFormats.DOTTED_DECIMAL;
        final IP ip = new IP("255.255.255.255");
        assertEquals("255.255.255.255", formatter.format(ip));
    }

    @Test
    public void testDOTTED_DECIMALwithIPv6AndAllZero() {
        final AddressFormat<IP> formatter = IPFormats.DOTTED_DECIMAL;
        // nobody uses this for IPv6, but there's no reason to not work
        final IP ip = new IP("::");
        assertEquals("0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0", formatter.format(ip));
    }

    @Test
    public void testCOLON_SEPARATED_ZEROED_HEXTETSwithIPv6AndMiscNumber() {
        final AddressFormat<IP> formatter = IPFormats.COLON_SEPARATED_ZEROED_HEXTETS;
        final IP ip = new IP("1234:5678:9abc:2345:3456:5678:0123:2345");
        assertEquals("1234:5678:9abc:2345:3456:5678:0123:2345", formatter.format(ip));
    }

    @Test
    public void testCOLON_SEPARATED_ZEROED_HEXTETSwithIPv6AndAllZero() {
        final AddressFormat<IP> formatter = IPFormats.COLON_SEPARATED_ZEROED_HEXTETS;
        final IP ip = new IP("::");
        assertEquals("0000:0000:0000:0000:0000:0000:0000:0000", formatter.format(ip));
    }

    @Test
    public void testRFC_5952() {
        final AddressFormat<IP> formatter = IPFormats.RFC_5952;
        // well you can use it for IPv4
        assertEquals("::", formatter.format(new IP("0.0.0.0")));
        assertEquals("ffff:ffff", formatter.format(new IP("255.255.255.255")));

        // now to IPv6
        // right compression
        assertEquals("abcd:1234:ffff:1234:9999:1234::", formatter.format(new IP("abcd:1234:ffff:1234:9999:1234:0000:0000")));
        // left compression
        assertEquals("::1234:ffff:1234:9999:0", formatter.format(new IP("0000:0000:0000:1234:ffff:1234:9999:0000")));
        // no compression
        assertEquals("abcd:1234:ffff:1234:9999:1234:8000:8000", formatter.format(new IP("abcd:1234:ffff:1234:9999:1234:8000:8000")));
        // full compression
        assertEquals("::", formatter.format(new IP("0000::0000")));
        // middle compression
        assertEquals("13::5678", formatter.format(new IP("13::5678")));

        // 4.2.1 Shorten as Much as Possible
        assertEquals("2001:db8::2:1",
                formatter.format(new IP("2001:db8:0:0:0:0:2:1")));

        // 4.2.2 Handling One 16-Bit 0 Field
        assertEquals("2001:db8:0:1:1:1:1:1",
                formatter.format(new IP("2001:db8:0:1:1:1:1:1")));

        // 4.2.3 Choice in Placement of "::"
        assertEquals("2001:db8::1:0:0:1",
                formatter.format(new IP("2001:db8:0:0:1:0:0:1")));
    }

    @Test
    public void testCOLON_SEPARATED_ZEROED_HEXTETS() {
        final AddressFormat<IP> formatter = IPFormats.COLON_SEPARATED_ZEROED_HEXTETS;
        assertEquals(new IP("2001:7f8::9be8:0:1"), new IP("2001:07f8:0000:0000:0000:9be8:0000:0001"));

        //ipv6
        assertEquals("2001:07f8:0000:0000:0000:9be8:0000:0001", formatter.format(new IP("2001:7f8::9be8:0:1")));
        assertEquals("2001:07f8:0000:0000:0000:9be8:0000:0001", formatter.format(new IP("2001:7f8:0:0:0:9be8:0:1")));

        //ipv4
        assertEquals("c0a8:0101", formatter.format(new IP("192.168.1.1")));
    }

    @Test
    public void testSYSTEM_ID_NOTATION() {
        final AddressFormat<IP> formatter = IPFormats.SYSTEM_ID_NOTATION;
        assertEquals("00 10 01 00 10 01", formatter.format(new IP("1.1.1.1")));
        assertEquals("14 40 23 25 50 01", formatter.format(new IP("144.23.255.1")));
    }
}
