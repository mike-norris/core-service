package com.openrangelabs.services.authenticate.tools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Slf4j
@Component
public class Security {

    public String getHash(String md5Str) {
        final byte[] defaultBytes = md5Str.getBytes();
        try {
            final MessageDigest md5MsgDigest = MessageDigest.getInstance("MD5");
            md5MsgDigest.reset();
            md5MsgDigest.update(defaultBytes);
            final byte messageDigest[] = md5MsgDigest.digest();

            final StringBuffer hexString = new StringBuffer();
            for (final byte element : messageDigest) {
                final String hex = Integer.toHexString(0xFF & element);
                if (hex.length() == 1) {
                    hexString.append("0");
                }
                hexString.append(hex);
            }
            md5Str = hexString + "";
        } catch (final NoSuchAlgorithmException nsae) {
            log.error(nsae.getMessage());
        }
        return md5Str;
    }
}
