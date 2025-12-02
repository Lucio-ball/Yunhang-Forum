package com.yunhang.forum.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Simple security utilities for hashing passwords.
 * <p>
 * This class provides a static method to compute SHA-256 hashes of input
 * strings. In a production system, prefer a slow, salted algorithm like
 * PBKDF2, bcrypt, or Argon2. For bootstrapping and local tests, SHA-256
 * is acceptable.
 */
public final class SecurityUtil {

    private SecurityUtil() {
        // Utility class; prevent instantiation.
    }

    /**
     * Compute SHA-256 hex digest of the given input string.
     *
     * @param input the input text (e.g., a password). Must not be null.
     * @return lower-case hex-encoded SHA-256 digest
     * @throws IllegalArgumentException if input is null
     * @throws RuntimeException if SHA-256 algorithm is not available (should not happen in standard JDKs)
     */
    public static String sha256(String input) {
        if (input == null) {
            throw new IllegalArgumentException("input must not be null");
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            // Convert to hex (lower-case)
            StringBuilder sb = new StringBuilder(hashed.length * 2);
            for (byte b : hashed) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 should always be present; rethrow as unchecked
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}
