package com.canaryforge.adapter.web.util;

import java.net.InetSocketAddress;

public final class NetUtil {
    private NetUtil() {
    }

    public static String ipTruncFrom(InetSocketAddress addr) {
        if (addr == null)
            return null;
        String host = addr.getAddress().getHostAddress();
        if (host.contains(".")) { // IPv4 → /24
            String[] p = host.split("\\.");
            if (p.length == 4)
                return p[0] + "." + p[1] + "." + p[2] + ".0/24";
        }
        // IPv6 simplificada → /48
        int idx = host.indexOf(":");
        if (idx > 0) {
            String[] seg = host.split(":");
            String a = seg.length > 0 ? seg[0] : "0";
            String b = seg.length > 1 ? seg[1] : "0";
            String c = seg.length > 2 ? seg[2] : "0";
            return a + ":" + b + ":" + c + "::/48";
        }
        return host;
    }
}