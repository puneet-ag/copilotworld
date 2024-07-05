package com.dpworld.copilotworld.llmServer.completion.response;

import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LLMChatCompletionMessageResponse {

  private String role;
  private String content;
  private List<String> images;

  public LLMChatCompletionMessageResponse() {
  }

  public LLMChatCompletionMessageResponse(@NotNull String role, @NotNull String content,
                                          @Nullable List<String> images) {
    this.role = role;
    this.content = content;
    this.images = images;
  }

  @NotNull
  public String getRole() {
    return role;
  }

  public void setRole(@NotNull String role) {
    this.role = role;
  }

  @NotNull
  public String getContent() {
    return content;
  }

  public void setContent(@NotNull String content) {
    this.content = content;
  }

  @Nullable
  public List<String> getImages() {
    return images;
  }

  public void setImages(@Nullable List<String> images) {
    this.images = images;
  }
}
