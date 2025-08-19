package com.canaryforge.adapter.crypto;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.canaryforge.application.port.out.TokenSignerPort;
import com.canaryforge.domain.entities.common.Version;
import com.canaryforge.domain.entities.token.vo.Label;
import com.canaryforge.domain.entities.token.vo.Nonce;
import com.canaryforge.domain.entities.token.vo.Scenario;
import com.canaryforge.domain.entities.token.vo.TokenClaims;
import com.canaryforge.domain.entities.token.vo.TokenType;
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
            String payloadB64 = Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(json.getBytes(StandardCharsets.UTF_8));

            byte[] mac = hmac(json.getBytes(StandardCharsets.UTF_8));
            String macB64 = Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(mac);

            return payloadB64 + "." + macB64;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public TokenClaims verify(String sig) {
        try {
            String[] parts = sig.split("\\.");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid token format");
            }

            byte[] payloadJson = Base64.getUrlDecoder().decode(parts[0]);
            byte[] providedMac = Base64.getUrlDecoder().decode(parts[1]);
            byte[] expectedMac = hmac(payloadJson);

            if (!java.security.MessageDigest.isEqual(providedMac, expectedMac)) {
                throw new SecurityException("MAC mismatch");
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> map = mapper.readValue(payloadJson, Map.class);

            String t = String.valueOf(map.get("t"));
            String lb = String.valueOf(map.get("label"));
            String sc = String.valueOf(map.get("sc"));
            int v = Integer.parseInt(String.valueOf(map.getOrDefault("v", 1)));
            String r = String.valueOf(map.get("r"));
            long exp = Long.parseLong(String.valueOf(map.get("exp")));

            return TokenClaims.of(
                    TokenType.from(t),
                    Label.of(lb),
                    Scenario.of(sc),
                    Version.of(v),
                    Nonce.of(r),
                    Instant.ofEpochSecond(exp));
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
