package io.rocketbase.asset.imgproxy;

import io.rocketbase.asset.imgproxy.options.GravityType;
import io.rocketbase.asset.imgproxy.options.ImageType;
import io.rocketbase.asset.imgproxy.options.ResizeType;
import io.rocketbase.asset.imgproxy.options.WatermarkPositionType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Format definition
 * The advanced URL should contain the signature, processing options, and source URL, like this:
 * <p>
 * /%signature/%processing_options/plain/%source_url@%extension
 * /%signature/%processing_options/%encoded_source_url.%extension
 * <p>
 * Example
 * Signed imgproxy URL that uses sharp preset, resizes http://example.com/images/curiosity.jpg to fill 300x400 area with smart gravity without enlarging, and then converts the image to png:
 * <p>
 * http://imgproxy.example.com/AfrOrF3gWeDA6VOlDG4TzxMv39O7MXnF4CXpKUwGqRM/preset:sharp/resize:fill:300:400:0/gravity:sm/plain/http://example.com/images/curiosity.jpg@png
 * The same URL with shortcuts will look like this:
 * <p>
 * http://imgproxy.example.com/AfrOrF3gWeDA6VOlDG4TzxMv39O7MXnF4CXpKUwGqRM/pr:sharp/rs:fill:300:400:0/g:sm/plain/http://example.com/images/curiosity.jpg@png
 * The same URL with Base64-encoded source URL will look like this:
 * <p>
 * http://imgproxy.example.com/AfrOrF3gWeDA6VOlDG4TzxMv39O7MXnF4CXpKUwGqRM/pr:sharp/rs:fill:300:400:0/g:sm/aHR0cDovL2V4YW1w/bGUuY29tL2ltYWdl/cy9jdXJpb3NpdHku/anBn.png
 */
@SuppressWarnings("WeakerAccess")
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Signature {

    public static final Pattern HEXCOLOR_PATTERN = Pattern.compile("[0-9A-Fa-f]{6}");

    private final SignatureConfiguration configuration;

    private List<String> processingOptions = new ArrayList<String>();


    public static Signature of(SignatureConfiguration configuration) {
        return new Signature(configuration);
    }

    /**
     * Meta-option that defines the width, height, enlarge, and extend. All arguments are optional and can be omited to use their default values.
     */
    public Signature size(int width, int height, boolean enlarge, boolean extend) {
        processingOptions.add(createProcessingOption("s", width, height, enlarge, extend));
        return this;
    }

    /**
     * Meta-option that defines the width, height, enlarge, and extend. All arguments are optional and can be omited to use their default values.
     */
    public Signature size(int width, int height, boolean enlarge) {
        processingOptions.add(createProcessingOption("s", width, height, enlarge));
        return this;
    }

    /**
     * Meta-option that defines the width, height, enlarge, and extend. All arguments are optional and can be omited to use their default values.
     */
    public Signature size(int width, int height) {
        processingOptions.add(createProcessingOption("s", width, height));
        return this;
    }

    /**
     * Meta-option that defines the resizing type, width, height, enlarge, and extend.
     */
    public Signature resize(ResizeType resizeType, int width, int height, boolean enlarge, boolean extend) {
        processingOptions.add(createProcessingOption("rs", resizeType, width, height, enlarge, extend));
        return this;
    }

    /**
     * Meta-option that defines the resizing type, width, height, enlarge, and extend.
     */
    public Signature resize(ResizeType resizeType, int width, int height, boolean enlarge) {
        processingOptions.add(createProcessingOption("rs", resizeType, width, height, enlarge));
        return this;
    }

    /**
     * Meta-option that defines the resizing type, width, height, enlarge, and extend.
     */
    public Signature resize(ResizeType resizeType, int width, int height) {
        processingOptions.add(createProcessingOption("rs", resizeType, width, height));
        return this;
    }

    /**
     * Defines how imgproxy will resize the source image.
     */
    public Signature resize(ResizeType resizeType) {
        processingOptions.add(createProcessingOption("rt", resizeType));
        return this;
    }

    /**
     * Defines the width of the resulting image. When set to 0, imgproxy will calculate the resulting width using the defined height and source aspect ratio.
     * Default: 0
     */
    public Signature width(int width) {
        processingOptions.add(createProcessingOption("w", width));
        return this;
    }

    /**
     * Defines the minimum width of the resulting image.
     */
    public Signature minWidth(int width) {
        processingOptions.add(createProcessingOption("mw", width));
        return this;
    }

    /**
     * Defines the height of the resulting image. When set to 0, imgproxy will calculate resulting height using the defined width and source aspect ratio.
     * Default: 0
     */
    public Signature height(int height) {
        processingOptions.add(createProcessingOption("h", height));
        return this;
    }

    /**
     * Defines the minimum height of the resulting image.
     */
    public Signature minHeight(int height) {
        processingOptions.add(createProcessingOption("mh", height));
        return this;
    }

    /**
     * When set, imgproxy will multiply the image dimensions according to these factors.
     */
    public Signature zoom(double zoom) {
        validatePositive("zoom", zoom);
        processingOptions.add(createProcessingOption("z", zoom));
        return this;
    }

    /**
     * When set, imgproxy will multiply the image dimensions according to these factors.
     */
    public Signature zoom(double zoomX, double zoomY) {
        validatePositive("zoomX", zoomX);
        validatePositive("zoomY", zoomY);
        processingOptions.add(createProcessingOption("z", zoomX, zoomY));
        return this;
    }

    /**
     * When set, imgproxy will multiply the image dimensions according to this factor for HiDPI (Retina) devices.
     * Default: true
     */
    public Signature dpr(boolean useDpr) {
        processingOptions.add(createProcessingOption("dpr", useDpr));
        return this;
    }

    /**
     * When set, imgproxy will multiply the image dimensions according to this factor for HiDPI (Retina) devices.
     */
    public Signature dpr(double dpr) {
        validatePositive("dpr", dpr);
        processingOptions.add(createProcessingOption("dpr", dpr));
        return this;
    }

    /**
     * If set to false, imgproxy will not enlarge the image if it is smaller than the given size. With any other value, imgproxy will enlarge the image.
     * Default: false
     */
    public Signature enlarge(boolean enlarge) {
        processingOptions.add(createProcessingOption("el", enlarge));
        return this;
    }

    /**
     * If set to false, imgproxy will not extend the image if the resizing result is smaller than the given size. With any other value, imgproxy will extend the image to the given size.
     * Default: false
     */
    public Signature extend(boolean extend) {
        processingOptions.add(createProcessingOption("ex", extend));
        return this;
    }

    /**
     * If set, imgproxy will extend the image and use the provided gravity to position it.
     */
    public Signature extend(boolean extend, GravityType gravityType, int offsetX, int offsetY) {
        validateExtendGravity(gravityType);
        processingOptions.add(createProcessingOption("ex", extend, gravityType.name(), offsetX, offsetY));
        return this;
    }

    /**
     * If set, imgproxy will extend the image and use the provided gravity to position it.
     */
    public Signature extend(boolean extend, GravityType gravityType, double offsetX, double offsetY) {
        validateExtendGravity(gravityType);
        processingOptions.add(createProcessingOption("ex", extend, gravityType.name(), offsetX, offsetY));
        return this;
    }

    /**
     * If set, imgproxy will extend the image and use the provided gravity to position it.
     */
    public Signature extend(boolean extend, GravityType gravityType) {
        validateExtendGravity(gravityType);
        processingOptions.add(createProcessingOption("ex", extend, gravityType.name()));
        return this;
    }

    /**
     * If set, imgproxy will extend the image and use the provided focus point gravity to position it.
     */
    public Signature extend(boolean extend, double focalX, double focalY) {
        processingOptions.add(createProcessingOption("ex", extend, "fp", focalX, focalY));
        return this;
    }

    /**
     * If set, imgproxy will extend the image to the requested aspect ratio.
     */
    public Signature extendAspectRatio(boolean extend) {
        processingOptions.add(createProcessingOption("exar", extend));
        return this;
    }

    /**
     * If set, imgproxy will extend the image to the requested aspect ratio and use the provided gravity to position it.
     */
    public Signature extendAspectRatio(boolean extend, GravityType gravityType, int offsetX, int offsetY) {
        validateExtendGravity(gravityType);
        processingOptions.add(createProcessingOption("exar", extend, gravityType.name(), offsetX, offsetY));
        return this;
    }

    /**
     * If set, imgproxy will extend the image to the requested aspect ratio and use the provided gravity to position it.
     */
    public Signature extendAspectRatio(boolean extend, GravityType gravityType, double offsetX, double offsetY) {
        validateExtendGravity(gravityType);
        processingOptions.add(createProcessingOption("exar", extend, gravityType.name(), offsetX, offsetY));
        return this;
    }

    /**
     * If set, imgproxy will extend the image to the requested aspect ratio and use the provided gravity to position it.
     */
    public Signature extendAspectRatio(boolean extend, GravityType gravityType) {
        validateExtendGravity(gravityType);
        processingOptions.add(createProcessingOption("exar", extend, gravityType.name()));
        return this;
    }

    /**
     * If set, imgproxy will extend the image to the requested aspect ratio and use the provided focus point gravity to position it.
     */
    public Signature extendAspectRatio(boolean extend, double focalX, double focalY) {
        processingOptions.add(createProcessingOption("exar", extend, "fp", focalX, focalY));
        return this;
    }

    /**
     * When imgproxy needs to cut some parts of the image, it is guided by the gravity.
     * Specify gravity offset by X and Y axes.
     */
    public Signature gravity(GravityType gravityType, int offsetX, int offsetY) {
        if (!gravityType.isFocalPointAllowed()) {
            throw new IllegalArgumentException(gravityType + " is not allowed with offset");
        }
        processingOptions.add(createProcessingOption("g", gravityType.name(), offsetX, offsetY));
        return this;
    }

    /**
     * When imgproxy needs to cut some parts of the image, it is guided by the gravity.
     * Specify gravity offset by X and Y axes.
     */
    public Signature gravity(GravityType gravityType, double offsetX, double offsetY) {
        if (!gravityType.isFocalPointAllowed()) {
            throw new IllegalArgumentException(gravityType + " is not allowed with offset");
        }
        processingOptions.add(createProcessingOption("g", gravityType.name(), offsetX, offsetY));
        return this;
    }

    /**
     * When imgproxy needs to cut some parts of the image, it is guided by the gravity.
     */
    public Signature gravity(GravityType gravityType) {
        processingOptions.add(createProcessingOption("g", gravityType.name()));
        return this;
    }

    /**
     * focus point gravity. x and y are floating point numbers between 0 and 1 that define the coordinates of the center of the resulting image. Treat 0 and 1 as right/left for x and top/bottom for y
     */
    public Signature gravity(double focalX, double focalY) {
        processingOptions.add(createProcessingOption("g", "fp", focalX, focalY));
        return this;
    }

    /**
     * Defines an area of the image to be processed (crop before resize).
     * <p>
     * width and height define the size of the area. When width or height is set to 0, imgproxy will use the full width/height of the source image.
     * gravity accepts the same values as gravity option.
     */
    public Signature crop(int width, int height, GravityType gravityType, int offsetX, int offsetY) {
        if (!gravityType.isFocalPointAllowed()) {
            throw new IllegalArgumentException(gravityType + " is not allowed with offset");
        }
        processingOptions.add(createProcessingOption("c", width, height, gravityType.name(), offsetX, offsetY));
        return this;
    }

    /**
     * Defines an area of the image to be processed (crop before resize).
     * <p>
     * width and height define the size of the area. When width or height is set to 0, imgproxy will use the full width/height of the source image.
     * gravity accepts the same values as gravity option.
     */
    public Signature crop(int width, int height, GravityType gravityType) {
        processingOptions.add(createProcessingOption("c", width, height, gravityType.name()));
        return this;
    }

    /**
     * Defines an area of the image to be processed (crop before resize).
     * <p>
     * width and height define the size of the area. When width or height is set to 0, imgproxy will use the full width/height of the source image.
     * gravity accepts the same values as gravity option.
     */
    public Signature crop(int width, int height, double focalX, double focalY) {
        processingOptions.add(createProcessingOption("c", width, height, "fp", focalX, focalY));
        return this;
    }

    /**
     * Defines an area of the image to be processed (crop before resize).
     * <p>
     * width and height define the size of the area. When width or height is set to 0, imgproxy will use the full width/height of the source image.
     * Imgproxy will use the value of the gravity option.
     */
    public Signature crop(int width, int height) {
        processingOptions.add(createProcessingOption("c", width, height));
        return this;
    }

    /**
     * Defines an area of the image to be processed (crop before resize).
     */
    public Signature crop(double width, double height, GravityType gravityType, double offsetX, double offsetY) {
        if (!gravityType.isFocalPointAllowed()) {
            throw new IllegalArgumentException(gravityType + " is not allowed with offset");
        }
        processingOptions.add(createProcessingOption("c", width, height, gravityType.name(), offsetX, offsetY));
        return this;
    }

    /**
     * Defines an area of the image to be processed (crop before resize).
     */
    public Signature crop(double width, double height, GravityType gravityType) {
        processingOptions.add(createProcessingOption("c", width, height, gravityType.name()));
        return this;
    }

    /**
     * Defines an area of the image to be processed (crop before resize).
     */
    public Signature crop(double width, double height, double focalX, double focalY) {
        processingOptions.add(createProcessingOption("c", width, height, "fp", focalX, focalY));
        return this;
    }

    /**
     * Defines an area of the image to be processed (crop before resize).
     */
    public Signature crop(double width, double height) {
        processingOptions.add(createProcessingOption("c", width, height));
        return this;
    }

    /**
     * Redefines quality of the resulting image, percentage.
     * Default: value from the environment variable.
     */
    public Signature quality(int percentage) {
        if (percentage < 0 || percentage > 100) {
            throw new IllegalArgumentException("quality percentage must be between 0 and 100 inclusively");
        }

        processingOptions.add(createProcessingOption("q", percentage));
        return this;
    }

    /**
     * Removes surrounding background.
     */
    public Signature trim(int threshold, String color, boolean equalHor, boolean equalVer) {
        validateHexColor(color);
        processingOptions.add(createProcessingOption("t", threshold, color, equalHor, equalVer));
        return this;
    }

    /**
     * Removes surrounding background.
     */
    public Signature trim(int threshold, String color) {
        validateHexColor(color);
        processingOptions.add(createProcessingOption("t", threshold, color));
        return this;
    }

    /**
     * Removes surrounding background.
     */
    public Signature trim(int threshold, boolean equalHor, boolean equalVer) {
        processingOptions.add(createProcessingOption("t", threshold, equalHor, equalVer));
        return this;
    }

    /**
     * Removes surrounding background.
     */
    public Signature trim(int threshold) {
        processingOptions.add(createProcessingOption("t", threshold));
        return this;
    }

    /**
     * Defines padding size using CSS-style syntax.
     */
    public Signature padding(int top, int right, int bottom, int left) {
        processingOptions.add(createProcessingOption("pd", top, right, bottom, left));
        return this;
    }

    /**
     * Defines padding size using CSS-style syntax.
     */
    public Signature padding(int top, int right, int bottom) {
        processingOptions.add(createProcessingOption("pd", top, right, bottom));
        return this;
    }

    /**
     * Defines padding size using CSS-style syntax.
     */
    public Signature padding(int top, int right) {
        processingOptions.add(createProcessingOption("pd", top, right));
        return this;
    }

    /**
     * Defines padding size using CSS-style syntax.
     */
    public Signature padding(int top) {
        processingOptions.add(createProcessingOption("pd", top));
        return this;
    }

    /**
     * Automatically rotates images based on EXIF orientation.
     */
    public Signature autoRotate(boolean autoRotate) {
        processingOptions.add(createProcessingOption("ar", autoRotate));
        return this;
    }

    /**
     * Rotates the image on the specified angle.
     */
    public Signature rotate(int angle) {
        validateRightAngle(angle);
        processingOptions.add(createProcessingOption("rot", angle));
        return this;
    }

    /**
     * Flips the image along the specified axes.
     */
    public Signature flip(boolean horizontal, boolean vertical) {
        processingOptions.add(createProcessingOption("fl", horizontal, vertical));
        return this;
    }

    /**
     * When set, imgproxy will fill the resulting image background with the specified color. R, G, and B are red, green and blue channel values of the background color (0-255).
     * Useful when you convert an image with alpha-channel to JPEG.
     * Default: disabled
     */
    public Signature background(int r, int g, int b) {
        if (!isByte(r) || !isByte(g) || !isByte(b)) {
            throw new IllegalArgumentException("r,g and b values must be between 0 and 255 inclusively");
        }
        processingOptions.add(createProcessingOption("bg", r, g, b));
        return this;
    }

    /**
     * Disables background manipulations.
     */
    public Signature background() {
        processingOptions.add(createProcessingOption("bg"));
        return this;
    }

    /**
     * When set, imgproxy will fill the resulting image background with the specified color.
     * hex_color is a hex-coded value of the color. Useful when you convert an image with alpha-channel to JPEG.
     * Default: disabled
     */
    public Signature background(String hexColor) {
        validateHexColor(hexColor);
        processingOptions.add(createProcessingOption("bg", hexColor));
        return this;
    }

    public Signature blur(int sigma) {
        processingOptions.add(createProcessingOption("bl", sigma));
        return this;
    }

    public Signature sharpen(int sigma) {
        processingOptions.add(createProcessingOption("sh", sigma));
        return this;
    }

    public Signature pixelate(int size) {
        processingOptions.add(createProcessingOption("pix", size));
        return this;
    }

    /**
     * Puts watermark on the processed image
     */
    public Signature watermark(double opacity, WatermarkPositionType position, int offsetX, int offsetY, double scale) {
        processingOptions.add(createProcessingOption("wm", opacity, position.name(), offsetX, offsetY, scale));
        return this;
    }

    /**
     * Puts watermark on the processed image
     */
    public Signature watermark(double opacity, WatermarkPositionType position, double offsetX, double offsetY, double scale) {
        processingOptions.add(createProcessingOption("wm", opacity, position.name(), offsetX, offsetY, scale));
        return this;
    }

    /**
     * Puts watermark on the processed image
     */
    public Signature watermark(double opacity, WatermarkPositionType position, int offsetX, int offsetY) {
        processingOptions.add(createProcessingOption("wm", opacity, position.name(), offsetX, offsetY));
        return this;
    }

    /**
     * Puts watermark on the processed image
     */
    public Signature watermark(double opacity, WatermarkPositionType position, double offsetX, double offsetY) {
        processingOptions.add(createProcessingOption("wm", opacity, position.name(), offsetX, offsetY));
        return this;
    }

    /**
     * Puts watermark on the processed image
     */
    public Signature watermark(double opacity, WatermarkPositionType position) {
        processingOptions.add(createProcessingOption("wm", opacity, position.name()));
        return this;
    }

    /**
     * Puts watermark on the processed image
     */
    public Signature watermark(double opacity) {
        processingOptions.add(createProcessingOption("wm", opacity));
        return this;
    }

    /**
     * Defines a list of presets to be used by imgproxy. Feel free to use as many presets in a single URL as you need.
     */
    public Signature preset(String... presetNames) {
        processingOptions.add(createProcessingOption("pr", (Object[]) presetNames));
        return this;
    }

    /**
     * Cache buster doesn’t affect image processing but it’s changing allows to bypass CDN, proxy server and browser cache.
     * Useful when you have changed some things that are not reflected in the URL like image quality settings, presets or watermark data.
     * <p>
     * It’s highly recommended to prefer cachebuster option over URL query string because the option can be properly signed.
     */
    public Signature cachebuster(String version) {
        processingOptions.add(createProcessingOption("cb", version));
        return this;
    }

    /**
     * Defines a filename for Content-Disposition header. When not specified, imgproxy will get filename from the source url.
     */
    public Signature filename(String filename) {
        processingOptions.add(createProcessingOption("fn", filename));
        return this;
    }

    /**
     * Defines a filename for Content-Disposition header. If encoded is true, filename must be URL-safe Base64 encoded.
     */
    public Signature filename(String filename, boolean encoded) {
        if (!encoded) {
            return filename(filename);
        }
        processingOptions.add(createProcessingOption("fn", filename, true));
        return this;
    }

    /**
     * Specifies the resulting image format. Alias for extension URL part.
     */
    public Signature format(String extension) {
        processingOptions.add(createProcessingOption("f", extension));
        return this;
    }


    public String url(String sourceUrl) {
        return url(sourceUrl, null);
    }

    public String urlPlain(String sourceUrl) {
        return urlPlain(sourceUrl, null);
    }

    @SneakyThrows
    public String url(String sourceUrl, ImageType imageType) {
        String path = buildEncodedPath(sourceUrl, imageType);
        return buildSignedUrl(path);
    }

    @SneakyThrows
    public String urlPlain(String sourceUrl, ImageType imageType) {
        String path = buildPlainPath(sourceUrl, imageType);
        return buildSignedUrl(path);
    }

    static String signUrl(String path, String key, String salt, int numberOfSignatureBytes) throws NoSuchAlgorithmException, InvalidKeyException {
        if (key == null || salt == null) {
            return "notset";
        }
        if (numberOfSignatureBytes < 1 || numberOfSignatureBytes > 32) {
            throw new IllegalArgumentException("numberOfSignatureBytes must be between 1 and 32 inclusively");
        }
        byte[] digest = HmacSha256.sign(
                key.getBytes(StandardCharsets.UTF_8),
                (salt + path).getBytes(StandardCharsets.UTF_8)
        );
        return UrlSafeBase64.encode(digest, numberOfSignatureBytes);
    }


    static String createProcessingOption(String command, Object... args) {
        StringBuilder builder = new StringBuilder(command);
        if (args != null) {
            for (Object arg : args) {
                builder.append(":");
                if (arg instanceof Boolean) {
                    builder.append((Boolean) arg ? 1 : 0);
                } else {
                    builder.append(arg);
                }
            }
        }
        return builder.toString();
    }

    static boolean isHexColor(String hexColor) {
        return HEXCOLOR_PATTERN
                .matcher(hexColor)
                .matches();
    }

    static boolean isByte(int r) {
        return r >= 0 && r <= 255;
    }

    private String buildEncodedPath(String sourceUrl, ImageType imageType) {
        StringBuilder builder = createProcessingPathBuilder();
        builder.append("/")
                .append(UrlSafeBase64.encode(sourceUrl.getBytes(StandardCharsets.UTF_8)));
        if (imageType != null) {
            builder.append(".")
                    .append(imageType.name());
        }
        return builder.toString();
    }

    private String buildPlainPath(String sourceUrl, ImageType imageType) {
        StringBuilder builder = createProcessingPathBuilder();
        builder.append("/plain/")
                .append(escapePlainSourceUrl(sourceUrl));
        if (imageType != null) {
            builder.append("@")
                    .append(imageType.name());
        }
        return builder.toString();
    }

    private StringBuilder createProcessingPathBuilder() {
        StringBuilder builder = new StringBuilder();
        for (String processingOption : processingOptions) {
            builder.append("/")
                    .append(processingOption);
        }
        return builder;
    }

    private String buildSignedUrl(String path) throws NoSuchAlgorithmException, InvalidKeyException {
        String signature = signUrl(path, configuration.getKey(), configuration.getSalt(), configuration.getNumberOfSignatureBytes());
        return configuration.getBaseurl() + "/" + signature + path;
    }

    static String escapePlainSourceUrl(String sourceUrl) {
        byte[] bytes = sourceUrl.getBytes(StandardCharsets.UTF_8);
        StringBuilder builder = new StringBuilder();
        for (byte value : bytes) {
            int unsigned = value & 0xFF;
            if (isPlainUrlSafe(unsigned)) {
                builder.append((char) unsigned);
            } else {
                builder.append('%');
                builder.append(Character.toUpperCase(Character.forDigit((unsigned >> 4) & 0xF, 16)));
                builder.append(Character.toUpperCase(Character.forDigit(unsigned & 0xF, 16)));
            }
        }
        return builder.toString();
    }

    private static boolean isPlainUrlSafe(int unsigned) {
        return (unsigned >= 'a' && unsigned <= 'z')
                || (unsigned >= 'A' && unsigned <= 'Z')
                || (unsigned >= '0' && unsigned <= '9')
                || unsigned == '-'
                || unsigned == '.'
                || unsigned == '_'
                || unsigned == '~'
                || unsigned == '/'
                || unsigned == ':'
                || unsigned == '!'
                || unsigned == '$'
                || unsigned == '&'
                || unsigned == '\''
                || unsigned == '('
                || unsigned == ')'
                || unsigned == '*'
                || unsigned == '+'
                || unsigned == ','
                || unsigned == ';'
                || unsigned == '='
                || unsigned == '['
                || unsigned == ']';
    }

    private static void validateHexColor(String hexColor) {
        if (!isHexColor(hexColor)) {
            throw new IllegalArgumentException("hexcolor must be a hexadecimalencoded string for 3 bytes like ffffff for white");
        }
    }

    private static void validatePositive(String name, double value) {
        if (value <= 0) {
            throw new IllegalArgumentException(name + " must be greater than 0");
        }
    }

    private static void validateRightAngle(int angle) {
        if (angle % 90 != 0) {
            throw new IllegalArgumentException("angle must be a multiple of 90");
        }
    }

    private static void validateExtendGravity(GravityType gravityType) {
        if (gravityType == GravityType.sm) {
            throw new IllegalArgumentException(gravityType + " is not allowed with extend gravity");
        }
    }

}
