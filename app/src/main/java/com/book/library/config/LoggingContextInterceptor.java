package com.book.library.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.servlet.HandlerInterceptor;

class LoggingContextInterceptor implements HandlerInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingContextInterceptor.class);

    @Override
    public boolean preHandle(
            @NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = getUserIdFromPrincipal(authentication.getPrincipal());
        MDC.put("userId", userId);

        return true;
    }

    private String getUserIdFromPrincipal(Object principal) {
        if (principal instanceof String) {
            // anonymous users will have a String principal with value "anonymousUser"
            return principal.toString();
        }

        if (principal instanceof OidcUser user) {
            try {
                if (user.getPreferredUsername() != null) {
                    return user.getPreferredUsername();
                } else if (user.getClaimAsString("name") != null) {
                    return user.getClaimAsString("name");
                } else {
                    LOG.warn("could not extract userId from Principal");
                    return "unknown";
                }
            } catch (Exception e) {
                LOG.warn("could not extract userId from Principal", e);
            }
        }
        return "unknown";
    }

    @Override
    public void afterCompletion(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler,
            Exception ex) {
        MDC.clear();
    }
}
