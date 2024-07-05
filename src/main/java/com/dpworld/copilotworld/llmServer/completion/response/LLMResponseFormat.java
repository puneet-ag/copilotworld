package com.dpworld.copilotworld.llmServer.completion.response;

import com.fasterxml.jackson.annotation.JsonValue;

public enum LLMResponseFormat {
  JSON("json");

  private final String value;

  LLMResponseFormat(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }
}
