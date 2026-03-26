package io.rocketbase.asset.imgproxy;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class SignatureConfiguration {

    private final String baseurl;

    private final String key;

    private final String salt;

    private int numberOfSignatureBytes = 32;

    public SignatureConfiguration(String baseurl) {
        this.baseurl = baseurl;
        this.key = null;
        this.salt = null;
    }

    public SignatureConfiguration numberOfSignatureBytes(int numberOfSignatureBytes) {
        if (numberOfSignatureBytes < 1 || numberOfSignatureBytes > 32) {
            throw new IllegalArgumentException("numberOfSignatureBytes must be between 1 and 32 inclusively");
        }
        this.numberOfSignatureBytes = numberOfSignatureBytes;
        return this;
    }

}
