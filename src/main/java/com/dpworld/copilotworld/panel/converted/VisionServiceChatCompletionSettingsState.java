package com.dpworld.copilotworld.panel.converted;

import com.intellij.openapi.components.BaseState;

public class VisionServiceChatCompletionSettingsState extends BaseState {
    private String model = "llama-3-8b";

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}
