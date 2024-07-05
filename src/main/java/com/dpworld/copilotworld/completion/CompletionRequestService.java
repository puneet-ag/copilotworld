package com.dpworld.copilotworld.completion;

import com.dpworld.copilotworld.llmServer.LLMSettings;
import com.dpworld.copilotworld.llmServer.completion.request.LLMChatMessageCompletion;
import com.dpworld.copilotworld.llmServer.completion.request.LLMChatCompletionRequest;
import com.dpworld.copilotworld.panel.CallParameters;
import com.dpworld.copilotworld.configuration.ConfigurationSettings;
import com.dpworld.copilotworld.configurations.GeneralSettings;
import com.dpworld.copilotworld.panel.ServiceType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.diagnostic.Logger;

import java.util.List;
import java.util.Optional;

import okhttp3.sse.EventSource;

@Service
public final class CompletionRequestService {

  private static final Logger LOG = Logger.getInstance(CompletionRequestService.class);

  private CompletionRequestService() {
  }

  public static CompletionRequestService getInstance() {
    return ApplicationManager.getApplication().getService(CompletionRequestService.class);
  }



  public EventSource getChatCompletionAsync(
          CallParameters callParameters,
          CompletionEventListener<String> eventListener) {
    var application = ApplicationManager.getApplication();
    var requestProvider = new CompletionRequestProvider(callParameters.getConversation());
    return CompletionClientProvider.getOllamaClient().getChatCompletionAsync(
              requestProvider.buildOllamaChatCompletionRequest(callParameters),
              eventListener);
    };

  public void generateCommitMessageAsync(
          String systemPrompt,
          String gitDiff,
          CompletionEventListener<String> eventListener) {
    var configuration = ConfigurationSettings.getCurrentState();

    var selectedService = GeneralSettings.getSelectedService();
    switch (selectedService) {


      case OLLAMA:
        var model = ApplicationManager.getApplication()
                .getService(LLMSettings.class)
                .getState()
                .getModel();
        var request = new LLMChatCompletionRequest.Builder(
                model,
                List.of(
                        new LLMChatMessageCompletion("system", systemPrompt, null),
                        new LLMChatMessageCompletion("user", gitDiff, null)
                )
        ).build();
        CompletionClientProvider.getOllamaClient().getChatCompletionAsync(request, eventListener);
        break;

      default:
        
        break;
    }
  }

  public Optional<String> getLookupCompletion(String prompt) {
        return Optional.empty();
  }

  public boolean isAllowed() {
    return isRequestAllowed();
  }

  public static boolean isRequestAllowed() {
    return isRequestAllowed(GeneralSettings.getSelectedService());
  }

  public static boolean isRequestAllowed(ServiceType serviceType) {
    return true;
  }

}