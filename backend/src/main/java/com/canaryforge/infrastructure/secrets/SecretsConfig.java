package com.canaryforge.infrastructure.secrets;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class SecretsConfig implements InitializingBean {
  @Value("${CF_SECRET:}")
  private String secretB64;

  private byte[] secret;

  /** Devuelve el secreto HMAC ya decodificado en bytes */
  public byte[] secret() {
    return secret;
  }

  @Override
  public void afterPropertiesSet() {
    if (secretB64 == null || secretB64.isBlank()) {
      throw new IllegalStateException("CF_SECRET is required. Generate with: openssl rand -base64 32");
    }
    try {
      secret = Base64.getDecoder().decode(secretB64);
      if (secret.length < 32) {
        throw new IllegalStateException("CF_SECRET must be at least 32 bytes (after base64 decode)");
      }
    } catch (IllegalArgumentException e) {
      throw new IllegalStateException("CF_SECRET must be base64-encoded", e);
    }
  }
}
