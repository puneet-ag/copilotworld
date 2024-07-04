package completion;

public interface CompletionModel {
  String getCode();

  String getDescription();

  int getMaxTokens();
}
