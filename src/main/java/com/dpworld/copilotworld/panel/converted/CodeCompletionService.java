package com.dpworld.copilotworld.panel.converted;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.ServiceManager;
import com.dpworld.copilotworld.completion.CompletionEventListener;
import com.dpworld.copilotworld.model.OllamaSettings;
import okhttp3.sse.EventSource;

@Service(Service.Level.PROJECT)
public final class CodeCompletionService {

    public boolean isCodeCompletionsEnabled(ServiceType selectedService) {
        switch (selectedService) {
            case CODEGPT:
                return ServiceManager.getService(CodeGPTServiceSettings.class)
                        .getState().getCodeCompletionSettings().isCodeCompletionsEnabled();
            case OLLAMA:
                return ServiceManager.getService(OllamaSettings.class).getState().isCodeCompletionsEnabled();
            default:
                return false;
        }
    }

    public EventSource getCodeCompletionAsync(InfillRequestDetails requestDetails,
                                              CompletionEventListener<String> eventListener) {
        ServiceType selectedService = GeneralSettings.getSelectedService();

        switch (selectedService) {
            case OLLAMA:
                return CompletionClientProvider.getOllamaClient()
                        .getCompletionAsync(
                                CodeCompletionRequestFactory.buildOllamaRequest(requestDetails),
                                eventListener);
            default:
                throw new IllegalArgumentException("Code completion not supported for " + selectedService.name());
        }
    }
}
