package com.dpworld.copilotworld.llmServer.completion.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.dpworld.copilotworld.completion.CompletionRequest;
import com.dpworld.copilotworld.llmServer.completion.response.LLMResponseFormat;


@JsonInclude(Include.NON_NULL)
public class LLMRequestCompletion implements CompletionRequest {

  private final String model;
  private final String prompt;

  private final LLMResponseFormat format;
  private final LLMParameters options;
  private final String system;
  private final String template;
  private final String context;
  private final Boolean stream;
  private final Boolean raw;

  public LLMRequestCompletion(Builder builder) {
    this.prompt = builder.prompt;
    this.model = builder.model;
    this.format = builder.format;
    this.options = builder.options;
    this.system = builder.system;
    this.template = builder.template;
    this.context = builder.context;
    this.stream = builder.stream;
    this.raw = builder.raw;
  }

  public String getModel() {
    return model;
  }

  public String getPrompt() {
    return prompt;
  }

  public LLMResponseFormat getFormat() {
    return format;
  }

  public LLMParameters getOptions() {
    return options;
  }

  public String getSystem() {
    return system;
  }

  public String getTemplate() {
    return template;
  }

  public String getContext() {
    return context;
  }

  public Boolean isStream() {
    return stream;
  }

  public Boolean isRaw() {
    return raw;
  }

  public static class Builder {

    private final String model;
    private final String prompt;

    private LLMResponseFormat format = null;
    private LLMParameters options = null;
    private String system = null;
    private String template = null;
    private String context = null;
    private Boolean stream = null;
    private Boolean raw = null;

    public Builder(String model, String prompt) {
      this.model = model;
      this.prompt = prompt;
    }

    public Builder setFormat(LLMResponseFormat format) {
      this.format = format;
      return Builder.this;
    }

    public Builder setOptions(LLMParameters options) {
      this.options = options;
      return Builder.this;
    }

    public Builder setSystem(String system) {
      this.system = system;
      return Builder.this;
    }

    public Builder setTemplate(String template) {
      this.template = template;
      return Builder.this;
    }

    public Builder setContext(String context) {
      this.context = context;
      return Builder.this;
    }

    public Builder setStream(boolean stream) {
      this.stream = stream;
      return Builder.this;
    }

    public Builder setRaw(boolean raw) {
      this.raw = raw;
      return Builder.this;
    }

    public LLMRequestCompletion build() {
      return new LLMRequestCompletion(this);
    }
  }
}
