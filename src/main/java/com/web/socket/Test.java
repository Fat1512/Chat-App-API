package com.web.socket;

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.ResponseHandler;

import java.io.IOException;

public class Test {
    public static void main(String[] args) throws IOException {
        String projectId = "totemic-ally-417012";
        String location = "asia-southeast1";
        String modelName = "gemini-1.5-flash-001";
        String textPrompt = "Current weather in HCMC";
        try (VertexAI vertexAI = new VertexAI(projectId, location)) {
            GenerativeModel model = new GenerativeModel(modelName, vertexAI);

            GenerateContentResponse response = model.generateContent(textPrompt);
            String output = ResponseHandler.getText(response);
            System.out.println(output);
        }
    }
}
