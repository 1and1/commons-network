package com.ionos.network.commons.address;

import org.junit.jupiter.api.Test;
import static com.ionos.network.commons.address.IPFormats.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

/** JUnit test for {@link IPFormats}.
 * @author Stephan Fuhrmann
 * */
public class IPFormatsTest {

    @Test
    public void testDOTTED_DECIMALwithIPv4AndAll0() {
        final IPv4 ip = new IPv4("0.0.0.0");
        assertEquals("0.0.0.0", DOTTED_DECIMAL.format(ip));
    }

    @Test
    public void testDOTTED_DECIMALwithIPv4AndAll255() {
        final IPv4 ip = new IPv4("255.255.255.255");
        assertEquals("255.255.255.255", DOTTED_DECIMAL.format(ip));
    }

    @Test
    public void testCOLON_SEPARATED_ZEROED_HEXTETSwithIPv6AndMiscNumber() {
        final IPv6 ip = new IPv6("1234:5678:9abc:2345:3456:5678:0123:2345");
        assertEquals("1234:5678:9abc:2345:3456:5678:0123:2345",
                COLON_SEPARATED_ZEROED_HEXTETS.format(ip));
    }

    @Test
    public void testCOLON_SEPARATED_ZEROED_HEXTETSwithIPv6AndAllZero() {
        final IPv6 ip = new IPv6("::");
        assertEquals("0000:0000:0000:0000:0000:0000:0000:0000",
                COLON_SEPARATED_ZEROED_HEXTETS.format(ip));
    }

    @Test
    public void testRFC_5952() {
        // now to IPv6
        // long 0 sequence is left
        assertEquals("a::a:0:0:c",
                RFC_5952.format(new IPv6("a::a:0:0:c")));
        // long 0 sequence is right
        assertEquals("a:0:0:a::c",
                RFC_5952.format(new IPv6("a:0:0:a::c")));
        // right compression
        assertEquals("abcd:1234:ffff:1234:9999:1234::",
                RFC_5952.format(new IPv6("abcd:1234:ffff:1234:9999:1234:0000:0000")));
        // left compression
        assertEquals("::1234:ffff:1234:9999:0",
                RFC_5952.format(new IPv6("0000:0000:0000:1234:ffff:1234:9999:0000")));
        // no compression
        assertEquals("abcd:1234:ffff:1234:9999:1234:8000:8000",
                RFC_5952.format(new IPv6("abcd:1234:ffff:1234:9999:1234:8000:8000")));
        // full compression
        assertEquals("::",
                RFC_5952.format(new IPv6("0000::0000")));
        // middle compression
        assertEquals("13::5678",
                RFC_5952.format(new IPv6("13::5678")));

        // 4.2.1 Shorten as Much as Possible
        assertEquals("2001:db8::2:1",
                RFC_5952.format(new IPv6("2001:db8:0:0:0:0:2:1")));

        // 4.2.2 Handling One 16-Bit 0 Field
        assertEquals("2001:db8:0:1:1:1:1:1",
                RFC_5952.format(new IPv6("2001:db8:0:1:1:1:1:1")));

        // 4.2.3 Choice in Placement of "::"
        assertEquals("2001:db8::1:0:0:1",
                RFC_5952.format(new IPv6("2001:db8:0:0:1:0:0:1")));
    }

    @Test
    public void testCOLON_SEPARATED_ZEROED_HEXTETS() {
        assertEquals("2001:07f8:0000:0000:0000:9be8:0000:0001",
                COLON_SEPARATED_ZEROED_HEXTETS.format(new IPv6("2001:7f8::9be8:0:1")));
    }

    @Test
    public void testSYSTEM_ID_NOTATION() {
        assertEquals("00 10 01 00 10 01",
                SYSTEM_ID_NOTATION.format(new IPv4("1.1.1.1")));
        assertEquals("14 40 23 25 50 01",
                SYSTEM_ID_NOTATION.format(new IPv4("144.23.255.1")));
    }
}
