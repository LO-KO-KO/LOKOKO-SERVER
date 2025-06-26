package com.lokoko.global.utils;

public class LineConstants {
    // Base URL
    public static final String AUTHORIZE_PATH = "https://access.line.me/oauth2/v2.1/authorize";
    public static final String TOKEN_PATH = "/oauth2/v2.1/token";
    public static final String PROFILE_PATH = "/v2/profile";
    // OAuth
    public static final String PARAM_RESPONSE_TYPE = "?response_type=code";
    public static final String PARAM_GRANT_TYPE = "grant_type";
    public static final String PARAM_CODE = "code";
    public static final String REDIRECT_URI = "redirect_uri";
    public static final String CLIENT_ID = "client_id";
    public static final String PARAM_REDIRECT_URI = "&redirect_uri=";
    public static final String PARAM_CLIENT_ID = "&client_id=";
    public static final String PARAM_CLIENT_SECRET = "client_secret";
    public static final String PARAM_SCOPE = "&scope=profile%20openid";
    public static final String PARAM_STATE = "&state=";
    public static final String RESPONSE_TYPE_CODE = "code";
    public static final String GRANT_TYPE_AUTH_CODE = "authorization_code";

    private LineConstants() {
    }
}
