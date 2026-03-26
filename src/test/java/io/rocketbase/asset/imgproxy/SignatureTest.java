package io.rocketbase.asset.imgproxy;

import io.rocketbase.asset.imgproxy.options.GravityType;
import io.rocketbase.asset.imgproxy.options.ImageType;
import io.rocketbase.asset.imgproxy.options.ResizeType;
import io.rocketbase.asset.imgproxy.options.WatermarkPositionType;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SignatureTest {

    private static final String BASE_URL = "http://localhost:8080";

    private static final String BASE_URL_UNSIGNED = BASE_URL + "/notset";

    private static final SignatureConfiguration SIGNED_CONFIGURATION = SignatureConfiguration.signedRaw(
            BASE_URL,
            "secret".getBytes(StandardCharsets.UTF_8),
            "hello".getBytes(StandardCharsets.UTF_8));

    private static final SignatureConfiguration SIGNED_HEX_CONFIGURATION = SignatureConfiguration.signed(BASE_URL, "736563726574", "68656c6c6f");

    private static final SignatureConfiguration UNSIGNED_CONFIGURATION = SignatureConfiguration.unsigned(BASE_URL);


    private static final String SOURCE_URL = "s3://cdn.rocketbase.io/assets/rocketbase/logo-white-400x400-b2bf42d0ad.png";

    private static final String ENCODED_SOURCE_URL = "czM6Ly9jZG4ucm9ja2V0YmFzZS5pby9hc3NldHMvcm9ja2V0YmFzZS9sb2dvLXdoaXRlLTQwMHg0MDAtYjJiZjQyZDBhZC5wbmc";

    @Test
    public void shouldCreateSimpleUrl() {
        // when
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .size(100, 100)
                .url(SOURCE_URL);

        // then
        assertEquals(BASE_URL_UNSIGNED + "/s:100:100/" + ENCODED_SOURCE_URL, url);
    }

    @Test
    public void shouldCreateSimpleUrlWithResultType() {
        // given
        // when
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .size(100, 100)
                .url(SOURCE_URL, ImageType.png);

        // then
        assertEquals(BASE_URL_UNSIGNED + "/s:100:100/" + ENCODED_SOURCE_URL + ".png", url);
    }

    @Test
    public void shouldCreateSimpleUrlWithResultTypeAndSigned() {
        // given
        // when
        String url = Signature.of(SIGNED_CONFIGURATION)
                .size(100, 100)
                .url(SOURCE_URL, ImageType.png);

        // then
        assertEquals(BASE_URL + "/fh2xh50cD5uXV9JSqu0Os8Re4Ii2TRgvEywvKPvJGJk/s:100:100/" + ENCODED_SOURCE_URL + ".png", url);
    }

    @Test
    public void shouldCreateSimpleUrlWithResultTypeAndHexSigned() {
        String url = Signature.of(SIGNED_HEX_CONFIGURATION)
                .size(100, 100)
                .url(SOURCE_URL, ImageType.png);

        assertEquals(BASE_URL + "/fh2xh50cD5uXV9JSqu0Os8Re4Ii2TRgvEywvKPvJGJk/s:100:100/" + ENCODED_SOURCE_URL + ".png", url);
    }

    @Test
    public void shouldCreateSignedConfigurationFactoryFromHexStrings() {
        SignatureConfiguration configuration = SignatureConfiguration.signed(BASE_URL, "736563726574", "68656c6c6f");

        assertArrayEquals("secret".getBytes(StandardCharsets.UTF_8), configuration.getKeyBytes());
        assertArrayEquals("hello".getBytes(StandardCharsets.UTF_8), configuration.getSaltBytes());
    }

    @Test
    public void shouldCreateSignedRawConfigurationFactoryFromBytes() {
        SignatureConfiguration configuration = SignatureConfiguration.signedRaw(
                BASE_URL,
                "secret".getBytes(StandardCharsets.UTF_8),
                "hello".getBytes(StandardCharsets.UTF_8));

        assertArrayEquals("secret".getBytes(StandardCharsets.UTF_8), configuration.getKeyBytes());
        assertArrayEquals("hello".getBytes(StandardCharsets.UTF_8), configuration.getSaltBytes());
    }

    @Test
    public void shouldCreateUnsignedConfigurationFactory() {
        SignatureConfiguration configuration = SignatureConfiguration.unsigned(BASE_URL);

        assertEquals(BASE_URL, configuration.getBaseurl());
        assertNull(configuration.getKeyBytes());
        assertNull(configuration.getSaltBytes());
    }

    @Test
    public void shouldCreateResizeUrlWithAll() {
        // given
        // when
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .resize(ResizeType.fit, 500, 500, true, false)
                .url(SOURCE_URL);

        // then
        assertEquals(BASE_URL_UNSIGNED + "/rs:fit:500:500:1:0/" + ENCODED_SOURCE_URL, url);
    }

    @Test
    public void shouldCreateResizeUrlWithoutExtend() {
        // given
        // when
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .resize(ResizeType.fit, 500, 500, true)
                .url(SOURCE_URL);

        // then
        assertEquals(BASE_URL_UNSIGNED + "/rs:fit:500:500:1/" + ENCODED_SOURCE_URL, url);
    }

    @Test
    public void shouldCreateResizeUrlWithWidthAndHeight() {
        // given
        // when
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .resize(ResizeType.fit, 100, 100)
                .url(SOURCE_URL, ImageType.png);

        // then
        assertEquals(BASE_URL_UNSIGNED + "/rs:fit:100:100/" + ENCODED_SOURCE_URL + ".png", url);
    }

    @Test
    public void shouldCreateResizeUrlSimple() {
        // given
        // when
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .resize(ResizeType.fit)
                .url(SOURCE_URL, ImageType.png);

        // then
        assertEquals(BASE_URL_UNSIGNED + "/rt:fit/" + ENCODED_SOURCE_URL + ".png", url);
    }

    @Test
    public void shouldCreateSizeUrlAll() {
        // given
        // when
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .size(100, 200, false, true)
                .url(SOURCE_URL, ImageType.png);

        // then
        assertEquals(BASE_URL_UNSIGNED + "/s:100:200:0:1/" + ENCODED_SOURCE_URL + ".png", url);
    }

    @Test
    public void shouldCreateSizeUrlWithoutExtend() {
        // given
        // when
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .size(100, 200, true)
                .url(SOURCE_URL, ImageType.png);

        // then
        assertEquals(BASE_URL_UNSIGNED + "/s:100:200:1/" + ENCODED_SOURCE_URL + ".png", url);
    }

    @Test
    public void shouldCreateSizeUrl() {
        // given
        // when
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .size(100, 200)
                .url(SOURCE_URL, ImageType.png);

        // then
        assertEquals(BASE_URL_UNSIGNED + "/s:100:200/" + ENCODED_SOURCE_URL + ".png", url);
    }

    @Test
    public void shouldCreateWidthUrl() {
        // given
        // when
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .width(600)
                .url(SOURCE_URL, ImageType.png);

        // then
        assertEquals(BASE_URL_UNSIGNED + "/w:600/" + ENCODED_SOURCE_URL + ".png", url);
    }

    @Test
    public void shouldCreateHeightUrl() {
        // given
        // when
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .height(60)
                .url(SOURCE_URL, ImageType.png);

        // then
        assertEquals(BASE_URL_UNSIGNED + "/h:60/" + ENCODED_SOURCE_URL + ".png", url);
    }

    @Test
    public void shouldCreateDprUrl() {
        // given
        // when
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .dpr(true)
                .url(SOURCE_URL, ImageType.png);

        // then
        assertEquals(BASE_URL_UNSIGNED + "/dpr:1/" + ENCODED_SOURCE_URL + ".png", url);
    }

    @Test
    public void shouldCreateEnlargeUrl() {
        // given
        // when
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .enlarge(true)
                .url(SOURCE_URL, ImageType.png);

        // then
        assertEquals(BASE_URL_UNSIGNED + "/el:1/" + ENCODED_SOURCE_URL + ".png", url);
    }

    @Test
    public void shouldCreateExtendUrl() {
        // given
        // when
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .extend(true)
                .url(SOURCE_URL, ImageType.png);

        // then
        assertEquals(BASE_URL_UNSIGNED + "/ex:1/" + ENCODED_SOURCE_URL + ".png", url);
    }

    @Test
    public void shouldNotCreateGravityUrlWithSmartAndOffset() {
        assertThrows(IllegalArgumentException.class, () -> Signature.of(UNSIGNED_CONFIGURATION)
                .gravity(GravityType.sm, 100, 200)
                .url(SOURCE_URL, ImageType.png));
    }

    @Test
    public void shouldCreateGravityUrlWithSmartAndOffset() {
        // given
        // when
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .gravity(GravityType.no, 100, 200)
                .url(SOURCE_URL, ImageType.png);

        // then
        assertEquals(BASE_URL_UNSIGNED + "/g:no:100:200/" + ENCODED_SOURCE_URL + ".png", url);
    }

    @Test
    public void shouldCreateGravityUrl() {
        // given
        // when
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .gravity(GravityType.sm)
                .url(SOURCE_URL, ImageType.png);

        // then
        assertEquals(BASE_URL_UNSIGNED + "/g:sm/" + ENCODED_SOURCE_URL + ".png", url);
    }

    @Test
    public void shouldCreateGravityUrlWithFocalPoint() {
        // given
        // when
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .gravity(0.5, 0.2)
                .url(SOURCE_URL, ImageType.png);

        // then
        assertEquals(BASE_URL_UNSIGNED + "/g:fp:0.5:0.2/" + ENCODED_SOURCE_URL + ".png", url);
    }


    @Test
    public void shouldCreateCropUrlWithAll() {
        // given
        // when
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .crop(50, 50, GravityType.ce, 10, 10)
                .url(SOURCE_URL, ImageType.png);

        // then
        assertEquals(BASE_URL_UNSIGNED + "/c:50:50:ce:10:10/" + ENCODED_SOURCE_URL + ".png", url);
    }

    @Test
    public void shouldCreateCropUrlWithoutOffset() {
        // given
        // when
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .crop(50, 50, GravityType.ce)
                .url(SOURCE_URL, ImageType.png);

        // then
        assertEquals(BASE_URL_UNSIGNED + "/c:50:50:ce/" + ENCODED_SOURCE_URL + ".png", url);
    }

    @Test
    public void shouldCreateCropUrlWithFocalPoint() {
        // given
        // when
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .crop(50, 50, 0.3, 0.4)
                .url(SOURCE_URL, ImageType.png);

        // then
        assertEquals(BASE_URL_UNSIGNED + "/c:50:50:fp:0.3:0.4/" + ENCODED_SOURCE_URL + ".png", url);
    }

    @Test
    public void shouldCreateCropUrl() {
        // given
        // when
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .crop(50, 50)
                .url(SOURCE_URL, ImageType.png);

        // then
        assertEquals(BASE_URL_UNSIGNED + "/c:50:50/" + ENCODED_SOURCE_URL + ".png", url);
    }

    @Test
    public void shouldCreateQualityUrl() {
        // given
        // when
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .quality(35)
                .url(SOURCE_URL, ImageType.jpg);

        // then
        assertEquals(BASE_URL_UNSIGNED + "/q:35/" + ENCODED_SOURCE_URL + ".jpg", url);
    }

    @Test
    public void shouldNotCreateQualityUrlWithTooSmallQuality() {
        assertThrows(IllegalArgumentException.class, () -> Signature.of(UNSIGNED_CONFIGURATION)
                .quality(-1)
                .url(SOURCE_URL, ImageType.jpg));
    }

    @Test
    public void shouldNotCreateQualityUrlWithTooBigQuality() {
        assertThrows(IllegalArgumentException.class, () -> Signature.of(UNSIGNED_CONFIGURATION)
                .quality(101)
                .url(SOURCE_URL, ImageType.jpg));
    }

    @Test
    public void shouldCreateBackgroundUrlWithRGB() {
        // given
        // when
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .background(20, 90, 199)
                .url(SOURCE_URL, ImageType.png);

        // then
        assertEquals(BASE_URL_UNSIGNED + "/bg:20:90:199/" + ENCODED_SOURCE_URL + ".png", url);
    }

    @Test
    public void shouldCreateBackgroundUrlWithHexCode() {
        // given
        // when
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .background("AA0000")
                .url(SOURCE_URL, ImageType.png);

        // then
        assertEquals(BASE_URL_UNSIGNED + "/bg:AA0000/" + ENCODED_SOURCE_URL + ".png", url);
    }


    @Test
    public void shouldNotCreateBackgroundUrlWithInvalidHexCode() {
        assertThrows(IllegalArgumentException.class, () -> Signature.of(UNSIGNED_CONFIGURATION)
                .background("AA00ZZ")
                .url(SOURCE_URL, ImageType.png));
    }

    @Test
    public void shouldNotCreateBackgroundUrlWithInvalidR() {
        assertThrows(IllegalArgumentException.class, () -> Signature.of(UNSIGNED_CONFIGURATION)
                .background(-1, 0, 0)
                .url(SOURCE_URL, ImageType.png));
    }


    @Test
    public void shouldNotCreateBackgroundUrlWithInvalidG() {
        assertThrows(IllegalArgumentException.class, () -> Signature.of(UNSIGNED_CONFIGURATION)
                .background(0, -1, 0)
                .url(SOURCE_URL, ImageType.png));
    }

    @Test
    public void shouldNotCreateBackgroundUrlWithInvalidB() {
        assertThrows(IllegalArgumentException.class, () -> Signature.of(UNSIGNED_CONFIGURATION)
                .background(0, 0, -1)
                .url(SOURCE_URL, ImageType.png));
    }

    @Test
    public void shouldCreateBlurUrl() {
        // given
        // when
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .blur(5)
                .url(SOURCE_URL, ImageType.png);

        // then
        assertEquals(BASE_URL_UNSIGNED + "/bl:5/" + ENCODED_SOURCE_URL + ".png", url);
    }

    @Test
    public void shouldCreateSharpenUrl() {
        // given
        // when
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .sharpen(5)
                .url(SOURCE_URL, ImageType.png);

        // then
        assertEquals(BASE_URL_UNSIGNED + "/sh:5/" + ENCODED_SOURCE_URL + ".png", url);
    }

    @Test
    public void shouldCreateWatermarkUrlWithAll() {
        // given
        // when
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .watermark(0.8, WatermarkPositionType.nowe, 1, 1, 0.5)
                .url(SOURCE_URL, ImageType.png);

        // then
        assertEquals(BASE_URL_UNSIGNED + "/wm:0.8:nowe:1:1:0.5/" + ENCODED_SOURCE_URL + ".png", url);
    }

    @Test
    public void shouldCreateWatermarkUrlWithoutScale() {
        // given
        // when
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .watermark(0.8, WatermarkPositionType.nowe, 40, 40)
                .url(SOURCE_URL, ImageType.png);

        // then
        assertEquals(BASE_URL_UNSIGNED + "/wm:0.8:nowe:40:40/" + ENCODED_SOURCE_URL + ".png", url);
    }

    @Test
    public void shouldCreateWatermarkUrlWithoutOffset() {
        // given
        // when
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .watermark(0.8, WatermarkPositionType.ce)
                .url(SOURCE_URL, ImageType.png);

        // then
        assertEquals(BASE_URL_UNSIGNED + "/wm:0.8:ce/" + ENCODED_SOURCE_URL + ".png", url);
    }

    @Test
    public void shouldCreateWatermarkUrlWithOpacity() {
        // given
        // when
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .watermark(1)
                .url(SOURCE_URL, ImageType.png);

        // then
        assertEquals(BASE_URL_UNSIGNED + "/wm:1.0/" + ENCODED_SOURCE_URL + ".png", url);
    }

    @Test
    public void shouldCreatePresetUrl() {
        // given
        // when
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .preset("default", "test1")
                .url(SOURCE_URL, ImageType.png);

        // then
        assertEquals(BASE_URL_UNSIGNED + "/pr:default:test1/" + ENCODED_SOURCE_URL + ".png", url);
    }


    @Test
    public void shouldCreateCacheBusterUrl() {
        // given
        // when
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .cachebuster("tralla")
                .url(SOURCE_URL, ImageType.png);

        // then
        assertEquals(BASE_URL_UNSIGNED + "/cb:tralla/" + ENCODED_SOURCE_URL + ".png", url);
    }

    @Test
    public void shouldCreateFilenameUrl() {
        // given
        // when
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .filename("filename.png")
                .url(SOURCE_URL, ImageType.png);

        // then
        assertEquals(BASE_URL_UNSIGNED + "/fn:filename.png/" + ENCODED_SOURCE_URL + ".png", url);
    }

    @Test
    public void shouldCreateFormatUrl() {
        // given
        // when
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .format("tiff")
                .url(SOURCE_URL, ImageType.png);

        // then
        assertEquals(BASE_URL_UNSIGNED + "/f:tiff/" + ENCODED_SOURCE_URL + ".png", url);
    }

    @Test
    public void shouldCreateProcessingOptionCorrectly() {
        // given
        // when
        String result = Signature.createProcessingOption("test", "one", 1, true);

        // then
        assertEquals("test:one:1:1", result);
    }


    @Test
    public void shouldTestByte() {
        assertTrue(Signature.isByte(0));
        assertFalse(Signature.isByte(-1));
        assertTrue(Signature.isByte(255));
        assertFalse(Signature.isByte(256));
    }

    @Test
    public void shouldTestHexColor() {
        assertTrue(Signature.isHexColor("000000"));
        assertTrue(Signature.isHexColor("0000ff"));
        assertTrue(Signature.isHexColor("00AA00"));
        assertFalse(Signature.isHexColor("11111"));
        assertFalse(Signature.isHexColor("GG0000"));
    }

    @Test
    public void shouldSignUrlCorrect() throws Exception {
        // given
        String path = "/fill/300/400/sm/0/aHR0cDovL2V4YW1w/bGUuY29tL2ltYWdl/cy9jdXJpb3NpdHku/anBn.png";
        String signature = Signature.signUrl(path, "secret".getBytes(StandardCharsets.UTF_8),
                "hello".getBytes(StandardCharsets.UTF_8), 32);

        // then
        assertEquals("AfrOrF3gWeDA6VOlDG4TzxMv39O7MXnF4CXpKUwGqRM", signature);
    }

    @Test
    public void shouldSignUrlCorrectWithHexEncodedKeyAndSalt() throws Exception {
        String path = "/fill/300/400/sm/0/aHR0cDovL2V4YW1w/bGUuY29tL2ltYWdl/cy9jdXJpb3NpdHku/anBn.png";

        String signature = Signature.signUrl(path, Hex.decode("736563726574", "key"), Hex.decode("68656c6c6f", "salt"), 32);

        assertEquals("AfrOrF3gWeDA6VOlDG4TzxMv39O7MXnF4CXpKUwGqRM", signature);
    }

    @Test
    public void shouldCreateResizeUrlWithFillDown() {
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .resize(ResizeType.fill_down, 300, 200)
                .url(SOURCE_URL, ImageType.png);

        assertEquals(BASE_URL_UNSIGNED + "/rs:fill-down:300:200/" + ENCODED_SOURCE_URL + ".png", url);
    }

    @Test
    public void shouldCreateResizeTypeUrlWithForce() {
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .resize(ResizeType.force)
                .url(SOURCE_URL, ImageType.png);

        assertEquals(BASE_URL_UNSIGNED + "/rt:force/" + ENCODED_SOURCE_URL + ".png", url);
    }

    @Test
    public void shouldCreateMinWidthUrl() {
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .minWidth(320)
                .url(SOURCE_URL, ImageType.png);

        assertEquals(BASE_URL_UNSIGNED + "/mw:320/" + ENCODED_SOURCE_URL + ".png", url);
    }

    @Test
    public void shouldCreateMinHeightUrl() {
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .minHeight(240)
                .url(SOURCE_URL, ImageType.png);

        assertEquals(BASE_URL_UNSIGNED + "/mh:240/" + ENCODED_SOURCE_URL + ".png", url);
    }

    @Test
    public void shouldCreateZoomUrlWithSingleFactor() {
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .zoom(1.5)
                .url(SOURCE_URL, ImageType.png);

        assertEquals(BASE_URL_UNSIGNED + "/z:1.5/" + ENCODED_SOURCE_URL + ".png", url);
    }

    @Test
    public void shouldCreateZoomUrlWithIndependentFactors() {
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .zoom(1.5, 2.0)
                .url(SOURCE_URL, ImageType.png);

        assertEquals(BASE_URL_UNSIGNED + "/z:1.5:2.0/" + ENCODED_SOURCE_URL + ".png", url);
    }

    @Test
    public void shouldCreateDprUrlWithFactor() {
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .dpr(2.0)
                .url(SOURCE_URL, ImageType.png);

        assertEquals(BASE_URL_UNSIGNED + "/dpr:2.0/" + ENCODED_SOURCE_URL + ".png", url);
    }

    @Test
    public void shouldCreateExtendUrlWithGravityAndRelativeOffsets() {
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .extend(true, GravityType.noea, 0.25, -0.5)
                .url(SOURCE_URL, ImageType.png);

        assertEquals(BASE_URL_UNSIGNED + "/ex:1:noea:0.25:-0.5/" + ENCODED_SOURCE_URL + ".png", url);
    }

    @Test
    public void shouldCreateExtendAspectRatioUrlWithFocalPoint() {
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .extendAspectRatio(true, 0.25, 0.75)
                .url(SOURCE_URL, ImageType.png);

        assertEquals(BASE_URL_UNSIGNED + "/exar:1:fp:0.25:0.75/" + ENCODED_SOURCE_URL + ".png", url);
    }

    @Test
    public void shouldCreateGravityUrlWithRelativeOffsets() {
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .gravity(GravityType.ce, 0.25, -0.5)
                .url(SOURCE_URL, ImageType.png);

        assertEquals(BASE_URL_UNSIGNED + "/g:ce:0.25:-0.5/" + ENCODED_SOURCE_URL + ".png", url);
    }

    @Test
    public void shouldCreateCropUrlWithRelativeSizeAndOffsets() {
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .crop(0.5, 0.25, GravityType.ce, 0.1, 0.2)
                .url(SOURCE_URL, ImageType.png);

        assertEquals(BASE_URL_UNSIGNED + "/c:0.5:0.25:ce:0.1:0.2/" + ENCODED_SOURCE_URL + ".png", url);
    }

    @Test
    public void shouldCreateTrimUrlWithAllArguments() {
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .trim(10, "FF00FF", true, false)
                .url(SOURCE_URL, ImageType.png);

        assertEquals(BASE_URL_UNSIGNED + "/t:10:FF00FF:1:0/" + ENCODED_SOURCE_URL + ".png", url);
    }

    @Test
    public void shouldCreatePaddingUrl() {
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .padding(10, 20, 30, 40)
                .url(SOURCE_URL, ImageType.png);

        assertEquals(BASE_URL_UNSIGNED + "/pd:10:20:30:40/" + ENCODED_SOURCE_URL + ".png", url);
    }

    @Test
    public void shouldCreateAutoRotateUrl() {
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .autoRotate(true)
                .url(SOURCE_URL, ImageType.png);

        assertEquals(BASE_URL_UNSIGNED + "/ar:1/" + ENCODED_SOURCE_URL + ".png", url);
    }

    @Test
    public void shouldCreateRotateUrl() {
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .rotate(270)
                .url(SOURCE_URL, ImageType.png);

        assertEquals(BASE_URL_UNSIGNED + "/rot:270/" + ENCODED_SOURCE_URL + ".png", url);
    }

    @Test
    public void shouldCreateFlipUrl() {
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .flip(true, false)
                .url(SOURCE_URL, ImageType.png);

        assertEquals(BASE_URL_UNSIGNED + "/fl:1:0/" + ENCODED_SOURCE_URL + ".png", url);
    }

    @Test
    public void shouldCreateBackgroundResetUrl() {
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .background()
                .url(SOURCE_URL, ImageType.png);

        assertEquals(BASE_URL_UNSIGNED + "/bg/" + ENCODED_SOURCE_URL + ".png", url);
    }

    @Test
    public void shouldCreatePixelateUrl() {
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .pixelate(12)
                .url(SOURCE_URL, ImageType.png);

        assertEquals(BASE_URL_UNSIGNED + "/pix:12/" + ENCODED_SOURCE_URL + ".png", url);
    }

    @Test
    public void shouldCreateWatermarkUrlWithRelativeOffsets() {
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .watermark(0.8, WatermarkPositionType.re, 0.25, -0.5, 0.4)
                .url(SOURCE_URL, ImageType.png);

        assertEquals(BASE_URL_UNSIGNED + "/wm:0.8:re:0.25:-0.5:0.4/" + ENCODED_SOURCE_URL + ".png", url);
    }

    @Test
    public void shouldCreateQualityUrlWithZero() {
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .quality(0)
                .url(SOURCE_URL, ImageType.jpg);

        assertEquals(BASE_URL_UNSIGNED + "/q:0/" + ENCODED_SOURCE_URL + ".jpg", url);
    }

    @Test
    public void shouldCreateFilenameUrlWithEncodedFlag() {
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .filename("ZsO2w7YucG5n", true)
                .url(SOURCE_URL, ImageType.png);

        assertEquals(BASE_URL_UNSIGNED + "/fn:ZsO2w7YucG5n:1/" + ENCODED_SOURCE_URL + ".png", url);
    }

    @Test
    public void shouldEscapePlainSourceUrlSpecialCharacters() {
        String sourceUrl = "https://example.org/image @2x.png?foo=bar@baz";

        assertEquals("https://example.org/image%20%402x.png%3Ffoo=bar%40baz", Signature.escapePlainSourceUrl(sourceUrl));
    }

    @Test
    public void shouldCreatePlainUrl() {
        String sourceUrl = "https://example.org/image @2x.png?foo=bar@baz";
        String escapedSourceUrl = "https://example.org/image%20%402x.png%3Ffoo=bar%40baz";

        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .size(100, 100)
                .urlPlain(sourceUrl, ImageType.png);

        assertEquals(BASE_URL_UNSIGNED + "/s:100:100/plain/" + escapedSourceUrl + "@png", url);
    }

    @Test
    public void shouldCreateSignedPlainUrl() throws Exception {
        String sourceUrl = "https://example.org/image @2x.png?foo=bar@baz";
        String escapedSourceUrl = "https://example.org/image%20%402x.png%3Ffoo=bar%40baz";
        String path = "/s:100:100/plain/" + escapedSourceUrl + "@png";
        String signature = Signature.signUrl(path, "secret".getBytes(StandardCharsets.UTF_8),
                "hello".getBytes(StandardCharsets.UTF_8), 32);

        String url = Signature.of(SIGNED_CONFIGURATION)
                .size(100, 100)
                .urlPlain(sourceUrl, ImageType.png);

        assertEquals(BASE_URL + "/" + signature + path, url);
    }

    @Test
    public void shouldCreateSignedPlainUrlWithHexEncodedKeyAndSalt() throws Exception {
        String sourceUrl = "https://example.org/image @2x.png?foo=bar@baz";
        String escapedSourceUrl = "https://example.org/image%20%402x.png%3Ffoo=bar%40baz";
        String path = "/s:100:100/plain/" + escapedSourceUrl + "@png";
        String signature = Signature.signUrl(path, Hex.decode("736563726574", "key"), Hex.decode("68656c6c6f", "salt"), 32);

        String url = Signature.of(SIGNED_HEX_CONFIGURATION)
                .size(100, 100)
                .urlPlain(sourceUrl, ImageType.png);

        assertEquals(BASE_URL + "/" + signature + path, url);
    }

    @Test
    public void shouldNotAllowNonPositiveZoom() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Signature.of(UNSIGNED_CONFIGURATION).zoom(0));

        assertEquals("zoom must be greater than 0", exception.getMessage());
    }

    @Test
    public void shouldNotAllowNonRightAngleRotation() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Signature.of(UNSIGNED_CONFIGURATION).rotate(45));

        assertEquals("angle must be a multiple of 90", exception.getMessage());
    }

    @Test
    public void shouldNotAllowSmartGravityForExtend() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Signature.of(UNSIGNED_CONFIGURATION).extend(true, GravityType.sm));

        assertEquals("sm is not allowed with extend gravity", exception.getMessage());
    }

    @Test
    public void shouldCreateSignedUrlWithReducedSignatureBytes() {
        SignatureConfiguration configuration = SignatureConfiguration.signedRaw(
                BASE_URL,
                "secret".getBytes(StandardCharsets.UTF_8),
                "hello".getBytes(StandardCharsets.UTF_8))
                .numberOfSignatureBytes(8);

        String url = Signature.of(configuration)
                .size(100, 100)
                .url(SOURCE_URL, ImageType.png);

        assertEquals(BASE_URL + "/fh2xh50cD5s/s:100:100/" + ENCODED_SOURCE_URL + ".png", url);
    }

    @Test
    public void shouldCreateSignedUrlWithReducedSignatureBytesAndHexKeySalt() {
        SignatureConfiguration configuration = SignatureConfiguration.signed(BASE_URL, "736563726574", "68656c6c6f")
                .numberOfSignatureBytes(8);

        String url = Signature.of(configuration)
                .size(100, 100)
                .url(SOURCE_URL, ImageType.png);

        assertEquals(BASE_URL + "/fh2xh50cD5s/s:100:100/" + ENCODED_SOURCE_URL + ".png", url);
    }

    @Test
    public void shouldCreateUrlWithUnicodeSourceUsingUtf8Encoding() {
        String sourceUrl = "https://cdn.example.org/über uns/weiß.png?emoji=😊";
        String encodedSourceUrl = "aHR0cHM6Ly9jZG4uZXhhbXBsZS5vcmcvw7xiZXIgdW5zL3dlacOfLnBuZz9lbW9qaT3wn5iK";

        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .resize(ResizeType.fill, 300, 200)
                .url(sourceUrl, ImageType.webp);

        assertEquals(BASE_URL_UNSIGNED + "/rs:fill:300:200/" + encodedSourceUrl + ".webp", url);
    }

    @Test
    public void shouldCreateSignatureWithUnicodeKeySaltAndPathUsingUtf8() {
        String sourceUrl = "https://cdn.example.org/über uns/weiß.png?emoji=😊";
        String encodedSourceUrl = "aHR0cHM6Ly9jZG4uZXhhbXBsZS5vcmcvw7xiZXIgdW5zL3dlacOfLnBuZz9lbW9qaT3wn5iK";
        SignatureConfiguration configuration = SignatureConfiguration.signedRaw(
                BASE_URL,
                "sëcret🔑".getBytes(StandardCharsets.UTF_8),
                "sälty🧂".getBytes(StandardCharsets.UTF_8))
                .numberOfSignatureBytes(12);

        String url = Signature.of(configuration)
                .resize(ResizeType.fill, 300, 200)
                .url(sourceUrl, ImageType.webp);

        assertEquals(BASE_URL + "/ANvMYp-DXTcP22jW/rs:fill:300:200/" + encodedSourceUrl + ".webp", url);
    }

    @Test
    public void shouldNotAllowTooSmallNumberOfSignatureBytes() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> SignatureConfiguration.signedRaw(BASE_URL,
                        "secret".getBytes(StandardCharsets.UTF_8),
                        "hello".getBytes(StandardCharsets.UTF_8)).numberOfSignatureBytes(0));

        assertEquals("numberOfSignatureBytes must be between 1 and 32 inclusively", exception.getMessage());
    }

    @Test
    public void shouldNotAllowTooLargeNumberOfSignatureBytes() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> SignatureConfiguration.signedRaw(BASE_URL,
                        "secret".getBytes(StandardCharsets.UTF_8),
                        "hello".getBytes(StandardCharsets.UTF_8)).numberOfSignatureBytes(33));

        assertEquals("numberOfSignatureBytes must be between 1 and 32 inclusively", exception.getMessage());
    }

    @Test
    public void shouldNotAllowInvalidHexEncodedKey() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> SignatureConfiguration.signed(BASE_URL, "xyz", "68656c6c6f"));

        assertEquals("key must be a valid even-length hex string", exception.getMessage());
    }

    @Test
    public void shouldNotAllowInvalidHexEncodedSalt() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> SignatureConfiguration.signed(BASE_URL, "736563726574", "abc"));

        assertEquals("salt must be a valid even-length hex string", exception.getMessage());
    }

}
