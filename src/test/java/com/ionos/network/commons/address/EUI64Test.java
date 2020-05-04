package com.ionos.network.commons.address;

import java.lang.reflect.Constructor;

import com.ionos.network.commons.address.EUI64;
import com.ionos.network.commons.address.IP;
import com.ionos.network.commons.address.MAC;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class EUI64Test {

    @Test
    public void testConvertMac() {
        final MAC mac = new MAC("6C:88:14:6F:D8:91");
        final IP<?> ip = EUI64.convertMac(mac);
        assertEquals(new IPv6("fe80:0:0:0:6e88:14ff:fe6f:d891"), ip);
    }

    @Test
    public void testConvertMacWithPrefix()  {
        final MAC mac = new MAC("6C:88:14:6F:D8:91");
        final IPv6 prefix = new IPv6("f380:0:ffff:0:0:0:0:0");
        final IPv6 ip = EUI64.convertMac(mac, prefix);
        assertEquals(new IPv6("f380:0:ffff:0:6e88:14ff:fe6f:d891"), ip);
    }

    @Test
    public void testConversionWithNoMac() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            final IPv6 prefix = new IPv6("f380:0:ffff:0:0:0:0:0");
            EUI64.convertMac(null, prefix);
        });
    }

    @Test
    public void testConversionWithNoPrefix() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            final MAC mac = new MAC("6C:88:14:6F:D8:91");
            EUI64.convertMac(mac, null);
        });
    }

    @Test
    public void testConversionWithIPv4Prefix() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            final MAC mac = new MAC("6C:88:14:6F:D8:91");
            final IPv6 prefix = new IPv6("127.0.0.1");
            EUI64.convertMac(mac, prefix);
        });
    }

    @Test
    public void accessPrivateConstructor() throws Exception {
        final Constructor<EUI64> constructor = EUI64.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        EUI64 eui64 = constructor.newInstance();
        assertNotNull(eui64);
    }
}