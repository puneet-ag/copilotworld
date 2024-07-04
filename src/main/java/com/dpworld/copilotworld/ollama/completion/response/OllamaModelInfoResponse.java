package com.dpworld.copilotworld.ollama.completion.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

/**
 * see <a
 * href="https://github.com/ollama/ollama/blob/main/docs/api.md#show-model-information">ollama/api</a>.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OllamaModelInfoResponse {

  private String modelfile;
  private String parameters;
  private String template;
  private OllamaModel.OllamaModelDetails details;

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

  public OllamaModel.OllamaModelDetails getDetails() {
    return details;
  }

  public void setDetails(OllamaModel.OllamaModelDetails details) {
    this.details = details;
  }
}
