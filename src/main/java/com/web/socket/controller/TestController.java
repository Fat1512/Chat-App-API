package com.web.socket.controller;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test")
public class TestController {

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
