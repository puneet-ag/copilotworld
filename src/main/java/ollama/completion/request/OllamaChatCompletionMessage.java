package ollama.completion.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OllamaChatCompletionMessage {

  private final String role;
  private final String content;
  private final List<String> images;

  public OllamaChatCompletionMessage(@NotNull String role, @NotNull String content,
      @Nullable List<String> images) {
    this.role = role;
    this.content = content;
    this.images = images;
  }

  @NotNull
  public String getRole() {
    return role;
  }

  @NotNull
  public String getContent() {
    return content;
  }

  @Nullable
  public List<String> getImages() {
    return images;
  }
}
