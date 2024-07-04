package com.dpworld.copilotworld.panel.converted;

import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;

import javax.swing.*;

public class GeneralSettingsComponent {

  private final JBTextField displayNameField;

  public GeneralSettingsComponent(GeneralSettings settings) {
    displayNameField = new JBTextField(settings.getState().getDisplayName(), 20);
  }

  public JPanel getPanel() {
    return FormBuilder.createFormBuilder()
        .addLabeledComponent(
            CodeGPTBundle.get("settingsConfigurable.displayName.label"),
            displayNameField)
        .addComponentFillVertically(new JPanel(), 0)
        .getPanel();
  }

  public JComponent getPreferredFocusedComponent() {
    return displayNameField;
  }

  public String getDisplayName() {
    return displayNameField.getText();
  }

  public void setDisplayName(String displayName) {
    displayNameField.setText(displayName);
  }
}
