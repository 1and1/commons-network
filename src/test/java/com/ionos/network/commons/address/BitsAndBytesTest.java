package com.ionos.network.commons.address;

import com.ionos.network.commons.address.BitsAndBytes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BitsAndBytesTest {

    @Test
    public void newArrayOf() {
        byte[] actual = BitsAndBytes.newArrayOf(3, (byte) 12);
        byte[] expected = new byte[3];
        expected[0] = (byte) 12;
        expected[1] = (byte) 12;
        expected[2] = (byte) 12;
        assertArrayEquals(expected, actual);
    }

    @Test
    public void getLowestBitSetWithZeroArray() {
        int actual = BitsAndBytes.getLowestBitSet(new byte[0]);
        assertEquals(-1, actual);
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
        array[0] = (byte) 0x80;
        int actual = BitsAndBytes.getLowestBitSet(array);
        assertEquals(15, actual);
    }

    @Test
    public void getLowestBitSetWithTwoBitsSet() {
        byte[] array = new byte[2];
        array[0] = (byte) 0x80;
        array[1] = 1;
        int actual = BitsAndBytes.getLowestBitSet(array);
        assertEquals(0, actual);
    }

    @Test
    public void getLowestBitSetWithAllBitsSet() {
        byte[] array = new byte[2];
        array[0] = (byte) 0xff;
        array[1] = (byte) 0xff;
        int actual = BitsAndBytes.getLowestBitSet(array);
        assertEquals(0, actual);
    }

    @Test
    public void getHighestBitSetWithZeroArray() {
        int actual = BitsAndBytes.getHighestBitSet(new byte[0]);
        assertEquals(-1, actual);
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
        array[0] = (byte) 0x80;
        int actual = BitsAndBytes.getHighestBitSet(array);
        assertEquals(15, actual);
    }

    @Test
    public void getHighestBitSetWithTwoBitsSet() {
        byte[] array = new byte[2];
        array[0] = (byte) 0x80;
        array[1] = 1;
        int actual = BitsAndBytes.getHighestBitSet(array);
        assertEquals(15, actual);
    }

    @Test
    public void getHighestBitSetWithAllBitsSet() {
        byte[] array = new byte[2];
        array[0] = (byte) 0xff;
        array[1] = (byte) 0xff;
        int actual = BitsAndBytes.getHighestBitSet(array);
        assertEquals(15, actual);
    }

    @Test
    public void toHexDigitWith0() {
        char actual = BitsAndBytes.toHexDigit(0);
        assertEquals('0', actual);
    }

    @Test
    public void toHexDigitWith9() {
        char actual = BitsAndBytes.toHexDigit(9);
        assertEquals('9', actual);
    }

    @Test
    public void toHexDigitWith10() {
        char actual = BitsAndBytes.toHexDigit(10);
        assertEquals('a', actual);
    }

    @Test
    public void toHexDigitWith15() {
        char actual = BitsAndBytes.toHexDigit(15);
        assertEquals('f', actual);
    }

    @Test
    public void toHexDigitWithNegative() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            BitsAndBytes.toHexDigit(-1);
        });
    }

    @Test
    public void toHexDigitWith16() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            BitsAndBytes.toHexDigit(16);
        });
    }

    @Test
    public void toIntWithWith0() {
        assertEquals(0, BitsAndBytes.toInt('0'));
    }

    @Test
    public void toIntWithWith9() {
        assertEquals(9, BitsAndBytes.toInt('9'));
    }

    @Test
    public void toIntWithWitha() {
        assertEquals(10, BitsAndBytes.toInt('a'));
    }

    @Test
    public void toIntWithWithA() {
        assertEquals(10, BitsAndBytes.toInt('A'));
    }

    @Test
    public void toIntWithWithF() {
        assertEquals(15, BitsAndBytes.toInt('F'));
    }

    @Test
    public void toIntWithWithf() {
        assertEquals(15, BitsAndBytes.toInt('f'));
    }

    @Test
    public void toIntWithZ() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                BitsAndBytes.toInt('Z')
        );
    }

    @Test
    public void appendHexWith0() throws IOException {
        StringBuilder actual = new StringBuilder();
        BitsAndBytes.appendHex(actual, (byte) 0, (byte) 0);
        assertEquals("0", actual.toString());
    }

    @Test
    public void appendHexWith100() throws IOException {
        StringBuilder actual = new StringBuilder();
        BitsAndBytes.appendHex(actual, (byte) 1, (byte) 0);
        assertEquals("100", actual.toString());
    }

    @Test
    public void appendHexWith1FF() throws IOException {
        StringBuilder actual = new StringBuilder();
        BitsAndBytes.appendHex(actual, (byte) 1, (byte) 255);
        assertEquals("1ff", actual.toString());
    }

    @Test
    public void appendHexWithLeadingZerosWith0() throws IOException {
        StringBuilder actual = new StringBuilder();
        BitsAndBytes.appendHexWithLeadingZeros(actual, (byte) 0, (byte) 0);
        assertEquals("0000", actual.toString());
    }

    @Test
    public void appendHexWithLeadingZerosWith0102() throws IOException {
        StringBuilder actual = new StringBuilder();
        BitsAndBytes.appendHexWithLeadingZeros(actual, (byte) 1, (byte) 2);
        assertEquals("0102", actual.toString());
    }

    @Test
    public void appendHexWithLeadingZerosith1FF() throws IOException {
        StringBuilder actual = new StringBuilder();
        BitsAndBytes.appendHexWithLeadingZeros(actual, (byte) 1, (byte) 255);
        assertEquals("01ff", actual.toString());
    }

    @Test
    public void equalsWithMaskWithMatch() throws IOException {
        byte[] left = {1, 2, 3};
        byte[] right = {1, 2, 4};
        byte[] mask = {(byte) 0xff, (byte) 0xff, (byte) 0xf8};
        boolean result = BitsAndBytes.equalsWithMask(left, right, mask);
        assertTrue(result);
    }

    @Test
    public void equalsWithMaskWithMismatch() throws IOException {
        byte[] left = {1, 2, 3};
        byte[] right = {1, 2, 4};
        byte[] mask = {(byte) 0xff, (byte) 0xff, (byte) 0xff};
        boolean result = BitsAndBytes.equalsWithMask(left, right, mask);
        assertFalse(result);
    }

    @Test
    public void equalsWithMaskWithDifferentMaskSize() throws IOException {
        byte[] left = {1, 2, 3};
        byte[] right = {1, 2, 3};
        byte[] mask = {(byte) 0xff, (byte) 0xff};
        boolean result = BitsAndBytes.equalsWithMask(left, right, mask);
        assertFalse(result);
    }

    @Test
    public void equalsWithMaskWithDifferentRightSize() throws IOException {
        byte[] left = {1, 2, 3};
        byte[] right = {1, 2, 3, 4};
        byte[] mask = {(byte) 0xff, (byte) 0xff};
        boolean result = BitsAndBytes.equalsWithMask(left, right, mask);
        assertFalse(result);
    }

    @Test
    public void setLeadingBits() {
        byte[] data = new byte[3];
        BitsAndBytes.setLeadingBits(data, 0);
        assertArrayEquals(new byte[]{0, 0, 0}, data);
        BitsAndBytes.setLeadingBits(data, 1);
        assertArrayEquals(new byte[]{(byte)0x80, 0, 0}, data);
        BitsAndBytes.setLeadingBits(data, 2);
        assertArrayEquals(new byte[]{(byte)0xC0, 0, 0}, data);
        BitsAndBytes.setLeadingBits(data, 23);
        assertArrayEquals(new byte[]{(byte)0xFF, (byte)0xFF, (byte)0xFE}, data);
    }
}
