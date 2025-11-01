package com.unsl.sgeu.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class SessionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        HttpSession session = request.getSession(false);

        // URLs que no requieren sesión (estaticos)
        String uri = request.getRequestURI();
        if (uri.startsWith("/login") || uri.startsWith("/css") || uri.startsWith("/js")) {
            return true;
        }

        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("/login");
            return false; 
        }

        return true; // Continuar al controlador
    }
}
