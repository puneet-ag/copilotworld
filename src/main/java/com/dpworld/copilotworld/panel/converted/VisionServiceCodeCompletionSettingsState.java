package com.dpworld.copilotworld.panel.converted;

import com.intellij.openapi.components.BaseState;

public class VisionServiceCodeCompletionSettingsState extends BaseState {
    private boolean codeCompletionsEnabled = false;
    private String model = "starcoder-7b";

    public boolean isCodeCompletionsEnabled() {
        return codeCompletionsEnabled;
    }

    public void setCodeCompletionsEnabled(boolean codeCompletionsEnabled) {
        this.codeCompletionsEnabled = codeCompletionsEnabled;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}
