package com.ionos.network.commons.address;

import com.ionos.network.commons.address.IP;
import com.ionos.network.commons.address.IPVersion;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class IPVersionTest {

    @Test
    public void testGetAddressBits() {
        assertEquals(4 * 8, IPVersion.IPv4.getAddressBits());
        assertEquals(16 * 8, IPVersion.IPv6.getAddressBits());
    }

    @Test
    public void testGetAddressLength() {
        assertEquals(4, IPVersion.IPv4.getAddressBytes());
        assertEquals(16, IPVersion.IPv6.getAddressBytes());
    }

    @Test
    public void testGetMinimumAddress() {
        assertEquals(new IP("0.0.0.0"), IPVersion.IPv4.getMinimumAddress());
        assertEquals(new IP("0000:0000:0000:0000:0000:0000:0000:0000"), IPVersion.IPv6.getMinimumAddress());
    }

    @Test
    public void testGetMaximumAddress() {
        assertEquals(new IP("255.255.255.255"), IPVersion.IPv4.getMaximumAddress());
        assertEquals(new IP("ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff"), IPVersion.IPv6.getMaximumAddress());
    }
}