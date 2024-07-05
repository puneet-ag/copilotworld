package com.dpworld.copilotworld.actions;

import com.dpworld.copilotworld.avatar.AvatarAvailableModels;
import com.dpworld.copilotworld.avatar.AvatarKeys;
import com.dpworld.copilotworld.avatar.AvatarModel;
import com.dpworld.copilotworld.avatar.AvatarServiceSettings;
import com.dpworld.copilotworld.configurations.GeneralSettings;
import com.dpworld.copilotworld.panel.Icons;
import com.dpworld.copilotworld.forms.OllamaSettingsForm;
import com.dpworld.copilotworld.panel.ServiceType;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.actionSystem.ex.ComboBoxAction;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;

import com.dpworld.copilotworld.llmServer.LLMSettings;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class ModelComboBoxAction extends ComboBoxAction {

  private final Runnable onModelChange;
  private final Project project;

  public ModelComboBoxAction(Project project, Runnable onModelChange, ServiceType selectedService) {
    this.project = project;
    this.onModelChange = onModelChange;
    updateTemplatePresentation(selectedService);
  }

  public JComponent createCustomComponent(@NotNull String place) {
    return createCustomComponent(getTemplatePresentation(), place);
  }

  @NotNull
  @Override
  public JComponent createCustomComponent(
      @NotNull Presentation presentation,
      @NotNull String place) {
    presentation.setText("Reload Models");
    ComboBoxButton button = createComboBoxButton(presentation);
    button.setBorder(null);
    
    return button;
  }

  private void refreshModels() {
    OllamaSettingsForm settingsForm = new OllamaSettingsForm();
    settingsForm.refreshModels(settingsForm.getModel());
  }

  private AnAction[] getAvatarModelActions(Project project, Presentation presentation) {
    var userDetails = AvatarKeys.AVATAR_USER_DETAILS.get(project);
    return AvatarAvailableModels.getToolWindowModels().stream()
            .map(model -> createAvatarModelAction(model, presentation))
            .toArray(AnAction[]::new);
  }

  @Override
  protected @NotNull DefaultActionGroup createPopupActionGroup(JComponent button) {
    var presentation = ((ComboBoxButton) button).getPresentation();
    var actionGroup = new DefaultActionGroup();
    actionGroup.addSeparator("Avatar");
    actionGroup.addAll(getAvatarModelActions(project, presentation));
    actionGroup.addSeparator("Available Models");
    actionGroup.addSeparator();

    actionGroup.addSeparator("Ollama");
    refreshModels();
    ApplicationManager.getApplication()
        .getService(LLMSettings.class)
        .getState()
        .getAvailableModels()
        .forEach(model ->
            actionGroup.add(createOllamaModelAction(model, presentation)));
    actionGroup.addSeparator();

    return actionGroup;
  }

  @Override
  protected boolean shouldShowDisabledActions() {
    return true;
  }

  private void updateTemplatePresentation(ServiceType selectedService) {
    var application = ApplicationManager.getApplication();
    var templatePresentation = getTemplatePresentation();
    switch (selectedService) {

      case OLLAMA:
        templatePresentation.setIcon(Icons.Ollama);
        templatePresentation.setText(application.getService(LLMSettings.class)
            .getState()
            .getModel());
        break;

      default:
        break;
    }
  }

  private void handleModelChange(
      ServiceType serviceType,
      String label,
      Icon icon,
      Presentation comboBoxPresentation) {
    GeneralSettings.getCurrentState().setSelectedService(serviceType);
    comboBoxPresentation.setIcon(icon);
    comboBoxPresentation.setText(label);
    onModelChange.run();
  }

  private AnAction createAvatarModelAction(AvatarModel model, Presentation comboBoxPresentation) {
    return new DumbAwareAction(model.getName(), "", model.getIcon()) {
      @Override
      public void update(@NotNull AnActionEvent event) {
        var presentation = event.getPresentation();
        presentation.setEnabled(!presentation.getText().equals(comboBoxPresentation.getText()));
      }

      @Override
      public void actionPerformed(@NotNull AnActionEvent e) {
        ApplicationManager.getApplication().getService(AvatarServiceSettings.class)
            .getState()
            .getChatCompletionSettings()
            .setModel(model.getCode());
        handleModelChange(
            ServiceType.AVATAR,
            model.getName(),
            model.getIcon(),
            comboBoxPresentation);
      }

      @Override
      public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
      }
    };
  }

  private AnAction createOllamaModelAction(
      String model,
      Presentation comboBoxPresentation
  ) {
    return new DumbAwareAction(model, "", Icons.Ollama) {
      @Override
      public void update(@NotNull AnActionEvent event) {
        var presentation = event.getPresentation();
        presentation.setEnabled(!presentation.getText().equals(comboBoxPresentation.getText()));
      }

      @Override
      public void actionPerformed(@NotNull AnActionEvent e) {
        ApplicationManager.getApplication()
            .getService(LLMSettings.class)
            .getState()
            .setModel(model);
        handleModelChange(
            ServiceType.OLLAMA,
            model,
            Icons.Ollama,
            comboBoxPresentation);
      }

      @Override
      public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
      }
    };
  }

}
