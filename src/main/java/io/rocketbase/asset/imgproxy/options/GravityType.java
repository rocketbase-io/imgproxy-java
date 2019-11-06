package io.rocketbase.asset.imgproxy.options;

import lombok.Getter;

public enum GravityType {
    no, so, ea, we, noea, nowe, soea, sowe, ce, sm(false);

    @Getter
    private final boolean focalPointAllowed;

    GravityType() {
        focalPointAllowed = true;
    }

    GravityType(boolean focalPointAllowed) {
        this.focalPointAllowed = focalPointAllowed;
    }

}
