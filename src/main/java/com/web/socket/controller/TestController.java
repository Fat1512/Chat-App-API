package com.web.socket.controller;


import com.web.socket.dto.response.APIResponse;
import com.web.socket.entity.MessageHistory;
import com.web.socket.service.ChatRoomService;
import com.web.socket.service.testService;
import com.web.socket.utils.APIResponseMessage;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/test")
@RequiredArgsConstructor
public class TestController {

    private final testService service;
    private final ChatRoomService chatRoomService;
    @GetMapping("/")
    public ResponseEntity<APIResponse>  x() throws Exception {
//        List<MessageHistory> msgHistory = chatRoomService.getChatRoomDetailz("6778e05b4c99357fe1f32a41", 10, 180, 10);

        APIResponse apiResponse = APIResponse.builder()
                .status(HttpStatus.OK)
                .message(APIResponseMessage.SUCCESSFULLY_RETRIEVED.name())
                .data(null)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
//    @PostMapping("/gemini")
//    public ResponseEntity<APIResponse> sendMessage(@RequestBody GeminiRequest geminiRequest) {
//        String projectId = "totemic-ally-417012";
//        String location = "asia-southeast1";
////        String modelName = "gemini-1.5-flash-002";
//        String modelName = geminiRequest.getModelName();
//        String textPrompt = geminiRequest.getText();
//        String output = "";
//        try (VertexAI vertexAI = new VertexAI(projectId, location)) {
//            GenerativeModel model = new GenerativeModel(modelName, vertexAI);
//
//            GenerateContentResponse response = model.generateContent(textPrompt);
//            output = ResponseHandler.getText(response);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        APIResponse apiResponse = APIResponse.builder()
//                .status(HttpStatus.OK)
//                .message(APIResponseMessage.SUCCESSFULLY_RETRIEVED.name())
//                .data(output)
//                .build();
//        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
//    }
}
