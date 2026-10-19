package me.havooc.poligon.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Bezpieczna, ograniczona lista domen dla środowiska lokalnego i Azure
        config.setAllowedOriginPatterns(List.of(
                "http://localhost:*",
                "http://127.0.0.1:*",
                "https://*.azurecontainerapps.io",
                "https://*.azurewebsites.net"
        ));

        // Metody wymagane do działania REST API i zapytań wstępnych (preflight OPTIONS)
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

        // Pozwala na przesyłanie dowolnych nagłówków (w tym Authorization z Firebase ID tokenem)
        config.setAllowedHeaders(List.of("*"));

        // Umożliwia przekazywanie poświadczeń i tokenów
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
