package com.ionos.network.commons.address;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** JUnit test for {@link HexadecimalAddressParser}.
 * @author Stephan Fuhrmann
 * */
public class HexadecimalAddressParserTest {

    @Test
    public void parseWithDotAndMacAndIntraByteSeparation() {
        AddressParser<MAC> parser = new HexadecimalAddressParser<MAC>('.', 3, adr -> new MAC(adr), 6);
        MAC actual = parser.parse("112.233.445.566");
        MAC expected = new MAC(new byte[] {0x11, 0x22, 0x33, 0x44, 0x55, 0x66});
        assertEquals(expected, actual);
    }

    @Test
    public void parseWithColonAndMac() {
        AddressParser<MAC> parser = new HexadecimalAddressParser<MAC>(':', 2, adr -> new MAC(adr), 6);
        MAC actual = parser.parse("11:22:33:44:55:66");
        MAC expected = new MAC(new byte[] {0x11, 0x22, 0x33, 0x44, 0x55, 0x66});
        assertEquals(expected, actual);
    }

    @Test
    public void parseWithWrongLengthAndMac() {
        AddressParser<MAC> parser = new HexadecimalAddressParser<MAC>(':', 2, adr -> new MAC(adr), 5);
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            parser.parse("11:22:33:44:55:66");
        });
    }

    @Test
    public void parseWithWrongSeparatorAndMac() {
        AddressParser<MAC> parser = new HexadecimalAddressParser<MAC>('*', 2, adr -> new MAC(adr), 6);
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            parser.parse("11:22:33:44:55:66");
        });
    }

    @Test
    public void parseWithWrongSeparatorinterval() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new HexadecimalAddressParser<MAC>('*', 0, adr -> new MAC(adr), 6);
        });
    }
}
