package com.dpworld.copilotworld.panel.converted;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.openapi.wm.impl.status.widget.StatusBarEditorBasedWidgetFactory;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class VisionStatusBarWidgetFactory extends StatusBarEditorBasedWidgetFactory {

  @Override
  public @NonNls @NotNull String getId() {
    return "com.dpworld.copilotworld.panel.converted.statusbar.widget";
  }

  @Override
  public @Nls @NotNull String getDisplayName() {
    return VisionBundle.get("project.label");
  }

  @Override
  public @NotNull StatusBarWidget createWidget(@NotNull Project project) {
    return new VisionStatusBarWidget(project);
  }

}
