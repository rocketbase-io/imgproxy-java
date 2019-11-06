package io.rocketbase.asset.imgproxy;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;
import io.rocketbase.asset.imgproxy.options.GravityType;
import io.rocketbase.asset.imgproxy.options.ImageType;
import io.rocketbase.asset.imgproxy.options.ResizeType;
import io.rocketbase.asset.imgproxy.options.WatermarkPositionType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

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
        processingOptions.add(createProcessingOption("rs", resizeType.name(), width, height, enlarge, extend));
        return this;
    }

    /**
     * Meta-option that defines the resizing type, width, height, enlarge, and extend.
     */
    public Signature resize(ResizeType resizeType, int width, int height, boolean enlarge) {
        processingOptions.add(createProcessingOption("rs", resizeType.name(), width, height, enlarge));
        return this;
    }

    /**
     * Meta-option that defines the resizing type, width, height, enlarge, and extend.
     */
    public Signature resize(ResizeType resizeType, int width, int height) {
        processingOptions.add(createProcessingOption("rs", resizeType.name(), width, height));
        return this;
    }

    /**
     * Defines how imgproxy will resize the source image.
     */
    public Signature resize(ResizeType resizeType) {
        processingOptions.add(createProcessingOption("rt", resizeType.name()));
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
     * Defines the height of the resulting image. When set to 0, imgproxy will calculate resulting height using the defined width and source aspect ratio.
     * Default: 0
     */
    public Signature height(int height) {
        processingOptions.add(createProcessingOption("h", height));
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
     * Redefines quality of the resulting image, percentage.
     * Default: value from the environment variable.
     */
    public Signature quality(int percentage) {
        if (percentage < 1 || percentage > 100) {
            throw new IllegalArgumentException("quality percentage must be between 1 and 100 inclusively");
        }

        processingOptions.add(createProcessingOption("q", percentage));
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
     * When set, imgproxy will fill the resulting image background with the specified color.
     * hex_color is a hex-coded value of the color. Useful when you convert an image with alpha-channel to JPEG.
     * Default: disabled
     */
    public Signature background(String hexColor) {
        if (!isHexColor(hexColor)) {
            throw new IllegalArgumentException("hexcolor must be a hexadecimalencoded string for 3 bytes like ffffff for white");
        }
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
    public Signature watermark(double opacity, WatermarkPositionType position, int offsetX, int offsetY) {
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
     * Specifies the resulting image format. Alias for extension URL part.
     */
    public Signature format(String extension) {
        processingOptions.add(createProcessingOption("f", extension));
        return this;
    }


    public String url(String sourceUrl) {
        return url(sourceUrl, null);
    }

    @SneakyThrows
    public String url(String sourceUrl, ImageType imageType) {
        StringBuilder builder = new StringBuilder();

        for (String processingOption : processingOptions) {
            builder.append("/")
                    .append(processingOption);
        }

        String url = BaseEncoding.base64Url()
                .omitPadding()
                .encode(sourceUrl.getBytes());
        builder.append("/")
                .append(url);
        if (imageType != null) {
            builder.append(".")
                    .append(imageType.name());
        }
        String path = builder.toString();
        String signature = signUrl(path, configuration.getKey(), configuration.getSalt(), configuration.getNumberOfSignatureBytes());
        return configuration.getBaseurl() + "/" +
                signature +
                path;
    }

    static String signUrl(String path, String key, String salt, int numberOfSignatureBytes) throws NoSuchAlgorithmException, InvalidKeyException {
        if (key == null || salt == null) {
            return "notset";
        }
        String encodeString = salt + path;
        HashCode hashCode = Hashing.hmacSha256(key.getBytes())
                .hashBytes(encodeString.getBytes());
        return BaseEncoding.base64Url()
                .omitPadding()
                .encode(hashCode.asBytes(), 0, numberOfSignatureBytes);
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

}
