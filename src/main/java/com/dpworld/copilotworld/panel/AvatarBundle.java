package com.dpworld.copilotworld.panel;

import com.intellij.DynamicBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

public class AvatarBundle extends DynamicBundle {

  private static final AvatarBundle INSTANCE = new AvatarBundle();

  private AvatarBundle() {
    super("messages.avatar");
  }

  public static String get(
      @NotNull @PropertyKey(resourceBundle = "messages.avatar") String key,
      Object... params) {
    return INSTANCE.getMessage(key, params);
  }
}
