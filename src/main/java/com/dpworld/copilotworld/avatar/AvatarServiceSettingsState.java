package com.dpworld.copilotworld.avatar;

import com.intellij.openapi.components.BaseState;

public class AvatarServiceSettingsState extends BaseState {
    private AvatarServiceChatCompletionSettingsState chatCompletionSettings = new AvatarServiceChatCompletionSettingsState();
    private AvatarServiceCodeCompletionSettingsState codeCompletionSettings = new AvatarServiceCodeCompletionSettingsState();

    public AvatarServiceChatCompletionSettingsState getChatCompletionSettings() {
        return chatCompletionSettings;
    }

    public void setChatCompletionSettings(AvatarServiceChatCompletionSettingsState chatCompletionSettings) {
        this.chatCompletionSettings = chatCompletionSettings;
    }

    public AvatarServiceCodeCompletionSettingsState getCodeCompletionSettings() {
        return codeCompletionSettings;
    }

    public void setCodeCompletionSettings(AvatarServiceCodeCompletionSettingsState codeCompletionSettings) {
        this.codeCompletionSettings = codeCompletionSettings;
    }
}

