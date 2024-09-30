package com.project.tableforyou.common.utils.jwt;

import io.jsonwebtoken.io.Decoder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

public class CustomBase64UrlDecoder implements Decoder<InputStream, InputStream> {

    public InputStream decode(InputStream inputStream) {
        try {
            byte[] bytes = inputStream.readAllBytes(); // InputStream을 바이트 배열로 변환
            byte[] decodedBytes = Base64.getUrlDecoder().decode(bytes); // Base64 URL-safe 디코딩
            return new ByteArrayInputStream(decodedBytes); // 디코딩된 결과를 새로운 InputStream으로 반환
        } catch (IOException e) {
            throw new RuntimeException("Decoding failed", e);
        }
    }
}