package com.ionos.network.commons.address;

import com.ionos.network.commons.address.MAC;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
        assertEquals("6c:88:14:6f:d8:91", mac1.toString());

        final MAC mac2 = new MAC("00:00:14:6F:D8:91");
        assertEquals("00:00:14:6f:d8:91", mac2.toString());

        final MAC mac3 = new MAC("14:6F:00:00:D8:91");
        assertEquals("14:6f:00:00:d8:91", mac3.toString());

        final MAC mac4 = new MAC("14:6F:D8:91:00:00");
        assertEquals("14:6f:d8:91:00:00", mac4.toString());

        final MAC mac5 = new MAC("00:00:00:00:00:00");
        assertEquals("00:00:00:00:00:00", mac5.toString());
    }

    @Test
    public void testCreationWithIllegalLength() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new MAC(new byte[]{0});
        });
    }

    @Test
    public void testCreateWithUnknownString() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new MAC("foobar this is never a MAC");
        });
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

    @Test
    public void testSerialize() throws IOException {
        byte[] data;
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);) {
            objectOutputStream.writeObject(new MAC("11:22:33:44:55:66"));
            objectOutputStream.close();
            data = byteArrayOutputStream.toByteArray();
        }

        assertNotNull(data);
        assertNotEquals(0, data.length);
    }

    @Test
    public void testDeserialize() throws IOException, ClassNotFoundException {
        try (
                InputStream inputStream = Files.newInputStream(Paths.get("src/test/resources/mac_11_22_33_44_55_66"));
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);) {
            MAC mac = (MAC)objectInputStream.readObject();
            assertEquals(new MAC("11:22:33:44:55:66"), mac);
        }
    }
}