package com.dpworld.copilotworld.panel.converted;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.components.ServiceManager;
import com.dpworld.copilotworld.model.InfillPromptTemplate;
import com.dpworld.copilotworld.model.OllamaSettings;
import com.dpworld.copilotworld.model.OllamaSettingsState;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import com.dpworld.copilotworld.ollama.completion.request.OllamaCompletionRequest;
import com.dpworld.copilotworld.ollama.completion.request.OllamaParameters;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class CodeCompletionRequestFactory {



//    public static Request buildCustomRequest(InfillRequestDetails details) {
//        CustomServiceSettings.CodeCompletionSettings settings = ServiceManager
//                .getService(CustomServiceSettings.class)
//                .getState()
//                .getCodeCompletionSettings();
//
//        String credential = CredentialsStore.getCredential(CredentialsStore.CredentialKey.CUSTOM_SERVICE_API_KEY);
//
//        return buildCustomRequest(
//                details,
//                settings.getUrl(),
//                settings.getHeaders(),
//                settings.getBody(),
//                settings.getInfillTemplate(),
//                credential
//        );
//    }

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

//    public static LlamaCompletionRequest buildLlamaRequest(InfillRequestDetails details) {
//        LlamaSettingsState settings = LlamaSettings.getCurrentState();
//        InfillPromptTemplate promptTemplate = getLlamaInfillPromptTemplate(settings);
//        String prompt = promptTemplate.buildPrompt(details.getPrefix(), details.getSuffix());
//
//        return new LlamaCompletionRequest.Builder(prompt)
//                .setN_predict(getMaxTokens(details.getPrefix(), details.getSuffix()))
//                .setStream(true)
//                .setTemperature(0.4)
//                .setStop(promptTemplate.getStopTokens())
//                .build();
//    }

    public static OllamaCompletionRequest buildOllamaRequest(InfillRequestDetails details) {
        OllamaSettingsState settings = ServiceManager.getService(OllamaSettings.class).getState();

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

//    private static InfillPromptTemplate getLlamaInfillPromptTemplate(LlamaSettingsState settings) {
//        if (!settings.isRunLocalServer()) {
//            return settings.getRemoteModelInfillPromptTemplate();
//        }
//        if (settings.isUseCustomModel()) {
//            return settings.getLocalModelInfillPromptTemplate();
//        }
//        return LlamaModel.findByHuggingFaceModel(settings.getHuggingFaceModel()).getInfillPromptTemplate();
//    }

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
            // Handle default case if necessary
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

