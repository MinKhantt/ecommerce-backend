package com.example.ecommercebackend.util;

import org.springframework.http.ResponseCookie;

public final class CookieUtil {

    private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";
    private static final String COOKIE_PATH = "/api/v1/auth";
    private static final long SEVEN_DAYS = 7 * 24 * 60 * 60;

    private CookieUtil() {}

    public static ResponseCookie createRefreshCookie(String refreshToken) {
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, refreshToken)
                .httpOnly(true)
                .secure(false)
                .path(COOKIE_PATH)
                .maxAge(SEVEN_DAYS)
                .sameSite("Strict")
                .build();
    }

    public static ResponseCookie clearRefreshCookie() {
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(false)
                .path(COOKIE_PATH)
                .maxAge(0)
                .sameSite("Strict")
                .build();
    }
}
