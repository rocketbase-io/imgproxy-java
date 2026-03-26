package io.rocketbase.asset.imgproxy;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

final class HmacSha256 {

    private static final String HMAC_SHA_256 = "HmacSHA256";

    private HmacSha256() {
    }

    static byte[] sign(byte[] key, byte[] data) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance(HMAC_SHA_256);
        mac.init(new SecretKeySpec(key, HMAC_SHA_256));
        return mac.doFinal(data);
    }

}
