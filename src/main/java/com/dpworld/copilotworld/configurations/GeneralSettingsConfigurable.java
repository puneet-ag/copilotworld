package com.dpworld.copilotworld.configurations;

import com.dpworld.copilotworld.avatar.AvatarBundle;
import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class GeneralSettingsConfigurable implements Configurable {

  private GeneralSettingsComponent component;

  @Nls(capitalization = Nls.Capitalization.Title)
  @Override
  public String getDisplayName() {
    return AvatarBundle.get("settings.displayName");
  }

  @Override
  public JComponent getPreferredFocusedComponent() {
    return component.getPreferredFocusedComponent();
  }

  @Nullable
  @Override
  public JComponent createComponent() {
    component = new GeneralSettingsComponent(GeneralSettings.getInstance());
    return component.getPanel();
  }

  @Override
  public boolean isModified() {
    var settings = GeneralSettings.getCurrentState();
    return !component.getDisplayName().equals(settings.getDisplayName());
  }

  @Override
  public void apply() {
    GeneralSettings.getCurrentState().setDisplayName(component.getDisplayName());
  }

  @Override
  public void reset() {
    component.setDisplayName(GeneralSettings.getCurrentState().getDisplayName());
  }
}
