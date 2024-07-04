package com.dpworld.copilotworld.panel.converted;

import com.intellij.DynamicBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

public class VisionBundle extends DynamicBundle {

  private static final VisionBundle INSTANCE = new VisionBundle();

  private VisionBundle() {
    super("messages.vision");
  }

  public static String get(
      @NotNull @PropertyKey(resourceBundle = "messages.vision") String key,
      Object... params) {
    return INSTANCE.getMessage(key, params);
  }
}
