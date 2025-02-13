package com.daruda.darudaserver.global.config;

import com.daruda.darudaserver.global.auth.jwt.provider.JwtTokenProvider;
import com.daruda.darudaserver.global.auth.security.ExceptionHandlerFilter;
import com.daruda.darudaserver.global.auth.security.JwtAuthenticationEntryPoint;
import com.daruda.darudaserver.global.auth.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtTokenProvider jwtTokenProvider;

    private static final String[] WHITE_LIST = {
            "/api/v1/users/signin",
            "/api/v1/users/token",
            "/api/v1/users/signup",
            "/api/v1/users/nickname",
            "/api/v1/tools/**",
            "/api/v1/comments",
            "/api/v1/boards/board/**",
            "/api/v1/users/kakao/login-url",
            "/v3/api-docs", "/swagger-resources/**", "/swagger-ui.html", "/webjars/**", "/swagger/**", "/swagger-ui/index.html"};

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
         http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagementConfigurer ->
                        sessionManagementConfigurer
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptionHandlingConfigurer ->
                        exceptionHandlingConfigurer
                                .authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry ->
                        authorizationManagerRequestMatcherRegistry
                                .requestMatchers("/v3/api-docs", "/v3/api-docs/**", "/swagger-resources/**", "/swagger-ui.html", "/webjars/**", "/swagger/**", "/swagger-ui/index.html", "/swagger-ui/**").permitAll()
                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() //OPTION 추가
                                .anyRequest()
                                .authenticated())
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new    ExceptionHandlerFilter(), JwtAuthenticationFilter.class);

         http.cors(cors->cors.configurationSource(CorsConfig.configurationSource()));

         return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers(WHITE_LIST);
    }
}
