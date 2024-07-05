package com.dpworld.copilotworld.completion;


import org.treesitter.TSLanguage;
import org.treesitter.TreeSitterJava;

public class CodeCompletionParserFactory {

  public static CodeCompletionParser getParserForFileExtension(String extension)
      throws IllegalArgumentException {
    return new CodeCompletionParser(getLanguageForExtension(extension));
  }

  private static TSLanguage getLanguageForExtension(String extension) {
    return switch (extension) {
      case "java" -> new TreeSitterJava();
      default -> throw new IllegalArgumentException("Unsupported file extension: " + extension);
    };
  }
}
