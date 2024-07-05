package com.dpworld.copilotworld.avatar;

import com.intellij.openapi.components.BaseState;

public class AvatarServiceChatCompletionSettingsState extends BaseState {
    private String model = "llama-3-8b";

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}
