package com.ionos.network.commons.address;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static com.ionos.network.commons.address.MACParsers.*;

/** JUnit test for {@link MACParsers}.
 * @author Stephan Fuhrmann
 * */
public class MACParsersTest {

    @Test
    public void defaultParse() {
        final MAC mac = DEFAULT.parse("6C:88:14:6F:D8:91");
        final byte[] array = new byte[]{108, -120, 20, 111, -40, -111};

        assertArrayEquals(array, mac.getBytes());
    }

    @Test
    public void colonParse() {
        final MAC mac1 = COLON_SEPARATED_HEX_FORMAT.parse("11:22:33:44:FF:bb");
        final byte[] array1 = new byte[]{0x11, 0x22, 0x33, 0x44, (byte)0xff, (byte)0xbb};

        assertArrayEquals(array1, mac1.getBytes());
    }

    @Test
    public void colonParseWithElementTooLong() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
            COLON_SEPARATED_HEX_FORMAT.parse("111:22:33:44:FF:bb"));
    }

    @Test
    public void colonParseWithElementTooManyElements() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                COLON_SEPARATED_HEX_FORMAT.parse("00:11:22:33:44:FF:bb"));
    }

    @Test
    public void ciscoParse() {
        final MAC mac1 = CISCO_CUSTOM_FORMAT.parse("6c88.146f.d891");
        final byte[] array1 = new byte[]{108, -120, 20, 111, -40, -111};

        assertArrayEquals(array1, mac1.getBytes());

        final MAC mac2 = CISCO_CUSTOM_FORMAT.parse("0000.146f.d891");
        final byte[] array2 = new byte[]{0, 0, 20, 111, -40, -111};

        assertArrayEquals(array2, mac2.getBytes());
    }

    @Test
    public void ciscoParseWithElementTooLong() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                CISCO_CUSTOM_FORMAT.parse("0000.11111.2222"));
    }
    @Test
    public void ciscoParseWithElementTooManyElements() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                CISCO_CUSTOM_FORMAT.parse("0000.1111.2222.33333"));
    }
}
