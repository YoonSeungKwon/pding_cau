package yoon.capstone.application.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.encrypt.AesBytesEncryptor;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import yoon.capstone.application.enums.Role;
import yoon.capstone.application.security.JwtAuthenticationFilter;
import yoon.capstone.application.security.JwtExceptionFilter;
import yoon.capstone.application.security.JwtProvider;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtProvider jwtProvider;

    private final JwtExceptionFilter jwtExceptionFilter;

    @Value("${ALLOW_ORIGIN}")
    private String allowOrigin;

    @Value("${AES_SECRET}")
    private String secret;

    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http

                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())

                .authorizeHttpRequests(auth->{
                    auth.requestMatchers("/api/v1/admin/**").hasRole(Role.ADMIN.name());
                    auth.requestMatchers("/api/v1/friends/**").hasAnyRole(Role.USER.name(), Role.ADMIN.name());
                    auth.requestMatchers("/api/v1/projects/**").hasAnyRole(Role.USER.name(), Role.ADMIN.name());
                    auth.requestMatchers("/api/v1/payment/**").hasAnyRole(Role.USER.name(), Role.ADMIN.name());
                    auth.anyRequest().permitAll();
                })

                .sessionManagement(session ->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .addFilterBefore(new JwtAuthenticationFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtExceptionFilter, JwtAuthenticationFilter.class)

                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("http://localhost:3000", allowOrigin));
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Scope("prototype")
    public AesBytesEncryptor aesBytesEncryptor(){
        return new AesBytesEncryptor(secret, "19981217");
    }


}
