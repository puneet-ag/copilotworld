package com.dpworld.copilotworld.avatar;

import com.intellij.openapi.components.BaseState;

public class AvatarServiceCodeCompletionSettingsState extends BaseState {
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
