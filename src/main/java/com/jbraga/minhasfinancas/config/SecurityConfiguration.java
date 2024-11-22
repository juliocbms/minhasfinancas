package com.jbraga.minhasfinancas.config;

import com.jbraga.minhasfinancas.api.dto.JwtTokenFilter;
import com.jbraga.minhasfinancas.service.JwtService;
import com.jbraga.minhasfinancas.service.impl.SecurityUserDetailsService;
import io.jsonwebtoken.lang.Arrays;
import org.apache.catalina.filters.CorsFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collections;
import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfiguration {

    @Autowired
    private SecurityUserDetailsService userDetailsService;

    @Autowired
    private JwtService jwtService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Desativa o CSRF
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/usuarios/autenticar").permitAll() // Permitir acesso público
                        .requestMatchers(HttpMethod.POST, "/api/usuarios").permitAll() // Permitir acesso público
                        .anyRequest().authenticated() // Exigir autenticação para qualquer outra requisição

                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Configura sessão sem estado (stateless)
                )
                .addFilterBefore(jwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
                 // Configuração para HTTP Basic
        return http.build();
    }

    @Bean
    public JwtTokenFilter jwtTokenFilter(){
        return  new JwtTokenFilter(jwtService, userDetailsService);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter(){

        List<String> all = Arrays.asList(new String[]{"*"});

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedMethods(all);
        config.setAllowedOrigins(all);
        config.setAllowedHeaders(all);
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        CorsFilter corFilter = new CorsFilter();

        FilterRegistrationBean<CorsFilter> filter =
                new FilterRegistrationBean<CorsFilter>(corFilter);
        filter.setOrder(Ordered.HIGHEST_PRECEDENCE);

        return filter;
    }

}
