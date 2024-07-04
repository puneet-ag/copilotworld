package panel.converted;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.actionSystem.ex.ComboBoxAction;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;

import model.OllamaSettings;
import org.jetbrains.annotations.NotNull;
import panel.converted.*;

import javax.swing.*;

import static panel.converted.ServiceType.CODEGPT;
import static panel.converted.ServiceType.OLLAMA;


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
    ComboBoxButton button = createComboBoxButton(presentation);
    button.setBorder(null);
    return button;
  }

  private AnAction[] getCodeGPTModelActions(Project project, Presentation presentation) {
    var userDetails = CodeGPTKeys.CODEGPT_USER_DETAILS.get(project);
    return CodeGPTAvailableModels.getToolWindowModels()
        .toArray(AnAction[]::new);
  }

  @Override
  protected @NotNull DefaultActionGroup createPopupActionGroup(JComponent button) {
    var presentation = ((ComboBoxButton) button).getPresentation();
    var actionGroup = new DefaultActionGroup();
    actionGroup.addSeparator("CodeGPT");
    actionGroup.addAll(getCodeGPTModelActions(project, presentation));
    actionGroup.addSeparator("OpenAI");
    actionGroup.addSeparator("Custom OpenAI");

    actionGroup.addSeparator();

    actionGroup.addSeparator();

    actionGroup.addSeparator();

    actionGroup.addSeparator("Ollama");
    ApplicationManager.getApplication()
        .getService(OllamaSettings.class)
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
        templatePresentation.setText(application.getService(OllamaSettings.class)
            .getState()
            .getModel());
        break;

      default:
        break;
    }
  }



//  private String getSelectedHuggingFace() {
//    var huggingFaceModel = LlamaSettings.getCurrentState().getHuggingFaceModel();
//    var llamaModel = LlamaModel.findByHuggingFaceModel(huggingFaceModel);
//    return format(
//        "%s %dB (Q%d)",
//        llamaModel.getLabel(),
//        huggingFaceModel.getParameterSize(),
//        huggingFaceModel.getQuantization());
//  }

  private AnAction createModelAction(
      ServiceType serviceType,
      String label,
      Icon icon,
      Presentation comboBoxPresentation) {
    return new DumbAwareAction(label, "", icon) {
      @Override
      public void update(@NotNull AnActionEvent event) {
        var presentation = event.getPresentation();
        presentation.setEnabled(!presentation.getText().equals(comboBoxPresentation.getText()));
      }

      @Override
      public void actionPerformed(@NotNull AnActionEvent e) {
        handleModelChange(serviceType, label, icon, comboBoxPresentation);
      }

      @Override
      public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
      }
    };
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

  private AnAction createCodeGPTModelAction(CodeGPTModel model, Presentation comboBoxPresentation) {
    return new DumbAwareAction(model.getName(), "", model.getIcon()) {
      @Override
      public void update(@NotNull AnActionEvent event) {
        var presentation = event.getPresentation();
        presentation.setEnabled(!presentation.getText().equals(comboBoxPresentation.getText()));
      }

      @Override
      public void actionPerformed(@NotNull AnActionEvent e) {
        ApplicationManager.getApplication().getService(CodeGPTServiceSettings.class)
            .getState()
            .getChatCompletionSettings()
            .setModel(model.getCode());
        handleModelChange(
            CODEGPT,
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
            .getService(OllamaSettings.class)
            .getState()
            .setModel(model);
        handleModelChange(
            OLLAMA,
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

//  private AnAction createOpenAIModelAction(
//      OpenAIChatCompletionModel model,
//      Presentation comboBoxPresentation) {
//    createModelAction(OPENAI, model.getDescription(), Icons.OpenAI,
//        comboBoxPresentation);
//    return new DumbAwareAction(model.getDescription(), "", Icons.OpenAI) {
//      @Override
//      public void update(@NotNull AnActionEvent event) {
//        var presentation = event.getPresentation();
//        presentation.setEnabled(!presentation.getText().equals(comboBoxPresentation.getText()));
//      }
//
//      @Override
//      public void actionPerformed(@NotNull AnActionEvent e) {
//        OpenAISettings.getCurrentState().setModel(model.getCode());
//        handleModelChange(
//            OPENAI,
//            model.getDescription(),
//            Icons.OpenAI,
//            comboBoxPresentation);
//      }
//
//      @Override
//      public @NotNull ActionUpdateThread getActionUpdateThread() {
//        return ActionUpdateThread.BGT;
//      }
//    };
//  }
}
