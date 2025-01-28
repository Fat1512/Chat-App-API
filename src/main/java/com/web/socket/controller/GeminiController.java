package com.web.socket.controller;


import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.ResponseHandler;
import com.web.socket.dto.request.MessageRequest;
import com.web.socket.dto.response.APIResponse;
import com.web.socket.utils.APIResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/chatbot")
public class GeminiController {
    @PostMapping("/gemini")
    public ResponseEntity<APIResponse> sendMessage(@RequestBody MessageRequest messageRequest) {
        String projectId = "totemic-ally-417012";
        String location = "asia-southeast1";
        String modelName = "gemini-1.5-flash-002";
        String textPrompt = messageRequest.getContent();
        String output = "";
        try (VertexAI vertexAI = new VertexAI(projectId, location)) {
            GenerativeModel model = new GenerativeModel(modelName, vertexAI);

            GenerateContentResponse response = model.generateContent(textPrompt);
            output = ResponseHandler.getText(response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        APIResponse apiResponse = APIResponse.builder()
                .status(HttpStatus.OK)
                .message(APIResponseMessage.SUCCESSFULLY_RETRIEVED.name())
                .data(output)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
