package com.dpworld.copilotworld.llmServer.completion.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;


@JsonInclude(Include.NON_NULL)
public class OllamaEmbeddingRequest {

  private final String model;
  private final String prompt;
  private final OllamaParameters options;

  public OllamaEmbeddingRequest(Builder builder) {
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

  public OllamaParameters getOptions() {
    return options;
  }

  public static class Builder {

    private final String model;
    private final String prompt;
    private OllamaParameters options = null;

    public Builder(String model, String prompt) {
      this.model = model;
      this.prompt = prompt;
    }

    public OllamaEmbeddingRequest.Builder setOptions(OllamaParameters options) {
      this.options = options;
      return OllamaEmbeddingRequest.Builder.this;
    }

    public OllamaEmbeddingRequest build() {
      return new OllamaEmbeddingRequest(this);
    }
  }
}