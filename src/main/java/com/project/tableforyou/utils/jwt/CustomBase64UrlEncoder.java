package com.project.tableforyou.utils.jwt;

import io.jsonwebtoken.io.Encoder;

import java.io.OutputStream;
import java.util.Base64;

public class CustomBase64UrlEncoder implements Encoder<OutputStream, OutputStream> {
    @Override
    public OutputStream encode(OutputStream outputStream) {
        // OutputStream을 Base64 URL-safe로 인코딩
        Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
        return encoder.wrap(outputStream);  // Base64로 인코딩한 결과를 OutputStream으로 반환
    }
}