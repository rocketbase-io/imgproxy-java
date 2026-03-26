# imgproxy-java

![logo](assets/imgproxy-java-logo.svg)


![build](https://github.com/rocketbase-io/imgproxy-java/actions/workflows/ci.yml/badge.svg)
[![Maven Central](https://badgen.net/maven/v/maven-central/io.rocketbase.asset/imgproxy)](https://mvnrepository.com/artifact/io.rocketbase.asset/imgproxy)


fluently generate asset urls for img-proxy within java

- dependency-free at runtime
- requires Java 11+
- supports signed and unsigned imgproxy URLs
- supports official imgproxy hex key/salt configuration
- supports both encoded and `/plain/` source URLs

## example usage

````java
// unsigned
String url = Signature.of(SignatureConfiguration.unsigned(BASE_URL))
                .size(300, 300)
                .url(SOURCE_URL)
````

### configuration

````java
// official imgproxy configuration:
// key and salt are the hex-encoded values from IMGPROXY_KEY / IMGPROXY_SALT
SignatureConfiguration signedConfiguration = SignatureConfiguration.signed(
                imgproxyProperties.getBaseurl(),
                imgproxyProperties.getKey(),
                imgproxyProperties.getSalt());

// raw/plain-text fallback:
// only use this if you already have raw key/salt bytes
SignatureConfiguration rawConfiguration = SignatureConfiguration.signedRaw(
                imgproxyProperties.getBaseurl(),
                keyBytes,
                saltBytes);
````

### further usages

````java
String resizedUrl = Signature.of(signedConfiguration)
                .resize(ResizeType.fit, 300, 300, true)
                .url("s3://bucket-name/" + assetReference.getUrlPath());

// exact 75x75 thumbnail, cropped around the smart focal area
String thumbUrl = Signature.of(signedConfiguration)
                .resize(ResizeType.fill, 75, 75, false, false)
                .gravity(GravityType.sm)
                .url("s3://bucket-name/path/image.jpg", ImageType.webp);

// keep the whole image visible inside 300x300 and fill the remaining area with white
String containUrl = Signature.of(signedConfiguration)
                .resize(ResizeType.fit, 300, 300, false, true)
                .background("FFFFFF")
                .url("s3://bucket-name/path/image.jpg", ImageType.webp);

// useful if you don't want the source URL base64-encoded in the path
String plainUrl = Signature.of(signedConfiguration)
                .resize(ResizeType.fit, 1200, 800, true)
                .quality(85)
                .urlPlain("https://cdn.example.org/images/photo.jpg?version=42", ImageType.jpg);
````
