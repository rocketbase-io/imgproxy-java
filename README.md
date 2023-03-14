# imgproxy-java

![logo](assets/imgproxy-java-logo.svg)


![build](https://github.com/rocketbase-io/imgproxy-java/actions/workflows/ci.yml/badge.svg)
[![Maven Central](https://badgen.net/maven/v/maven-central/io.rocketbase.asset/imgproxy)](https://mvnrepository.com/artifact/io.rocketbase.asset/imgproxy)


fluently generate asset urls for img-proxy within java

## example usage

````java
// simple unsigned 
String url = Signature.of(new SignatureConfiguration(BASE_URL))
                .size(300, 300)
                .url(SOURCE_URL)


// advanced with key + salt
Signature signature = Signature.of(new SignatureConfiguration(imgproxyProperties.getBaseurl(),
                    imgproxyProperties.getKey(),
                    imgproxyProperties.getSalt()));

signature.resize(ResizeType.fit, 300, 300, true);
String url = signature.url("s3://bucket-name/" + assetReference.getUrlPath());

````
