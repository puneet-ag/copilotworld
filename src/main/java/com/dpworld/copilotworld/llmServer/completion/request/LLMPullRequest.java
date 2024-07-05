package com.dpworld.copilotworld.llmServer.completion.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;


@JsonInclude(Include.NON_NULL)
public class LLMPullRequest {

  private final String name;
  private final boolean stream;

  public LLMPullRequest(String name, boolean stream) {
    this.name = name;
    this.stream = stream;
  }

  public String getName() {
    return name;
  }

  public boolean isStream() {
    return stream;
  }
}
