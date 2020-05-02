package com.ionos.network.commons.address;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.hamcrest.Matchers.*;



/**
 * JUnit test for {@linkplain MAC}.
 */
public class MACTest {

    final byte[] mac1Bytes = new byte[]{108, -120, 20, 111, -40, -111};
    final byte[] mac2Bytes = new byte[]{108, -120, 20, 111, -40, -110};
    private static final String MAC1_STRING = "6c:88:14:6f:d8:91";
    private static final String MAC2_STRING = "6c:88:14:6f:d8:92";
    private MAC mac1;
    private MAC mac2;

    @BeforeEach
    @Test
    public void init() {
        mac1 = new MAC(MAC1_STRING);
        mac2 = new MAC(MAC2_STRING);
    }

    @Test
    public void testCreationByString() {
        assertArrayEquals(mac1Bytes, new MAC(MAC1_STRING).getBytes());
    }

    @Test
    public void testCreationByString2() {
        assertArrayEquals(mac2Bytes, new MAC(MAC2_STRING).getBytes());
    }

    @Test
    public void testCreationByCiscoString() {
        assertArrayEquals(mac1Bytes, new MAC("6c88.146f.d891").getBytes());
    }

    @Test
    public void testCreationByArray() {
        assertArrayEquals(mac1Bytes, new MAC(mac1Bytes).getBytes());
        assertNotSame(mac1Bytes, new MAC(mac1Bytes).getBytes());
    }

    @Test
    public void testToString() {
        assertEquals(MAC1_STRING, mac1.toString());
    }

    @Test
    public void testCreationWithIllegalLength() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new MAC(new byte[]{0}));
    }

    @Test
    public void testCreateWithUnknownString() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new MAC("foobar this is never a MAC"));
    }

    @Test
    public void testSameInstance() {
        assertTrue(mac1.equals(mac1));
    }

    @Test
    public void testEqualsWithStringClass() {
        assertFalse(mac1.equals("foo"));
    }

    @Test
    public void testEqualsWithSameContent() {
        assertTrue(mac1.equals(new MAC(mac1.getBytes())));
    }

    @Test
    public void testEqualsWithNull() {
        assertFalse(mac1.equals(null));
    }

    @Test
    public void testEqualsWithOtherAddressClass() {
        assertFalse(mac1.equals(new Address() {
            @Override
            public byte[] getBytes() {
                return new byte[0];
            }

            @Override
            public int length() {
                return 0;
            }
        }));
    }

    @Test
    public void testSerialize() throws IOException {
        byte[] data;
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
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
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)) {
            MAC mac = (MAC)objectInputStream.readObject();
            assertEquals(new MAC("11:22:33:44:55:66"), mac);
        }
    }

    @Test
    public void testHashCodeWithSame() {
        assertEquals(mac1.hashCode(), new MAC(MAC1_STRING).hashCode());
    }

    @Test
    public void testHashCodeWithOtherMac() {
        assertNotEquals(mac1.hashCode(), mac2.hashCode());
    }

    @Test
    public void testCompareToWithSame() {
        MatcherAssert.assertThat(mac1.compareTo(mac1), equalTo(0));
    }

    @Test
    public void testCompareToWithEqual() {
        MatcherAssert.assertThat(mac1.compareTo(new MAC(MAC1_STRING)), equalTo(0));
    }

    @Test
    public void testCompareToWithSmaller() {
        MatcherAssert.assertThat(mac1.compareTo(mac2), lessThan(0));
    }

    @Test
    public void testCompareToWithGreater() {
        MatcherAssert.assertThat(mac2.compareTo(mac1), greaterThan(0));
    }
}
