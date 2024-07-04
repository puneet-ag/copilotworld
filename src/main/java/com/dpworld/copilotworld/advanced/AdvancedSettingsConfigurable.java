package com.dpworld.copilotworld.advanced;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class AdvancedSettingsConfigurable implements Configurable {

  private AdvancedSettingsComponent component;

  @Nls(capitalization = Nls.Capitalization.Title)
  @Override
  public String getDisplayName() {
    return "Vision";
  }

  @Nullable
  @Override
  public JComponent createComponent() {
    component = new AdvancedSettingsComponent(AdvancedSettings.getCurrentState());
    return component.getPanel();
  }

  @Override
  public boolean isModified() {
    return !component.getCurrentFormState().equals(AdvancedSettings.getCurrentState());
  }

  @Override
  public void apply() {
    AdvancedSettings.getInstance().loadState(component.getCurrentFormState());
  }

  @Override
  public void reset() {
    component.resetForm();
  }

  @Override
  public void disposeUIResources() {
    component = null;
  }
}
