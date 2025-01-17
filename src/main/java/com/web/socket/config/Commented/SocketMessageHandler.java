//package com.web.socket.config;
//
//import com.fasterxml.jackson.databind.util.JSONPObject;
//import org.bson.json.JsonObject;
//import org.springframework.stereotype.Component;
//import org.springframework.web.socket.TextMessage;
//import org.springframework.web.socket.WebSocketSession;
//import org.springframework.web.socket.handler.TextWebSocketHandler;
//
//import java.io.IOException;
//
//@Component
//public class SocketMessageHandler extends TextWebSocketHandler{
//    @Override
//    public void handleTextMessage(WebSocketSession session, TextMessage message)
//            throws InterruptedException, IOException {
//
//        String payload = message.getPayload();
//        JsonObject jsonObject = new JsonObject(payload);
//        session.sendMessage(new TextMessage("Hi " + jsonObject.get("user") + " how may we help you?"));
//    }
//}
