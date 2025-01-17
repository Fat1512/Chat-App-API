//package com.web.socket.config;
//
//import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//
//@Component
//public class SharedThreadConfig extends ThreadPoolTaskExecutor {
//    @Override
//    public void execute(Runnable task) {
//        final Authentication a = SecurityContextHolder.getContext().getAuthentication();
//
//        super.execute(() -> {
//            try {
//                SecurityContext ctx = SecurityContextHolder.createEmptyContext();
//                ctx.setAuthentication(a);
//                SecurityContextHolder.setContext(ctx);
//                task.run();
//            } finally {
//                SecurityContextHolder.clearContext();
//            }
//        });
//    }
//}
