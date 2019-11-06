package io.rocketbase.asset.imgproxy;

import io.rocketbase.asset.imgproxy.options.GravityType;
import io.rocketbase.asset.imgproxy.options.ImageType;
import io.rocketbase.asset.imgproxy.options.ResizeType;
import io.rocketbase.asset.imgproxy.options.WatermarkPositionType;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class SignatureTest {

    private static final String BASE_URL = "http://localhost:8080";

    private static final String BASE_URL_UNSIGNED = BASE_URL + "/notset";

    private static final SignatureConfiguration SIGNED_CONFIGURATION = new SignatureConfiguration(BASE_URL, "secret", "hello");

    private static final SignatureConfiguration UNSIGNED_CONFIGURATION = new SignatureConfiguration(BASE_URL, null, null);


    private static final String SOURCE_URL = "s3://cdn.rocketbase.io/assets/rocketbase/logo-white-400x400-b2bf42d0ad.png";

    private static final String ENCODED_SOURCE_URL = "czM6Ly9jZG4ucm9ja2V0YmFzZS5pby9hc3NldHMvcm9ja2V0YmFzZS9sb2dvLXdoaXRlLTQwMHg0MDAtYjJiZjQyZDBhZC5wbmc";

    @Test
    public void shouldCreateSimpleUrl() {
        // when
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .size(100, 100)
                .url(SOURCE_URL);

        // then
        assertThat(url,
                is(BASE_URL_UNSIGNED + "/s:100:100/" + ENCODED_SOURCE_URL));
    }

    @Test
    public void shouldCreateSimpleUrlWithResultType() {
        // given
        // when
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .size(100, 100)
                .url(SOURCE_URL, ImageType.png);

        // then
        assertThat(url,
                is(BASE_URL_UNSIGNED + "/s:100:100/" + ENCODED_SOURCE_URL + ".png"));
    }

    @Test
    public void shouldCreateSimpleUrlWithResultTypeAndSigned() {
        // given
        // when
        String url = Signature.of(SIGNED_CONFIGURATION)
                .size(100, 100)
                .url(SOURCE_URL, ImageType.png);

        // then
        assertThat(url,
                is(BASE_URL + "/fh2xh50cD5uXV9JSqu0Os8Re4Ii2TRgvEywvKPvJGJk/s:100:100/" + ENCODED_SOURCE_URL + ".png"));
    }

    @Test
    public void shouldCreateResizeUrlWithAll() {
        // given
        // when
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .resize(ResizeType.fit, 500, 500, true, false)
                .url(SOURCE_URL);

        // then
        assertThat(url,
                is(BASE_URL_UNSIGNED + "/rs:fit:500:500:1:0/" + ENCODED_SOURCE_URL));
    }

    @Test
    public void shouldCreateResizeUrlWithoutExtend() {
        // given
        // when
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .resize(ResizeType.fit, 500, 500, true)
                .url(SOURCE_URL);

        // then
        assertThat(url,
                is(BASE_URL_UNSIGNED + "/rs:fit:500:500:1/" + ENCODED_SOURCE_URL));
    }

    @Test
    public void shouldCreateResizeUrlWithWidthAndHeight() {
        // given
        // when
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .resize(ResizeType.fit, 100, 100)
                .url(SOURCE_URL, ImageType.png);

        // then
        assertThat(url,
                is(BASE_URL_UNSIGNED + "/rs:fit:100:100/" + ENCODED_SOURCE_URL + ".png"));
    }

    @Test
    public void shouldCreateResizeUrlSimple() {
        // given
        // when
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .resize(ResizeType.fit)
                .url(SOURCE_URL, ImageType.png);

        // then
        assertThat(url,
                is(BASE_URL_UNSIGNED + "/rt:fit/" + ENCODED_SOURCE_URL + ".png"));
    }

    @Test
    public void shouldCreateSizeUrlAll() {
        // given
        // when
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .size(100, 200, false, true)
                .url(SOURCE_URL, ImageType.png);

        // then
        assertThat(url,
                is(BASE_URL_UNSIGNED + "/s:100:200:0:1/" + ENCODED_SOURCE_URL + ".png"));
    }

    @Test
    public void shouldCreateSizeUrlWithoutExtend() {
        // given
        // when
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .size(100, 200, true)
                .url(SOURCE_URL, ImageType.png);

        // then
        assertThat(url,
                is(BASE_URL_UNSIGNED + "/s:100:200:1/" + ENCODED_SOURCE_URL + ".png"));
    }

    @Test
    public void shouldCreateSizeUrl() {
        // given
        // when
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .size(100, 200)
                .url(SOURCE_URL, ImageType.png);

        // then
        assertThat(url,
                is(BASE_URL_UNSIGNED + "/s:100:200/" + ENCODED_SOURCE_URL + ".png"));
    }

    @Test
    public void shouldCreateWidthUrl() {
        // given
        // when
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .width(600)
                .url(SOURCE_URL, ImageType.png);

        // then
        assertThat(url,
                is(BASE_URL_UNSIGNED + "/w:600/" + ENCODED_SOURCE_URL + ".png"));
    }

    @Test
    public void shouldCreateHeightUrl() {
        // given
        // when
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .height(60)
                .url(SOURCE_URL, ImageType.png);

        // then
        assertThat(url,
                is(BASE_URL_UNSIGNED + "/h:60/" + ENCODED_SOURCE_URL + ".png"));
    }

    @Test
    public void shouldCreateDprUrl() {
        // given
        // when
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .dpr(true)
                .url(SOURCE_URL, ImageType.png);

        // then
        assertThat(url,
                is(BASE_URL_UNSIGNED + "/dpr:1/" + ENCODED_SOURCE_URL + ".png"));
    }

    @Test
    public void shouldCreateEnlargeUrl() {
        // given
        // when
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .enlarge(true)
                .url(SOURCE_URL, ImageType.png);

        // then
        assertThat(url,
                is(BASE_URL_UNSIGNED + "/el:1/" + ENCODED_SOURCE_URL + ".png"));
    }

    @Test
    public void shouldCreateExtendUrl() {
        // given
        // when
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .extend(true)
                .url(SOURCE_URL, ImageType.png);

        // then
        assertThat(url,
                is(BASE_URL_UNSIGNED + "/ex:1/" + ENCODED_SOURCE_URL + ".png"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotCreateGravityUrlWithSmartAndOffset() {
        // given
        // when
        Signature.of(UNSIGNED_CONFIGURATION)
                .gravity(GravityType.sm, 100, 200)
                .url(SOURCE_URL, ImageType.png);
    }

    @Test
    public void shouldCreateGravityUrlWithSmartAndOffset() {
        // given
        // when
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .gravity(GravityType.no, 100, 200)
                .url(SOURCE_URL, ImageType.png);

        // then
        assertThat(url,
                is(BASE_URL_UNSIGNED + "/g:no:100:200/" + ENCODED_SOURCE_URL + ".png"));
    }

    @Test
    public void shouldCreateGravityUrl() {
        // given
        // when
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .gravity(GravityType.sm)
                .url(SOURCE_URL, ImageType.png);

        // then
        assertThat(url,
                is(BASE_URL_UNSIGNED + "/g:sm/" + ENCODED_SOURCE_URL + ".png"));
    }

    @Test
    public void shouldCreateGravityUrlWithFocalPoint() {
        // given
        // when
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .gravity(0.5, 0.2)
                .url(SOURCE_URL, ImageType.png);

        // then
        assertThat(url,
                is(BASE_URL_UNSIGNED + "/g:fp:0.5:0.2/" + ENCODED_SOURCE_URL + ".png"));
    }


    @Test
    public void shouldCreateCropUrlWithAll() {
        // given
        // when
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .crop(50, 50, GravityType.ce, 10, 10)
                .url(SOURCE_URL, ImageType.png);

        // then
        assertThat(url,
                is(BASE_URL_UNSIGNED + "/c:50:50:ce:10:10/" + ENCODED_SOURCE_URL + ".png"));
    }

    @Test
    public void shouldCreateCropUrlWithoutOffset() {
        // given
        // when
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .crop(50, 50, GravityType.ce)
                .url(SOURCE_URL, ImageType.png);

        // then
        assertThat(url,
                is(BASE_URL_UNSIGNED + "/c:50:50:ce/" + ENCODED_SOURCE_URL + ".png"));
    }

    @Test
    public void shouldCreateCropUrlWithFocalPoint() {
        // given
        // when
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .crop(50, 50, 0.3, 0.4)
                .url(SOURCE_URL, ImageType.png);

        // then
        assertThat(url,
                is(BASE_URL_UNSIGNED + "/c:50:50:fp:0.3:0.4/" + ENCODED_SOURCE_URL + ".png"));
    }

    @Test
    public void shouldCreateCropUrl() {
        // given
        // when
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .crop(50, 50)
                .url(SOURCE_URL, ImageType.png);

        // then
        assertThat(url,
                is(BASE_URL_UNSIGNED + "/c:50:50/" + ENCODED_SOURCE_URL + ".png"));
    }

    @Test
    public void shouldCreateQualityUrl() {
        // given
        // when
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .quality(35)
                .url(SOURCE_URL, ImageType.jpg);

        // then
        assertThat(url,
                is(BASE_URL_UNSIGNED + "/q:35/" + ENCODED_SOURCE_URL + ".jpg"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotCreateQualityUrlWithTooSmallQuality() {
        // given
        // when
        Signature.of(UNSIGNED_CONFIGURATION)
                .quality(-1)
                .url(SOURCE_URL, ImageType.jpg);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotCreateQualityUrlWithTooBigQuality() {
        // given
        // when
        Signature.of(UNSIGNED_CONFIGURATION)
                .quality(101)
                .url(SOURCE_URL, ImageType.jpg);
    }

    @Test
    public void shouldCreateBackgroundUrlWithRGB() {
        // given
        // when
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .background(20, 90, 199)
                .url(SOURCE_URL, ImageType.png);

        // then
        assertThat(url,
                is(BASE_URL_UNSIGNED + "/bg:20:90:199/" + ENCODED_SOURCE_URL + ".png"));
    }

    @Test
    public void shouldCreateBackgroundUrlWithHexCode() {
        // given
        // when
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .background("AA0000")
                .url(SOURCE_URL, ImageType.png);

        // then
        assertThat(url,
                is(BASE_URL_UNSIGNED + "/bg:AA0000/" + ENCODED_SOURCE_URL + ".png"));
    }


    @Test(expected = IllegalArgumentException.class)
    public void shouldNotCreateBackgroundUrlWithInvalidHexCode() {
        // given
        // when
        Signature.of(UNSIGNED_CONFIGURATION)
                .background("AA00ZZ")
                .url(SOURCE_URL, ImageType.png);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotCreateBackgroundUrlWithInvalidR() {
        // given
        // when
        Signature.of(UNSIGNED_CONFIGURATION)
                .background(-1, 0, 0)
                .url(SOURCE_URL, ImageType.png);
    }


    @Test(expected = IllegalArgumentException.class)
    public void shouldNotCreateBackgroundUrlWithInvalidG() {
        // given
        // when
        Signature.of(UNSIGNED_CONFIGURATION)
                .background(0, -1, 0)
                .url(SOURCE_URL, ImageType.png);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotCreateBackgroundUrlWithInvalidB() {
        // given
        // when
        Signature.of(UNSIGNED_CONFIGURATION)
                .background(0, 0, -1)
                .url(SOURCE_URL, ImageType.png);
    }

    @Test
    public void shouldCreateBlurUrl() {
        // given
        // when
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .blur(5)
                .url(SOURCE_URL, ImageType.png);

        // then
        assertThat(url,
                is(BASE_URL_UNSIGNED + "/bl:5/" + ENCODED_SOURCE_URL + ".png"));
    }

    @Test
    public void shouldCreateSharpenUrl() {
        // given
        // when
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .sharpen(5)
                .url(SOURCE_URL, ImageType.png);

        // then
        assertThat(url,
                is(BASE_URL_UNSIGNED + "/sh:5/" + ENCODED_SOURCE_URL + ".png"));
    }

    @Test
    public void shouldCreateWatermarkUrlWithAll() {
        // given
        // when
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .watermark(0.8, WatermarkPositionType.nowe, 1, 1, 0.5)
                .url(SOURCE_URL, ImageType.png);

        // then
        assertThat(url,
                is(BASE_URL_UNSIGNED + "/wm:0.8:nowe:1:1:0.5/" + ENCODED_SOURCE_URL + ".png"));
    }

    @Test
    public void shouldCreateWatermarkUrlWithoutScale() {
        // given
        // when
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .watermark(0.8, WatermarkPositionType.nowe, 40, 40)
                .url(SOURCE_URL, ImageType.png);

        // then
        assertThat(url,
                is(BASE_URL_UNSIGNED + "/wm:0.8:nowe:40:40/" + ENCODED_SOURCE_URL + ".png"));
    }

    @Test
    public void shouldCreateWatermarkUrlWithoutOffset() {
        // given
        // when
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .watermark(0.8, WatermarkPositionType.ce)
                .url(SOURCE_URL, ImageType.png);

        // then
        assertThat(url,
                is(BASE_URL_UNSIGNED + "/wm:0.8:ce/" + ENCODED_SOURCE_URL + ".png"));
    }

    @Test
    public void shouldCreateWatermarkUrlWithOpacity() {
        // given
        // when
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .watermark(1)
                .url(SOURCE_URL, ImageType.png);

        // then
        assertThat(url,
                is(BASE_URL_UNSIGNED + "/wm:1.0/" + ENCODED_SOURCE_URL + ".png"));
    }

    @Test
    public void shouldCreatePresetUrl() {
        // given
        // when
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .preset("default", "test1")
                .url(SOURCE_URL, ImageType.png);

        // then
        assertThat(url,
                is(BASE_URL_UNSIGNED + "/pr:default:test1/" + ENCODED_SOURCE_URL + ".png"));
    }


    @Test
    public void shouldCreateCacheBusterUrl() {
        // given
        // when
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .cachebuster("tralla")
                .url(SOURCE_URL, ImageType.png);

        // then
        assertThat(url,
                is(BASE_URL_UNSIGNED + "/cb:tralla/" + ENCODED_SOURCE_URL + ".png"));
    }

    @Test
    public void shouldCreateFilenameUrl() {
        // given
        // when
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .filename("filename.png")
                .url(SOURCE_URL, ImageType.png);

        // then
        assertThat(url,
                is(BASE_URL_UNSIGNED + "/fn:filename.png/" + ENCODED_SOURCE_URL + ".png"));
    }

    @Test
    public void shouldCreateFormatUrl() {
        // given
        // when
        String url = Signature.of(UNSIGNED_CONFIGURATION)
                .format("tiff")
                .url(SOURCE_URL, ImageType.png);

        // then
        assertThat(url,
                is(BASE_URL_UNSIGNED + "/f:tiff/" + ENCODED_SOURCE_URL + ".png"));
    }

    @Test
    public void shouldCreateProcessingOptionCorrectly() {
        // given
        // when
        String result = Signature.createProcessingOption("test", "one", 1, true);

        // then
        assertThat(result, is("test:one:1:1"));
    }


    @Test
    public void shouldTestByte() {
        assertThat(Signature.isByte(0), is(true));
        assertThat(Signature.isByte(-1), is(false));
        assertThat(Signature.isByte(255), is(true));
        assertThat(Signature.isByte(256), is(false));
    }

    @Test
    public void shouldTestHexColor() {
        assertThat(Signature.isHexColor("000000"), is(true));
        assertThat(Signature.isHexColor("0000ff"), is(true));
        assertThat(Signature.isHexColor("00AA00"), is(true));
        assertThat(Signature.isHexColor("11111"), is(false));
        assertThat(Signature.isHexColor("GG0000"), is(false));
    }

    @Test
    public void shouldSignUrlCorrect() throws Exception {
        // given
        String path = "/fill/300/400/sm/0/aHR0cDovL2V4YW1w/bGUuY29tL2ltYWdl/cy9jdXJpb3NpdHku/anBn.png";
        String key = "secret";
        String salt = "hello";

        // when
        String signature = Signature.signUrl(path, key, salt, 32);

        // then
        assertThat(signature, is("AfrOrF3gWeDA6VOlDG4TzxMv39O7MXnF4CXpKUwGqRM"));
    }

}