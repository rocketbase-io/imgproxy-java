package io.rocketbase.asset.imgproxy;

import java.util.Arrays;
import java.util.Base64;

final class UrlSafeBase64 {

    private static final Base64.Encoder BASE64_URL_ENCODER = Base64.getUrlEncoder().withoutPadding();

    private UrlSafeBase64() {
    }

    static String encode(byte[] bytes) {
        return BASE64_URL_ENCODER.encodeToString(bytes);
    }

    static String encode(byte[] bytes, int length) {
        return encode(Arrays.copyOf(bytes, length));
    }

}
