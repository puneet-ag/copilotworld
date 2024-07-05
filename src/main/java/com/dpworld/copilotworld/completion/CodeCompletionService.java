package com.dpworld.copilotworld.completion;

import com.dpworld.copilotworld.avatar.AvatarServiceSettings;
import com.dpworld.copilotworld.configurations.GeneralSettings;
import com.dpworld.copilotworld.panel.InfillRequestDetails;
import com.dpworld.copilotworld.panel.ServiceType;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.ServiceManager;
import com.dpworld.copilotworld.llmServer.LLMSettings;
import okhttp3.sse.EventSource;

@Service(Service.Level.PROJECT)
public final class CodeCompletionService {

    public boolean isCodeCompletionsEnabled(ServiceType selectedService) {
        switch (selectedService) {
            case AVATAR:
                return ServiceManager.getService(AvatarServiceSettings.class)
                        .getState().getCodeCompletionSettings().isCodeCompletionsEnabled();
            case OLLAMA:
                return ServiceManager.getService(LLMSettings.class).getState().isCodeCompletionsEnabled();
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

