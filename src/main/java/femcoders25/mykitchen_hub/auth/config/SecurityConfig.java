package femcoders25.mykitchen_hub.auth.config;

import femcoders25.mykitchen_hub.auth.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthFilter;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http,
                        AuthenticationManager authenticationManager) throws Exception {
                http
                                .csrf(AbstractHttpConfigurer::disable)
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                                .authorizeHttpRequests(auth -> auth
                                                // Public endpoints (read-only)
                                                .requestMatchers(HttpMethod.GET, "/api/recipes/**").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/ingredients/**").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/recipes/*/comments/**")
                                                .permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/recipes/*/ratings/**")
                                                .permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/favorites/recipes/*/count")
                                                .permitAll()

                                                // Public auth endpoints (register and login)
                                                .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
                                                .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()

                                                // Protected auth endpoints (logout requires authentication)
                                                .requestMatchers(HttpMethod.POST, "/api/auth/logout").authenticated()

                                                .requestMatchers("/ws/**").permitAll()
                                                .requestMatchers("/actuator/**").permitAll()
                                                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                                                // Admin-only
                                                .requestMatchers("/api/users").hasRole("ADMIN")
                                                .requestMatchers("/api/users/search").hasRole("ADMIN")
                                                .requestMatchers("/api/users/{id}/role").hasRole("ADMIN")
                                                .requestMatchers("/api/chat/messages/user/{userId}").hasRole("ADMIN")

                                                // Authenticated users (USER, ADMIN)
                                                .requestMatchers(HttpMethod.POST, "/api/recipes/**")
                                                .hasAnyRole("USER", "ADMIN")
                                                .requestMatchers(HttpMethod.PUT, "/api/recipes/**")
                                                .hasAnyRole("USER", "ADMIN")
                                                .requestMatchers(HttpMethod.DELETE, "/api/recipes/**")
                                                .hasAnyRole("USER", "ADMIN")

                                                .requestMatchers(HttpMethod.POST, "/api/ingredients/**")
                                                .hasAnyRole("USER", "ADMIN")
                                                .requestMatchers(HttpMethod.PUT, "/api/ingredients/**")
                                                .hasAnyRole("USER", "ADMIN")
                                                .requestMatchers(HttpMethod.DELETE, "/api/ingredients/**")
                                                .hasAnyRole("USER", "ADMIN")

                                                .requestMatchers("/api/users/{id}").hasAnyRole("USER", "ADMIN")
                                                .requestMatchers("/api/users/profile").hasAnyRole("USER", "ADMIN")
                                                .requestMatchers("/api/shopping-lists/**").hasAnyRole("USER", "ADMIN")
                                                .requestMatchers("/api/favorites/my/**").hasAnyRole("USER", "ADMIN")
                                                .requestMatchers("/api/favorites/recipes/{recipeId}")
                                                .hasAnyRole("USER", "ADMIN")
                                                .requestMatchers("/api/favorites/recipes/{recipeId}/is-favorited")
                                                .hasAnyRole("USER", "ADMIN")
                                                .requestMatchers("/api/chat/messages/**").hasAnyRole("USER", "ADMIN")
                                                .anyRequest().authenticated())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authenticationManager(authenticationManager)
                                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOriginPatterns(Arrays.asList(
                                "http://localhost:3000",
                                "https://myfrontend.com"));
                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                configuration.setAllowedHeaders(List.of("*"));
                configuration.setAllowCredentials(true);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                return source;
        }
}
