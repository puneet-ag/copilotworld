package com.dpworld.copilotworld.completion;

import com.dpworld.copilotworld.panel.InfillRequestDetails;
import com.dpworld.copilotworld.panel.Placeholder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.components.ServiceManager;
import com.dpworld.copilotworld.model.InfillPromptTemplate;
import com.dpworld.copilotworld.llmServer.LLMSettings;
import com.dpworld.copilotworld.llmServer.LLMStateSettings;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import com.dpworld.copilotworld.llmServer.completion.request.OllamaCompletionRequest;
import com.dpworld.copilotworld.llmServer.completion.request.OllamaParameters;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class CodeCompletionRequestFactory {





















    public static Request buildCustomRequest(
            InfillRequestDetails details,
            String url,
            Map<String, String> headers,
            Map<String, Object> body,
            InfillPromptTemplate infillTemplate,
            String credential
    ) {
        Request.Builder requestBuilder = new Request.Builder().url(url);

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            String value = entry.getValue();
            if (credential != null && value.contains("$CUSTOM_SERVICE_API_KEY")) {
                value = value.replace("$CUSTOM_SERVICE_API_KEY", credential);
            }
            requestBuilder.addHeader(entry.getKey(), value);
        }

        Map<String, Object> transformedBody = new HashMap<>();
        for (Map.Entry<String, Object> entry : body.entrySet()) {
            transformedBody.put(entry.getKey(), transformValue(entry.getValue(), infillTemplate, details));
        }

        try {
            String jsonBody = new ObjectMapper()
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(transformedBody);
            RequestBody requestBody = RequestBody.create(
                    jsonBody.getBytes(StandardCharsets.UTF_8),
                    MediaType.parse("application/json")
            );
            return requestBuilder.post(requestBody).build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }














    public static OllamaCompletionRequest buildOllamaRequest(InfillRequestDetails details) {
        LLMStateSettings settings = ServiceManager.getService(LLMSettings.class).getState();

        return new OllamaCompletionRequest.Builder(
                settings.getModel(),
                settings.getFimTemplate().buildPrompt(details.getPrefix(), details.getSuffix())
        )
                .setOptions(
                        new OllamaParameters.Builder()
                                .stop(settings.getFimTemplate().getStopTokens())
                                .numPredict(getMaxTokens(details.getPrefix(), details.getSuffix()))
                                .temperature(0.4)
                                .build()
                )
                .setRaw(true)
                .build();
    }











    private static Object transformValue(Object value, InfillPromptTemplate template, InfillRequestDetails details) {
        if (!(value instanceof String)) return value;
        String strValue = (String) value;

        if (("$" + Placeholder.FIM_PROMPT).equals(strValue)) {
            return template.buildPrompt(details.getPrefix(), details.getSuffix());
        } else if (("$" + Placeholder.PREFIX).equals(strValue)) {
            return details.getPrefix();
        } else if (("$" + Placeholder.SUFFIX).equals(strValue)) {
            return details.getSuffix();
        } else {
            
            return value;
        }
    }

    private static int getMaxTokens(String prefix, String suffix) {
        if (isBoundaryCharacter(prefix.charAt(prefix.length() - 1)) || isBoundaryCharacter(suffix.charAt(0))) {
            return 16;
        }
        return 36;
    }

    private static boolean isBoundaryCharacter(char c) {
        return "()[]{}<>~!@#$%^&*-+=|\\;:'\",./?".indexOf(c) >= 0;
    }
}

