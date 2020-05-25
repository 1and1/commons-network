package com.ionos.network.commons.address;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test cases for {@link IP}.
 */
public class IPTest {

    @Test
    public void getIPVersionWithIPV4() {
        assertEquals(IPVersion.IPV4, IP.getIPVersion(IPv4.class));
    }

    @Test
    public void getIPVersionWithIPV6() {
        assertEquals(IPVersion.IPV6, IP.getIPVersion(IPv6.class));
    }
}
