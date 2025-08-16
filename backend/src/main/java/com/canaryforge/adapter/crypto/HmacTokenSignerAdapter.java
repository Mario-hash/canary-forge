package com.canaryforge.adapter.crypto;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.canaryforge.application.port.out.TokenSignerPort;
import com.canaryforge.domain.token.TokenPayload;
import com.canaryforge.infrastructure.secrets.SecretsConfig;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HmacTokenSignerAdapter implements TokenSignerPort {

    private final byte[] secret;
    private final ObjectMapper mapper = new ObjectMapper();

    public HmacTokenSignerAdapter(SecretsConfig cfg) {
        this.secret = cfg.secret();
    }

    @Override
    public String sign(Map<String, Object> payload) {
        try {
            String json = mapper.writeValueAsString(payload);
            String payloadB64 = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(json.getBytes(StandardCharsets.UTF_8));
            byte[] mac = hmac(json.getBytes(StandardCharsets.UTF_8));
            String macB64 = Base64.getUrlEncoder().withoutPadding().encodeToString(mac);
            return payloadB64 + "." + macB64;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public TokenPayload verify(String sig) {
        try {
            String[] parts = sig.split("\\.");
            if (parts.length != 2)
                throw new IllegalArgumentException("Invalid token format");
            byte[] payloadJson = Base64.getUrlDecoder().decode(parts[0]);
            byte[] providedMac = Base64.getUrlDecoder().decode(parts[1]);
            byte[] expectedMac = hmac(payloadJson);
            if (!java.security.MessageDigest.isEqual(providedMac, expectedMac))
                throw new SecurityException("MAC mismatch");
            Map map = mapper.readValue(payloadJson, Map.class);
            String t = String.valueOf(map.get("t"));
            String label = String.valueOf(map.get("label"));
            String sc = String.valueOf(map.get("sc"));
            int v = Integer.parseInt(String.valueOf(map.getOrDefault("v", 1)));
            String r = String.valueOf(map.get("r"));
            long exp = Long.parseLong(String.valueOf(map.get("exp")));
            long now = java.time.Instant.now().getEpochSecond();
            if (exp < now)
                throw new SecurityException("Token expired");
            return new com.canaryforge.domain.token.TokenPayload(t, label, sc, exp, v, r);
        } catch (RuntimeException re) {
            throw re;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] hmac(byte[] payload) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret, "HmacSHA256"));
        return mac.doFinal(payload);
    }
}
