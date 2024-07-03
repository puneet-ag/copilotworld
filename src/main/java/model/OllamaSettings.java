package model;

import com.intellij.openapi.components.BaseState;
import com.intellij.openapi.components.SimplePersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;

import java.util.ArrayList;
import java.util.List;

@State(name = "CodeGPT_OllamaSettings_210", storages = {@Storage("CodeGPT_OllamaSettings_210.xml")})
public class OllamaSettings extends SimplePersistentStateComponent<OllamaSettingsState> {
    public OllamaSettings() {
        super(new OllamaSettingsState());
    }
}

class OllamaSettingsState extends BaseState {
    private String host = "http://localhost:11434";
    private String model;
    private boolean codeCompletionsEnabled = false;
    private InfillPromptTemplate fimTemplate = InfillPromptTemplate.CODE_LLAMA;
    private List<String> availableModels = new ArrayList<>();

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
        incrementModificationCount();
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
        incrementModificationCount();
    }

    public boolean isCodeCompletionsEnabled() {
        return codeCompletionsEnabled;
    }

    public void setCodeCompletionsEnabled(boolean codeCompletionsEnabled) {
        this.codeCompletionsEnabled = codeCompletionsEnabled;
        incrementModificationCount();
    }

    public InfillPromptTemplate getFimTemplate() {
        return fimTemplate;
    }

    public void setFimTemplate(InfillPromptTemplate fimTemplate) {
        this.fimTemplate = fimTemplate;
        incrementModificationCount();
    }

    public List<String> getAvailableModels() {
        return availableModels;
    }

    public void setAvailableModels(List<String> availableModels) {
        this.availableModels = availableModels;
        incrementModificationCount();
    }
}

