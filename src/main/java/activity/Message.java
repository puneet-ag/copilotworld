package activity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Message {

  private final UUID id;
  private String prompt;
  private String response;
  private String userMessage;
  private List<YouSerpResult> serpResults;
  private List<String> referencedFilePaths;
  private @Nullable String imageFilePath;

  public Message(String prompt, String response) {
    this(prompt);
    this.response = response;
  }

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public Message(@JsonProperty("prompt") String prompt) {
    this.id = UUID.randomUUID();
    this.prompt = prompt;
  }

  public UUID getId() {
    return id;
  }

  public String getPrompt() {
    return prompt;
  }

  public void setPrompt(String prompt) {
    this.prompt = prompt;
  }

  public String getResponse() {
    return response;
  }

  public void setResponse(String response) {
    this.response = response;
  }

  public String getUserMessage() {
    return userMessage;
  }

  public void setUserMessage(String userMessage) {
    this.userMessage = userMessage;
  }

  public List<YouSerpResult> getSerpResults() {
    return serpResults;
  }

  public void setSerpResults(List<YouSerpResult> serpResults) {
    this.serpResults = serpResults;
  }

  public List<String> getReferencedFilePaths() {
    return referencedFilePaths;
  }

  public void setReferencedFilePaths(List<String> referencedFilePaths) {
    this.referencedFilePaths = referencedFilePaths;
  }

  public @Nullable String getImageFilePath() {
    return imageFilePath;
  }

  public void setImageFilePath(@Nullable String imageFilePath) {
    this.imageFilePath = imageFilePath;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof Message other)) {
      return false;
    }
    return Objects.equals(id, other.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, prompt);
  }
}
