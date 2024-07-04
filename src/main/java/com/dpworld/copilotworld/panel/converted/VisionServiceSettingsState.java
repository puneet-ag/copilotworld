package com.dpworld.copilotworld.panel.converted;

import com.intellij.openapi.components.BaseState;

public class VisionServiceSettingsState extends BaseState {
    private VisionServiceChatCompletionSettingsState chatCompletionSettings = new VisionServiceChatCompletionSettingsState();
    private VisionServiceCodeCompletionSettingsState codeCompletionSettings = new VisionServiceCodeCompletionSettingsState();

    public VisionServiceChatCompletionSettingsState getChatCompletionSettings() {
        return chatCompletionSettings;
    }

    public void setChatCompletionSettings(VisionServiceChatCompletionSettingsState chatCompletionSettings) {
        this.chatCompletionSettings = chatCompletionSettings;
    }

    public VisionServiceCodeCompletionSettingsState getCodeCompletionSettings() {
        return codeCompletionSettings;
    }

    public void setCodeCompletionSettings(VisionServiceCodeCompletionSettingsState codeCompletionSettings) {
        this.codeCompletionSettings = codeCompletionSettings;
    }
}

