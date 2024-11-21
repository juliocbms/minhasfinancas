package com.jbraga.minhasfinancas.api.dto;

import com.jbraga.minhasfinancas.model.entity.Usuario;
import com.jbraga.minhasfinancas.service.JwtService;
import com.jbraga.minhasfinancas.service.impl.SecurityUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtTokenFilter extends OncePerRequestFilter {

    private JwtService jwtService;
    private  SecurityUserDetailsService userDatailsService;

    public JwtTokenFilter(
            JwtService jwtService,
            SecurityUserDetailsService userDetailsService
    ){
        this.jwtService = jwtService;
        this.userDatailsService = userDetailsService;

    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authorization = request.getHeader("Authorization");

        if (authorization != null && authorization.startsWith("Bearer")){

            String token = authorization.split(" ")[1];
            boolean isTokenValid = jwtService.isTokenValido(token);

            if (isTokenValid){
                String login = jwtService.obterLoginUsuraio(token);
               UserDetails usuarioAutenticado = userDatailsService.loadUserByUsername(login);
                UsernamePasswordAuthenticationToken user = new UsernamePasswordAuthenticationToken(
                        usuarioAutenticado, null, usuarioAutenticado.getAuthorities());
                user.setDetails(new WebAuthenticationDetailsSource().buildDetails(request) );
                SecurityContextHolder.getContext().setAuthentication(user);


            }
        }
        filterChain.doFilter(request, response);
    }
}
