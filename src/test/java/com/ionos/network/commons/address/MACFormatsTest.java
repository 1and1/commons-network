package com.ionos.network.commons.address;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import static com.ionos.network.commons.address.MACFormats.*;

/** JUnit test for {@link MACFormats}.
 * @author Stephan Fuhrmann
 * */
public class MACFormatsTest {

    private static final MAC MAC1 = new MAC("6C:88:14:6F:D8:91");

    private static final MAC MAC2 = new MAC("00:00:14:6F:D8:91");

    private static final MAC MAC3 = new MAC("14:6F:00:00:D8:91");

    private static final MAC MAC4 = new MAC("14:6F:D8:91:00:00");

    private static final MAC MAC5 = new MAC("00:00:00:00:00:00");

    @Test
    public void testCOLON_SPARATED_HEX_FORMAT() {
        assertEquals("6c:88:14:6f:d8:91",
                COLON_SEPARATED_HEX_FORMAT.format(MAC1));

        assertEquals("00:00:14:6f:d8:91",
                COLON_SEPARATED_HEX_FORMAT.format(MAC2));

        assertEquals("14:6f:00:00:d8:91",
                COLON_SEPARATED_HEX_FORMAT.format(MAC3));

        assertEquals("14:6f:d8:91:00:00",
                COLON_SEPARATED_HEX_FORMAT.format(MAC4));

        assertEquals("00:00:00:00:00:00",
                COLON_SEPARATED_HEX_FORMAT.format(MAC5));
    }

    @Test
    public void testCISCO_CUSTOM_FORMAT() {
        assertEquals("6c88.146f.d891",
                CISCO_CUSTOM_FORMAT.format(MAC1));

        assertEquals("0000.146f.d891",
                CISCO_CUSTOM_FORMAT.format(MAC2));

        assertEquals("146f.0000.d891",
                CISCO_CUSTOM_FORMAT.format(MAC3));

        assertEquals("146f.d891.0000",
                CISCO_CUSTOM_FORMAT.format(MAC4));

        assertEquals("0000.0000.0000",
                CISCO_CUSTOM_FORMAT.format(MAC5));
    }
}
