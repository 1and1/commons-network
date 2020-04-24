package com.ionos.network.commons;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

/** JUnit test for {@link MACFormats}.
 * @author Stephan Fuhrmann
 * */
public class MACParserTest {
	
    @Test
    public void testCreationByString() {
        final MAC mac = MACParser.INSTANCE.parse("6C:88:14:6F:D8:91");
        final byte[] array = new byte[]{108, -120, 20, 111, -40, -111};

        assertArrayEquals(array, mac.getBytes());
    }

    @Test
    public void testCreationByCiscoString() {
        final MAC mac1 = MACParser.INSTANCE.parse("6c88.146f.d891");
        final byte[] array1 = new byte[]{108, -120, 20, 111, -40, -111};

        assertArrayEquals(array1, mac1.getBytes());

        final MAC mac2 = MACParser.INSTANCE.parse("0000.146f.d891");
        final byte[] array2 = new byte[]{0, 0, 20, 111, -40, -111};

        assertArrayEquals(array2, mac2.getBytes());
    }
}
