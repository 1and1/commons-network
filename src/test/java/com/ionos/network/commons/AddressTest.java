package com.ionos.network.commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AddressTest {
    @Test
    public void testComponentComparator() {
        IP ip1 = new IP("192.168.0.1");
        IP ip2 = new IP("192.168.0.2");

        assertEquals(0, Address.COMPONENT_COMPARATOR.compare(ip1, ip1));
        assertEquals(-1, Address.COMPONENT_COMPARATOR.compare(ip1, ip2));
        assertEquals(1, Address.COMPONENT_COMPARATOR.compare(ip2, ip1));
    }
}
