package com.ionos.network.commons;

import org.junit.Test;

import static org.junit.Assert.*;

public class MACTest {

    @Test
    public void testCreationByString() {
        final MAC mac = new MAC("6C:88:14:6F:D8:91");
        final byte[] array = new byte[]{108, -120, 20, 111, -40, -111};

        assertArrayEquals(array, mac.getBytes());
    }

    @Test
    public void testCreationByCiscoString() {
        final MAC mac1 = new MAC("6c88.146f.d891");
        final byte[] array1 = new byte[]{108, -120, 20, 111, -40, -111};

        assertArrayEquals(array1, mac1.getBytes());

        final MAC mac2 = new MAC("0000.146f.d891");
        final byte[] array2 = new byte[]{0, 0, 20, 111, -40, -111};

        assertArrayEquals(array2, mac2.getBytes());
    }

    @Test
    public void testCreationByArray() {
        final byte[] array = new byte[]{0, 0, 0, 0, 0, 0};
        final MAC mac = new MAC(array);

        assertArrayEquals(array, mac.getBytes());
    }

    @Test
    public void testToString() {
        final MAC mac1 = new MAC("6C:88:14:6F:D8:91");
        assertEquals("6C:88:14:6F:D8:91", mac1.toString());

        final MAC mac2 = new MAC("00:00:14:6F:D8:91");
        assertEquals("00:00:14:6F:D8:91", mac2.toString());

        final MAC mac3 = new MAC("14:6F:00:00:D8:91");
        assertEquals("14:6F:00:00:D8:91", mac3.toString());

        final MAC mac4 = new MAC("14:6F:D8:91:00:00");
        assertEquals("14:6F:D8:91:00:00", mac4.toString());

        final MAC mac5 = new MAC("00:00:00:00:00:00");
        assertEquals("00:00:00:00:00:00", mac5.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreationWithIllegalLength() {
        new MAC(new byte[]{0});
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateWithUnknownString() {
        new MAC("foobar this is never a MAC");
    }

    @Test
    public void testSameInstance() {
        final MAC mac = new MAC("6C:88:14:6F:D8:91");
        assertEquals(mac, mac);
    }

    @Test
    public void testEqualsOtherObject() {
        final MAC mac = new MAC("6C:88:14:6F:D8:91");
        assertNotEquals("hello", mac);
    }

    @Test
    public void testEquals() {
        final MAC mac1 = new MAC("6C:88:14:6F:D8:91");
        final MAC mac2 = new MAC("6C:88:14:6F:D8:91");
        assertEquals(mac1, mac2);
    }

    @Test
    public void testNotEquals() {
        final MAC mac1 = new MAC("6C:88:14:6F:D8:91");
        final MAC mac2 = new MAC("6C:88:23:6F:D8:91");
        assertNotEquals(mac1, mac2);
    }
}