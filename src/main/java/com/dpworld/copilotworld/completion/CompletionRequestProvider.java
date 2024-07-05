package com.dpworld.copilotworld.completion;

import com.dpworld.copilotworld.activity.Conversation;
import com.dpworld.copilotworld.configuration.ConfigurationSettings;
import com.dpworld.copilotworld.configurations.IncludedFilesSettings;
import com.dpworld.copilotworld.conversation.ConversationType;
import com.dpworld.copilotworld.panel.*;
import com.intellij.openapi.application.ApplicationManager;

import com.dpworld.copilotworld.llmServer.LLMSettings;
import com.dpworld.copilotworld.llmServer.completion.request.LLMChatMessageCompletion;
import com.dpworld.copilotworld.llmServer.completion.request.LLMChatCompletionRequest;
import com.dpworld.copilotworld.llmServer.completion.request.LLMParameters;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static com.dpworld.copilotworld.util.FileUtilIntellij.getResourceContent;

public class CompletionRequestProvider {

  public static final String COMPLETION_SYSTEM_PROMPT = getResourceContent(
          "/prompts/default.txt");

  public static final String GENERATE_COMMIT_MESSAGE_SYSTEM_PROMPT = getResourceContent(
          "/prompts/commit.txt");

  public static final String FIX_COMPILE_ERRORS_SYSTEM_PROMPT = getResourceContent(
          "/prompts/compile.txt");

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

  public LLMChatCompletionRequest buildOllamaChatCompletionRequest(
      CallParameters callParameters
  ) {
    var configuration = ConfigurationSettings.getCurrentState();
    var settings = ApplicationManager.getApplication().getService(LLMSettings.class).getState();
    return new LLMChatCompletionRequest
        .Builder(settings.getModel(), buildOllamaMessages(callParameters))
        .setStream(true)
        .setOptions(new LLMParameters.Builder()
            .numPredict(configuration.getMaxTokens())
            .temperature(configuration.getTemperature())
            .build())
        .build();
  }

  private List<LLMChatMessageCompletion> buildOllamaMessages(CallParameters callParameters) {
    var message = callParameters.getMessage();
    var messages = new ArrayList<LLMChatMessageCompletion>();
    if (callParameters.getConversationType() == ConversationType.DEFAULT) {
      String systemPrompt = ConfigurationSettings.getCurrentState().getSystemPrompt();
      messages.add(new LLMChatMessageCompletion("system", systemPrompt, null));
    }
    if (callParameters.getConversationType() == ConversationType.FIX_COMPILE_ERRORS) {
      messages.add(
          new LLMChatMessageCompletion("system", FIX_COMPILE_ERRORS_SYSTEM_PROMPT, null)
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
              new LLMChatMessageCompletion(
                  "user", prevMessage.getPrompt(), List.of(imageBase64)
              )
          );
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      } else {
        messages.add(
            new LLMChatMessageCompletion("user", prevMessage.getPrompt(), null)
        );
      }
      messages.add(
          new LLMChatMessageCompletion("assistant", prevMessage.getResponse(), null)
      );
    }

    if (callParameters.getImageMediaType() != null && callParameters.getImageData().length > 0) {
      var imageBase64 = Base64.getEncoder().encodeToString(callParameters.getImageData());
      messages.add(
          new LLMChatMessageCompletion("user", message.getPrompt(), List.of(imageBase64))
      );
    } else {
      messages.add(new LLMChatMessageCompletion("user", message.getPrompt(), null));
    }
    return messages;
  }

}
