package com.dpworld.copilotworld.llmServer.completion.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class LLMModelInfoResponse {

  private String modelfile;
  private String parameters;
  private String template;
  private LLMResponseModel.OllamaModelDetails details;

  public String getModelfile() {
    return modelfile;
  }

  public void setModelfile(String modelfile) {
    this.modelfile = modelfile;
  }

  public String getParameters() {
    return parameters;
  }

  public void setParameters(String parameters) {
    this.parameters = parameters;
  }

  public String getTemplate() {
    return template;
  }

  public void setTemplate(String template) {
    this.template = template;
  }

  public LLMResponseModel.OllamaModelDetails getDetails() {
    return details;
  }

  public void setDetails(LLMResponseModel.OllamaModelDetails details) {
    this.details = details;
  }
}
