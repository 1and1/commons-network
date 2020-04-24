package com.ionos.network.commons;

import org.junit.Test;

import java.lang.reflect.Constructor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class EUI64Test {

    @Test
    public void testConvertMac() {
        final MAC mac = new MAC("6C:88:14:6F:D8:91");
        final IP ip = EUI64.convertMac(mac);
        assertEquals(new IP("fe80:0:0:0:6e88:14ff:fe6f:d891"), ip);
    }

    @Test
    public void testConvertMacWithPrefix()  {
        final MAC mac = new MAC("6C:88:14:6F:D8:91");
        final IP prefix = new IP("f380:0:ffff:0:0:0:0:0");
        final IP ip = EUI64.convertMac(mac, prefix);
        assertEquals(new IP("f380:0:ffff:0:6e88:14ff:fe6f:d891"), ip);
    }

    @Test(expected = NullPointerException.class)
    public void testConversionWithNoMac() {
        final IP prefix = new IP("f380:0:ffff:0:0:0:0:0");
        EUI64.convertMac(null, prefix);
    }

    @Test(expected = NullPointerException.class)
    public void testConversionWithNoPrefix() {
        final MAC mac = new MAC("6C:88:14:6F:D8:91");
        EUI64.convertMac(mac, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConversionWithIPv4Prefix() {
        final MAC mac = new MAC("6C:88:14:6F:D8:91");
        final IP prefix = new IP("127.0.0.1");
        EUI64.convertMac(mac, prefix);
    }

    @Test
    public void accessPrivateConstructor() throws Exception {
        final Constructor<EUI64> constructor = EUI64.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        EUI64 eui64 = constructor.newInstance();
        assertNotNull(eui64);
    }
}