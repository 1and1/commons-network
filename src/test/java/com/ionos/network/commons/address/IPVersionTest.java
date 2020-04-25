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

    @Test
    public void testGetMinimumAddress() {
        assertEquals(new IP("0.0.0.0"), IPVersion.IPV4.getMinimumAddress());
        assertEquals(new IP("0000:0000:0000:0000:0000:0000:0000:0000"), IPVersion.IPV6.getMinimumAddress());
    }

    @Test
    public void testGetMaximumAddress() {
        assertEquals(new IP("255.255.255.255"), IPVersion.IPV4.getMaximumAddress());
        assertEquals(new IP("ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff"), IPVersion.IPV6.getMaximumAddress());
    }
}