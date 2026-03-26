package io.rocketbase.asset.imgproxy.options;

/**
 * Defines how imgproxy will resize the source image
 */
public enum ResizeType {
    // resizes the image while keeping aspect ratio to fit given size
    fit("fit"),

    // resizes the image while keeping aspect ratio to fill given size and cropping projecting parts
    fill("fill"),

    // the same as fill, but if the resized image is smaller than the requested size, imgproxy will crop the result
    fill_down("fill-down"),

    // resizes the image without keeping the aspect ratio
    force("force"),

    // if both source and resulting dimensions have the same orientation (portrait or landscape), imgproxy will use fill. Otherwise, it will use fit
    auto("auto");

    private final String value;

    ResizeType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
