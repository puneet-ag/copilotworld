package com.dpworld.copilotworld.panel.converted;

import com.dpworld.copilotworld.panel.OllamaSettingsForm;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.options.Configurable;


import javax.swing.JComponent;

public class OllamaSettingsConfigurable implements Configurable {

    private OllamaSettingsForm component;

    @Override
    public String getDisplayName() {
        return "Vision: Ollama Service";
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

