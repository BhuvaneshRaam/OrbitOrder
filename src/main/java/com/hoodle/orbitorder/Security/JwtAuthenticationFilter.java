package com.hoodle.orbitorder.Security;

import com.hoodle.orbitorder.DTO.UserContext;
import com.hoodle.orbitorder.Service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if(authHeader != null & authHeader.startsWith("Bearer")) {
            String token = authHeader.substring(7);
            System.out.println("1. Token received!");

            if(jwtService.validateToken(token)) {
                System.out.println("2. Token signature is valid!");
                String userIdString = jwtService.extractUserId(token);
                String tenantIdString = jwtService.extractClaim(token, "tenantUuid");
                System.out.println("3. Extracted User: " + userIdString + " | Tenant: " + tenantIdString);



                UserContext userContext = new UserContext(UUID.fromString(userIdString), UUID.fromString(tenantIdString));

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userContext, null, new ArrayList<>());

                SecurityContextHolder.getContext().setAuthentication(authToken);
                System.out.println("4. Authentication successfully set in context!");
            }
            else {
                System.out.println("FAILED: Token validation failed.");
            }
        }
        filterChain.doFilter(request,response);
    }
}
