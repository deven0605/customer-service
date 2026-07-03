package com.thalicloud.customer.security;

public interface JwtService {

    String extractSubject(String token);

    boolean isTokenExpired(String token);
}
