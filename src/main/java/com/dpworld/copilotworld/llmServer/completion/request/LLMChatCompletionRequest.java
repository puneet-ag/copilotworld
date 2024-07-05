package com.dpworld.copilotworld.llmServer.completion.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.dpworld.copilotworld.llmServer.completion.response.LLMResponseFormat;

import java.util.List;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class LLMChatCompletionRequest {

  private final String model;
  private final List<LLMChatMessageCompletion> messages;
  private final LLMResponseFormat format;
  private final LLMParameters options;
  private final Boolean stream;
  private final String keepAlive;

  public LLMChatCompletionRequest(Builder builder) {
    this.model = builder.model;
    this.messages = builder.messages;
    this.format = builder.format;
    this.options = builder.options;
    this.stream = builder.stream;
    this.keepAlive = builder.keepAlive;
  }

  public String getModel() {
    return model;
  }

  public List<LLMChatMessageCompletion> getMessages() {
    return messages;
  }

  public LLMResponseFormat getFormat() {
    return format;
  }

  public LLMParameters getOptions() {
    return options;
  }

  public Boolean getStream() {
    return stream;
  }

  public String getKeepAlive() {
    return keepAlive;
  }

  public static class Builder {

    private final String model;
    private final List<LLMChatMessageCompletion> messages;

    private LLMResponseFormat format = null;
    private LLMParameters options = null;
    private Boolean stream = null;
    private String keepAlive = null;

    public Builder(String model, List<LLMChatMessageCompletion> messages) {
      this.model = model;
      this.messages = messages;
    }

    public Builder setFormat(LLMResponseFormat format) {
      this.format = format;
      return this;
    }

    public Builder setOptions(LLMParameters options) {
      this.options = options;
      return this;
    }

    public Builder setStream(Boolean stream) {
      this.stream = stream;
      return this;
    }

    public Builder setKeepAlive(String keepAlive) {
      this.keepAlive = keepAlive;
      return this;
    }

    public LLMChatCompletionRequest build() {
      return new LLMChatCompletionRequest(this);
    }
  }
}