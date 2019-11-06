package io.rocketbase.asset.imgproxy.options;

/**
 * Defines how imgproxy will resize the source image
 */
public enum ResizeType {
    // resizes the image while keeping aspect ratio to fit given size
    fit,

    // resizes the image while keeping aspect ratio to fill given size and cropping projecting parts
    fill,

    // if both source and resulting dimensions have the same orientation (portrait or landscape), imgproxy will use fill. Otherwise, it will use fit
    auto;
}
