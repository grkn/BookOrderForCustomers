package com.reading.is.good.constant;

public final class ChallengeConstant {


    public static final String ROLE_USER = "ROLE_USER";
    public static final String DUMMY_SIGN = "anySign";
    public static final String AUTH_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final long EXPIRE_TIME = 1000 * 60 * 60 * 2L;
    public static final String BASE_URL = "/api/v1";
    public static final String AUTHORIZE_ENDPOINT = BASE_URL + "/authorize";
    public static final String TOKEN_ENDPOINT = BASE_URL + "/token";
    public static final String SWAGGER_ENDPOINT = "/swagger-ui/**";
    public static final String SWAGGER_V2_API_DOCS = "/v2/api-docs";
    public static final String SWAGGER_RESOURCES = "/swagger-resources/**";

    private ChallengeConstant() {
    }
}
