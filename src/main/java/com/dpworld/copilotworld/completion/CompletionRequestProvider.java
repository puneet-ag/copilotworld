package com.dpworld.copilotworld.completion;

import com.dpworld.copilotworld.activity.Conversation;
import com.dpworld.copilotworld.configuration.ConfigurationSettings;
import com.dpworld.copilotworld.configurations.IncludedFilesSettings;
import com.dpworld.copilotworld.conversation.ConversationType;
import com.dpworld.copilotworld.panel.*;
import com.intellij.openapi.application.ApplicationManager;

import com.dpworld.copilotworld.ollama.OllamaSettings;
import com.dpworld.copilotworld.ollama.completion.request.OllamaChatCompletionMessage;
import com.dpworld.copilotworld.ollama.completion.request.OllamaChatCompletionRequest;
import com.dpworld.copilotworld.ollama.completion.request.OllamaParameters;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static com.dpworld.copilotworld.util.IntellijFileUtil.getResourceContent;

public class CompletionRequestProvider {

  public static final String COMPLETION_SYSTEM_PROMPT = getResourceContent(
      "/prompts/default-completion-system-prompt.txt");

  public static final String GENERATE_COMMIT_MESSAGE_SYSTEM_PROMPT = getResourceContent(
      "/prompts/generate-commit-message-system-prompt.txt");

  public static final String FIX_COMPILE_ERRORS_SYSTEM_PROMPT = getResourceContent(
      "/prompts/fix-compile-errors.txt");

  private final EncodingManager encodingManager = EncodingManager.getInstance();
  private final Conversation conversation;

  public CompletionRequestProvider(Conversation conversation) {
    this.conversation = conversation;
  }

  public static String getPromptWithContext(List<ReferencedFile> referencedFiles,
      String userPrompt) {
    var includedFilesSettings = IncludedFilesSettings.getCurrentState();
    var repeatableContext = referencedFiles.stream()
        .map(item -> includedFilesSettings.getRepeatableContext()
            .replace("{FILE_PATH}", item.getFilePath())
            .replace("{FILE_CONTENT}", format(
                "```%s%n%s%n```",
                item.getFileExtension(),
                item.getFileContent().trim())))
        .collect(joining("\n\n"));

    return includedFilesSettings.getPromptTemplate()
            .replace("{REPEATABLE_CONTEXT}", repeatableContext)
        .replace("{QUESTION}", userPrompt);
  }

  public OllamaChatCompletionRequest buildOllamaChatCompletionRequest(
      CallParameters callParameters
  ) {
    var configuration = ConfigurationSettings.getCurrentState();
    var settings = ApplicationManager.getApplication().getService(OllamaSettings.class).getState();
    return new OllamaChatCompletionRequest
        .Builder(settings.getModel(), buildOllamaMessages(callParameters))
        .setStream(true)
        .setOptions(new OllamaParameters.Builder()
            .numPredict(configuration.getMaxTokens())
            .temperature(configuration.getTemperature())
            .build())
        .build();
  }

  private List<OllamaChatCompletionMessage> buildOllamaMessages(CallParameters callParameters) {
    var message = callParameters.getMessage();
    var messages = new ArrayList<OllamaChatCompletionMessage>();
    if (callParameters.getConversationType() == ConversationType.DEFAULT) {
      String systemPrompt = ConfigurationSettings.getCurrentState().getSystemPrompt();
      messages.add(new OllamaChatCompletionMessage("system", systemPrompt, null));
    }
    if (callParameters.getConversationType() == ConversationType.FIX_COMPILE_ERRORS) {
      messages.add(
          new OllamaChatCompletionMessage("system", FIX_COMPILE_ERRORS_SYSTEM_PROMPT, null)
      );
    }

    for (var prevMessage : conversation.getMessages()) {
      if (callParameters.isRetry() && prevMessage.getId().equals(message.getId())) {
        break;
      }
      var prevMessageImageFilePath = prevMessage.getImageFilePath();
      if (prevMessageImageFilePath != null && !prevMessageImageFilePath.isEmpty()) {
        try {
          var imageFilePath = Path.of(prevMessageImageFilePath);
          var imageBytes = Files.readAllBytes(imageFilePath);
          var imageBase64 = Base64.getEncoder().encodeToString(imageBytes);
          messages.add(
              new OllamaChatCompletionMessage(
                  "user", prevMessage.getPrompt(), List.of(imageBase64)
              )
          );
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      } else {
        messages.add(
            new OllamaChatCompletionMessage("user", prevMessage.getPrompt(), null)
        );
      }
      messages.add(
          new OllamaChatCompletionMessage("assistant", prevMessage.getResponse(), null)
      );
    }

    if (callParameters.getImageMediaType() != null && callParameters.getImageData().length > 0) {
      var imageBase64 = Base64.getEncoder().encodeToString(callParameters.getImageData());
      messages.add(
          new OllamaChatCompletionMessage("user", message.getPrompt(), List.of(imageBase64))
      );
    } else {
      messages.add(new OllamaChatCompletionMessage("user", message.getPrompt(), null));
    }
    return messages;
  }

}
