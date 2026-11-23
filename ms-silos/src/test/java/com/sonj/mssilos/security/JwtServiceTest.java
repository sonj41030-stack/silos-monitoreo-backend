package com.sonj.mssilos.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private static final String SECRETO = "clave-de-prueba-para-jwt-con-mas-de-32-bytes-123456";

    private String token(String secreto, long vidaMs) {
        Date ahora = new Date();
        return Jwts.builder()
                .subject("operador@silos.com")
                .claim("rol", "OPERADOR")
                .issuedAt(ahora)
                .expiration(new Date(ahora.getTime() + vidaMs))
                .signWith(Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    @Test
    void tokenValido_traeSubjectYRol() {
        JwtService svc = new JwtService(SECRETO);

        Claims c = svc.validar(token(SECRETO, 60000));

        assertEquals("operador@silos.com", c.getSubject());
        assertEquals("OPERADOR", c.get("rol", String.class));
    }

    @Test
    void tokenFirmadoConOtraClave_noValida() {
        JwtService svc = new JwtService(SECRETO);
        String token = token("otra-clave-distinta-con-mas-de-32-bytes-abcdef-999", 60000);

        assertThrows(JwtException.class, () -> svc.validar(token));
    }

    @Test
    void tokenExpirado_noValida() {
        JwtService svc = new JwtService(SECRETO);
        String token = token(SECRETO, -1000);

        assertThrows(JwtException.class, () -> svc.validar(token));
    }

    @Test
    void tokenMalformado_noValida() {
        JwtService svc = new JwtService(SECRETO);

        assertThrows(JwtException.class, () -> svc.validar("esto.no.es-un-token"));
    }
}