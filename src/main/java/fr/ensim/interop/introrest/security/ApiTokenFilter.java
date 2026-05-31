package fr.ensim.interop.introrest.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.ensim.interop.introrest.oas.model.ErrorResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class ApiTokenFilter extends OncePerRequestFilter {
    private final ObjectMapper objectMapper;

    @Value("${api.access.token:change-me}")
    private String expectedToken;

    public ApiTokenFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getRequestURI();
        if (path.startsWith("/actuator") || path.equals("/error")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = request.getHeader("X-API-Token");
        String authorization = request.getHeader("Authorization");
        if (!StringUtils.hasText(token) && StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
            token = authorization.substring("Bearer ".length());
        }
        if (!StringUtils.hasText(token)) {
            token = request.getParameter("access_token");
        }

        if (StringUtils.hasText(expectedToken) && expectedToken.equals(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        ErrorResponse body = new ErrorResponse()
                .status(HttpStatus.UNAUTHORIZED.value())
                .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .message("Token d'acces manquant ou invalide");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
