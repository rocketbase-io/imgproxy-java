package io.rocketbase.asset.imgproxy;

final class Hex {

    private Hex() {
    }

    static byte[] decode(String value, String name) {
        if (value == null || (value.length() % 2) != 0) {
            throw new IllegalArgumentException(name + " must be a valid even-length hex string");
        }
        byte[] result = new byte[value.length() / 2];
        for (int i = 0; i < value.length(); i += 2) {
            int high = Character.digit(value.charAt(i), 16);
            int low = Character.digit(value.charAt(i + 1), 16);
            if (high < 0 || low < 0) {
                throw new IllegalArgumentException(name + " must be a valid even-length hex string");
            }
            result[i / 2] = (byte) ((high << 4) + low);
        }
        return result;
    }

}
