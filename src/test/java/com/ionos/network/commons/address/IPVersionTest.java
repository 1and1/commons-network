package com.ionos.network.commons.address;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class IPVersionTest {

    @Test
    public void testGetAddressBits() {
        assertEquals(4 * 8, IPVersion.IPV4.getAddressBits());
        assertEquals(16 * 8, IPVersion.IPV6.getAddressBits());
    }

    @Test
    public void testGetAddressLength() {
        assertEquals(4, IPVersion.IPV4.getAddressBytes());
        assertEquals(16, IPVersion.IPV6.getAddressBytes());
    }
}