package com.dpworld.copilotworld.panel;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.util.Disposer;

import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ConfigurationConfigurable implements Configurable {

  private Disposable parentDisposable;

  private ConfigurationComponent component;

  @Nls(capitalization = Nls.Capitalization.Title)
  @Override
  public String getDisplayName() {
    return AvatarBundle.get("configurationConfigurable.displayName");
  }

  @Nullable
  @Override
  public JComponent createComponent() {
    parentDisposable = Disposer.newDisposable();
    component = new ConfigurationComponent(
        parentDisposable,
        ConfigurationSettings.getCurrentState());
    return component.getPanel();
  }

  @Override
  public boolean isModified() {
    return !component.getCurrentFormState()
        .equals(ConfigurationSettings.getCurrentState());
  }

  @Override
  public void apply() {
    ConfigurationSettings.getInstance().loadState(component.getCurrentFormState());
    EditorActionsUtil.refreshActions();
  }

  @Override
  public void reset() {
    component.resetForm();
  }

  @Override
  public void disposeUIResources() {
    if (parentDisposable != null) {
      Disposer.dispose(parentDisposable);
    }
    component = null;
  }
}
