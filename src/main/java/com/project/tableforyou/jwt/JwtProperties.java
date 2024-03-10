package com.project.tableforyou.jwt;

public interface JwtProperties {
    Long ACCESS_EXPIRATION_TIME = 30*60*1000L;  // 30분
    Long REFRESH_EXPIRATION_TIME = 24*60*60*1000L;      // 24시간
    String SECRET_KEY = "vmfhaltmskdlstkfkdgodyroqkfwkdbalroqkfwkdbalaaaaaaaaaaaaaaaabbbbb";    // 임의로 만든 암호화 변수 키
    String TOKEN_PREFIX = "Bearer ";
    String ACCESS_HEADER_VALUE = "Authorization";
    String REFRESH_COOKIE_VALUE = "refresh";
}
