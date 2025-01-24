//package com.web.socket.config;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//import org.springframework.web.socket.TextMessage;
//import org.springframework.web.socket.WebSocketSession;
//import org.springframework.web.socket.handler.TextWebSocketHandler;
//
//import java.io.IOException;
//
//@Component
//@Slf4j
//public class SocketMessageHandler extends TextWebSocketHandler{
//    @Override
//    public void handleTextMessage(WebSocketSession session, TextMessage message)
//            throws InterruptedException, IOException {
//        log.info("my session id: {}", session.getId());
//        session.sendMessage(message);
////        String payload = message.getPayload();
////        JsonObject jsonObject = new JsonObject(payload);
////        session.sendMessage(new TextMessage("Hi " + jsonObject.get("user") + " how may we help you?"));
//    }
//}
