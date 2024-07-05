package com.dpworld.copilotworld.llmServer;

import com.dpworld.copilotworld.configurations.GeneralSettings;
import com.dpworld.copilotworld.panel.OllamaSettingsForm;
import com.dpworld.copilotworld.panel.ServiceType;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.options.Configurable;


import javax.swing.JComponent;

public class LLMConfigurableSettings implements Configurable {

    private OllamaSettingsForm component;

    @Override
    public String getDisplayName() {
        return "Avatar: Ollama Service";
    }

    @Override
    public JComponent createComponent() {
        component = new OllamaSettingsForm();
        return component.getForm();
    }

    @Override
    public boolean isModified() {
        return component.isModified();
    }

    @Override
    public void apply() {
        component.applyChanges();
        ServiceManager.getService(GeneralSettings.class).getState().setSelectedService(ServiceType.OLLAMA);
    }

    @Override
    public void reset() {
        component.resetForm();
    }
}

