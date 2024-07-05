package com.dpworld.copilotworld.actions;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.dpworld.copilotworld.llmServer.LLMSettings;
import com.dpworld.copilotworld.completion.CodeCompletionService;
import com.dpworld.copilotworld.avatar.AvatarServiceSettings;
import com.dpworld.copilotworld.configurations.GeneralSettings;
import com.dpworld.copilotworld.panel.ServiceType;


public abstract class CodeCompletionFeatureToggleActions extends AnAction {

    private final boolean enableFeatureAction;

    public CodeCompletionFeatureToggleActions(boolean enableFeatureAction) {
        this.enableFeatureAction = enableFeatureAction;
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        ServiceType selectedService = GeneralSettings.getSelectedService();

        switch (selectedService) {
            case AVATAR:
                ServiceManager.getService(AvatarServiceSettings.class).getState().getCodeCompletionSettings().setCodeCompletionsEnabled(enableFeatureAction);
                break;

            case OLLAMA:
                ServiceManager.getService(LLMSettings.class).getState().setCodeCompletionsEnabled(enableFeatureAction);
                break;
            default:
                
                break;
        }
    }

    @Override
    public void update(AnActionEvent e) {
        ServiceType selectedService = GeneralSettings.getSelectedService();
        Project project = e.getProject();

        boolean codeCompletionEnabled = false;
        if (project != null) {
            CodeCompletionService codeCompletionService = ServiceManager.getService(project, CodeCompletionService.class);
            codeCompletionEnabled = codeCompletionService.isCodeCompletionsEnabled(selectedService);
        }

        e.getPresentation().setVisible(codeCompletionEnabled != enableFeatureAction);
        e.getPresentation().setEnabled(
                selectedService == ServiceType.AVATAR ||
                        selectedService == ServiceType.OLLAMA
        );
    }

    @Override
    public ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }
}

class EnableCompletionsAction extends CodeCompletionFeatureToggleActions {
    public EnableCompletionsAction() {
        super(true);
    }
}

class DisableCompletionsAction extends CodeCompletionFeatureToggleActions {
    public DisableCompletionsAction() {
        super(false);
    }
}

