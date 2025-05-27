package com.decade.doj.user.utils;

import com.decade.doj.common.exception.UnauthorizedException;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;


@Component
public class AESTool {

    private final String AES_ECB = "AES/ECB/PKCS5Padding";

    private final int KEY_SIZE = 128; // 128-bit key size
    private final int NUM_KEYS = 8; // Number of random keys

    private List<byte[]> candidateKeys = new ArrayList<>();

    @PostConstruct
    public void init() {
        String[] keys = {
                "6A2B5E1D9F6C2A7E",
                "3D5B6F4E9C7A1B2C",
                "4E8A3D7B2F9C1D5A",
                "7C2B4D1E6F9A5E8C",
                "9F1A4B7E3C5D6B2A",
                "2D7C9B4E1A5F6E3B",
                "5A1C7E2B4F3D9C8A",
                "8C3D1E7B6F5A9B2E"
        };
        for (String key : keys) {
            candidateKeys.add(key.getBytes(StandardCharsets.UTF_8));
        }
    }

    public int fnv1aHash(String str) {
        final int FNV_prime = 0x01000193;
        final int FNV_offset_basis = 0x811c9dc5;
        int hash = FNV_offset_basis;
        for (int i = 0; i < str.length(); i++) {
            hash ^= str.charAt(i);
            hash *= FNV_prime;
        }
        return Math.abs(hash) % NUM_KEYS;
    }

    private String _encode(String data) {
        int index = fnv1aHash(data);
        byte[] key = candidateKeys.get(index);
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        try {
            Cipher cipher = Cipher.getInstance(AES_ECB);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            byte[] ret = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(ret);
        } catch (Exception e) {
            throw new UnauthorizedException("Failed to initialize AES cipher", e);
        }
    }

    public boolean match(String raw, String encoded) {
        String ret = _encode(raw);
        return ret.equals(encoded);
    }

    public String encode(String data, int index) {
        byte[] key = candidateKeys.get(index);
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        try {
            Cipher cipher = Cipher.getInstance(AES_ECB);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            byte[] ret = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(ret);
        } catch (Exception e) {
            throw new UnauthorizedException("Failed to initialize AES cipher", e);
        }
    }

    public String decode(String data) {
        int index = NUM_KEYS - 1;
        byte[] key = candidateKeys.get(index);
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        try {
            Cipher cipher = Cipher.getInstance(AES_ECB);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            byte[] ret = cipher.doFinal(Base64.getDecoder().decode(data));
            return new String(ret, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new UnauthorizedException("Failed to initialize AES cipher", e);
        }
    }

}
