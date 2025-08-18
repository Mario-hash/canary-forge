package com.canaryforge.adapter.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.junit.jupiter.api.Test;

class NetUtilTest {

    @Test
    void ipTruncFrom_null_returns_null() {
        assertNull(NetUtil.ipTruncFrom(null));
    }

    @Test
    void ipTruncFrom_ipv4_returns_slash24() throws Exception {
        InetAddress ip = InetAddress.getByName("192.168.2.45");
        InetSocketAddress addr = new InetSocketAddress(ip, 8080);

        String out = NetUtil.ipTruncFrom(addr);

        assertEquals("192.168.2.0/24", out);
    }

    @Test
    void ipTruncFrom_ipv6_full_returns_slash48_on_first_three_segments() throws Exception {

        InetAddress ip = InetAddress.getByName("2001:db8:abcd:12:0:0:0:1");
        InetSocketAddress addr = new InetSocketAddress(ip, 8080);

        String out = NetUtil.ipTruncFrom(addr);

        assertEquals("2001:db8:abcd::/48", out);
    }

    @Test
    void ipTruncFrom_ipv6_compressed_loopback_preserves_current_behavior() throws Exception {
        InetAddress ip = InetAddress.getByName("::1");
        InetSocketAddress addr = new InetSocketAddress(ip, 8080);

        String out = NetUtil.ipTruncFrom(addr);

        assertEquals("0:0:0::/48", out);
    }

    @Test
    void ipTruncFrom_ipv6_compressed_isExpandedByJre_butSlash48_isCorrect() throws Exception {
        InetAddress ip = InetAddress.getByName("2001:db8::1");
        InetSocketAddress addr = new InetSocketAddress(ip, 8080);

        String out = NetUtil.ipTruncFrom(addr);

        assertEquals("2001:db8:0::/48", out);
    }
}
