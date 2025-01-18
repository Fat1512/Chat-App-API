package com.web.socket.config;

import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Map;

public class ContextCopyingDecorator implements TaskDecorator {
    @Override
    public Runnable decorate(Runnable runnable) {
        // store context in variables which will be bound to the executor thread
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Map<String, String> mdcContextMap = MDC.getCopyOfContextMap();
        return () -> { // code runs inside executor thread and binds context
            try {
                if (requestAttributes != null) {
                    RequestContextHolder.setRequestAttributes(requestAttributes);
                }
                if (securityContext != null) {
                    SecurityContextHolder.setContext(securityContext);
                }
                if (mdcContextMap != null) {
                    MDC.setContextMap(mdcContextMap);
                }
                runnable.run();
            } finally {
                MDC.clear();
                RequestContextHolder.resetRequestAttributes();
                SecurityContextHolder.clearContext();
            }
        };
    }
}
