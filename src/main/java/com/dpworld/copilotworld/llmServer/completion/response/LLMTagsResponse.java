package com.dpworld.copilotworld.llmServer.completion.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class LLMTagsResponse {

  private List<LLMResponseModel> models;

  public List<LLMResponseModel> getModels() {
    return models;
  }

  public void setModels(List<LLMResponseModel> models) {
    this.models = models;
  }
}
