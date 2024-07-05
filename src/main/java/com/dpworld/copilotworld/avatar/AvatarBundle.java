package com.dpworld.copilotworld.avatar;

import com.intellij.DynamicBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

public class AvatarBundle extends DynamicBundle {

  private static final AvatarBundle INSTANCE = new AvatarBundle();

  private AvatarBundle() {
    super("messages.copilot");
  }

  public static String get(
      @NotNull @PropertyKey(resourceBundle = "messages.copilot") String key,
      Object... params) {
    return INSTANCE.getMessage(key, params);
  }
}
