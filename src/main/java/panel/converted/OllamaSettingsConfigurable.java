package panel.converted;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.options.Configurable;
import panel.OllamaSettingsForm;
import panel.converted.GeneralSettings;
import panel.converted.ServiceType;


import javax.swing.JComponent;

public class OllamaSettingsConfigurable implements Configurable {

    private OllamaSettingsForm component;

    @Override
    public String getDisplayName() {
        return "CodeGPT: Ollama Service";
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

