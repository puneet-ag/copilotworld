package com.dpworld.copilotworld.llmServer;

import com.dpworld.copilotworld.model.InfillPromptTemplate;
import com.intellij.openapi.components.BaseState;

import java.util.ArrayList;
import java.util.List;

public class LLMStateSettings extends BaseState {
    private String host = "http://localhost:11434";
    private String model;
    private boolean codeCompletionsEnabled = false;
    private InfillPromptTemplate fimTemplate = InfillPromptTemplate.DEEPSEEK_CODER;
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
