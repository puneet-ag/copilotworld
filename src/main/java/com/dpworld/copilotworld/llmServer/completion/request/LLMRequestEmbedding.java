package com.dpworld.copilotworld.llmServer.completion.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;


@JsonInclude(Include.NON_NULL)
public class LLMRequestEmbedding {

  private final String model;
  private final String prompt;
  private final LLMParameters options;

  public LLMRequestEmbedding(Builder builder) {
    this.prompt = builder.prompt;
    this.model = builder.model;
    this.options = builder.options;
  }

  public String getModel() {
    return model;
  }

  public String getPrompt() {
    return prompt;
  }

  public LLMParameters getOptions() {
    return options;
  }

  public static class Builder {

    private final String model;
    private final String prompt;
    private LLMParameters options = null;

    public Builder(String model, String prompt) {
      this.model = model;
      this.prompt = prompt;
    }

    public LLMRequestEmbedding.Builder setOptions(LLMParameters options) {
      this.options = options;
      return LLMRequestEmbedding.Builder.this;
    }

    public LLMRequestEmbedding build() {
      return new LLMRequestEmbedding(this);
    }
  }
}