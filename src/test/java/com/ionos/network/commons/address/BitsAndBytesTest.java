package com.ionos.network.commons.address;

import com.ionos.network.commons.address.BitsAndBytes;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BitsAndBytesTest {

    @Test
    public void newArrayOf() {
        byte[] actual = BitsAndBytes.newArrayOf(3, (byte)12);
        byte[] expected = new byte[3];
        expected[0] = (byte)12;
        expected[1] = (byte)12;
        expected[2] = (byte)12;
        assertArrayEquals(expected, actual);
    }

    @Test
    public void getLowestBitSetWithZeroArray() {
        BitsAndBytes.getLowestBitSet(new byte[0]);
    }

    @Test
    public void getLowestBitSetWithNoBitSet() {
        int actual = BitsAndBytes.getLowestBitSet(new byte[1]);
        assertEquals(-1, actual);
    }

    @Test
    public void getLowestBitSetWithLowestBitSet() {
        byte[] array = new byte[2];
        array[1] = 1;
        int actual = BitsAndBytes.getLowestBitSet(array);
        assertEquals(0, actual);
    }

    @Test
    public void getLowestBitSetWithHighestBitSet() {
        byte[] array = new byte[2];
        array[0] = (byte)0x80;
        int actual = BitsAndBytes.getLowestBitSet(array);
        assertEquals(15, actual);
    }

    @Test
    public void getLowestBitSetWithTwoBitsSet() {
        byte[] array = new byte[2];
        array[0] = (byte)0x80;
        array[1] = 1;
        int actual = BitsAndBytes.getLowestBitSet(array);
        assertEquals(0, actual);
    }

    @Test
    public void getLowestBitSetWithAllBitsSet() {
        byte[] array = new byte[2];
        array[0] = (byte)0xff;
        array[1] = (byte)0xff;
        int actual = BitsAndBytes.getLowestBitSet(array);
        assertEquals(0, actual);
    }

    @Test
    public void getHighestBitSetWithZeroArray() {
        BitsAndBytes.getHighestBitSet(new byte[0]);
    }

    @Test
    public void getHighestBitSetWithNoBitSet() {
        int actual = BitsAndBytes.getHighestBitSet(new byte[1]);
        assertEquals(-1, actual);
    }

    @Test
    public void getHighestBitSetWithLowestBitSet() {
        byte[] array = new byte[2];
        array[1] = 1;
        int actual = BitsAndBytes.getHighestBitSet(array);
        assertEquals(0, actual);
    }

    @Test
    public void getHighestBitSetWithHighestBitSet() {
        byte[] array = new byte[2];
        array[0] = (byte)0x80;
        int actual = BitsAndBytes.getHighestBitSet(array);
        assertEquals(15, actual);
    }

    @Test
    public void getHighestBitSetWithTwoBitsSet() {
        byte[] array = new byte[2];
        array[0] = (byte)0x80;
        array[1] = 1;
        int actual = BitsAndBytes.getHighestBitSet(array);
        assertEquals(15, actual);
    }

    @Test
    public void getHighestBitSetWithAllBitsSet() {
        byte[] array = new byte[2];
        array[0] = (byte)0xff;
        array[1] = (byte)0xff;
        int actual = BitsAndBytes.getHighestBitSet(array);
        assertEquals(15, actual);
    }
}
