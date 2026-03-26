package io.rocketbase.asset.imgproxy;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class SignatureConfiguration {

    public static SignatureConfiguration unsigned(String baseurl) {
        return new SignatureConfiguration(baseurl, null, null);
    }

    public static SignatureConfiguration signed(String baseurl, String key, String salt) {
        return new SignatureConfiguration(baseurl, Hex.decode(requireValue(key, "key"), "key"),
                Hex.decode(requireValue(salt, "salt"), "salt"));
    }

    public static SignatureConfiguration signedRaw(String baseurl, byte[] keyBytes, byte[] saltBytes) {
        return new SignatureConfiguration(baseurl, requireBytes(keyBytes, "key"), requireBytes(saltBytes, "salt"));
    }

    private final String baseurl;

    private final byte[] keyBytes;

    private final byte[] saltBytes;

    private int numberOfSignatureBytes = 32;

    private SignatureConfiguration(String baseurl, byte[] keyBytes, byte[] saltBytes) {
        this.baseurl = baseurl;
        this.keyBytes = keyBytes == null ? null : keyBytes.clone();
        this.saltBytes = saltBytes == null ? null : saltBytes.clone();
    }

    public SignatureConfiguration numberOfSignatureBytes(int numberOfSignatureBytes) {
        if (numberOfSignatureBytes < 1 || numberOfSignatureBytes > 32) {
            throw new IllegalArgumentException("numberOfSignatureBytes must be between 1 and 32 inclusively");
        }
        this.numberOfSignatureBytes = numberOfSignatureBytes;
        return this;
    }

    public String getBaseurl() {
        return baseurl;
    }

    public int getNumberOfSignatureBytes() {
        return numberOfSignatureBytes;
    }

    byte[] getKeyBytes() {
        return keyBytes == null ? null : keyBytes.clone();
    }

    byte[] getSaltBytes() {
        return saltBytes == null ? null : saltBytes.clone();
    }

    private static String requireValue(String value, String name) {
        if (value == null) {
            throw new IllegalArgumentException(name + " must not be null");
        }
        return value;
    }

    private static byte[] requireBytes(byte[] value, String name) {
        if (value == null) {
            throw new IllegalArgumentException(name + " must not be null");
        }
        return value;
    }

}
