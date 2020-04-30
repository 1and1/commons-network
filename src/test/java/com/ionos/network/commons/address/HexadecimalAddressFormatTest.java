package com.ionos.network.commons.address;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** JUnit test for {@link HexadecimalAddressFormat}.
 * @author Stephan Fuhrmann
 * */
public class HexadecimalAddressFormatTest {
    @Test
    public void formatWithTwoSeparatorInterval() {
        String actual = new HexadecimalAddressFormat<IP>(':', 2)
                .format(new IPv4("172.20.0.1"));
        assertEquals("ac:14:00:01", actual);
    }

    @Test
    public void formatWithOneSeparatorInterval() {
        String actual = new HexadecimalAddressFormat<IP>(':', 1)
                .format(new IPv4("172.20.0.1"));
        assertEquals("a:c:1:4:0:0:0:1", actual);
    }

    @Test
    public void formatWithZeroSeparatorInterval() {
        assertThrows(IllegalArgumentException.class, () -> {
                new HexadecimalAddressFormat<IP>(':', 0)
                .format(new IPv4("172.20.0.1"));
        });
    }

    @Test
    public void formatWithDifferentAddressClass() {
        Address address = new Address() {
            @Override
            public byte[] getBytes() {
                return new byte[] {1,2,3,4};
            }
        };
        String actual = new HexadecimalAddressFormat<Address>(':', 1)
                .format(address);
        assertEquals("0:1:0:2:0:3:0:4", actual);
    }
}
