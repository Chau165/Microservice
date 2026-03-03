package com.group1.franchiseservice.controller;

import com.group1.franchiseservice.common.security.JwtService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.JWKSet;

import java.security.interfaces.RSAPublicKey;
import java.util.Map;

@RestController
public class JwksController {

    private final JwtService jwtService;

    public JwksController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @GetMapping("/.well-known/jwks.json")
    public Map<String, Object> getKeys() throws Exception {

        RSAPublicKey publicKey = (RSAPublicKey) jwtService.getPublicKey();

        RSAKey key = new RSAKey.Builder(publicKey)
                .keyID("franchise-key")
                .algorithm(JWSAlgorithm.RS256)
                .build();

        return new JWKSet(key).toJSONObject();
    }
}