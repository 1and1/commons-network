package com.ionos.network.commons.address;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AddressComparatorsTest {
    @Test
    public void testComponentComparatorWithIP() {
        IP ip1 = new IP("192.168.0.1");
        IP ip2 = new IP("192.168.0.2");

        assertEquals(0, AddressComparators.UNSIGNED_BYTE_COMPARATOR.compare(ip1, ip1));
        assertEquals(-1, AddressComparators.UNSIGNED_BYTE_COMPARATOR.compare(ip1, ip2));
        assertEquals(1, AddressComparators.UNSIGNED_BYTE_COMPARATOR.compare(ip2, ip1));
    }

    @Test
    public void testComponentComparatorWithOther() {
        TestAddress first = new TestAddress();

        assertEquals(0, AddressComparators.UNSIGNED_BYTE_COMPARATOR.compare(first, first));
    }

    private class TestAddress implements Address {

        @Override
        public byte[] getBytes() {
            return new byte[4];
        }
    }
}
