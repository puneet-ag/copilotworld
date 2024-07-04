package com.dpworld.copilotworld.panel.converted;

import com.intellij.openapi.components.BaseState;

public class CodeGPTServiceSettingsState extends BaseState {
    private CodeGPTServiceChatCompletionSettingsState chatCompletionSettings = new CodeGPTServiceChatCompletionSettingsState();
    private CodeGPTServiceCodeCompletionSettingsState codeCompletionSettings = new CodeGPTServiceCodeCompletionSettingsState();

    public CodeGPTServiceChatCompletionSettingsState getChatCompletionSettings() {
        return chatCompletionSettings;
    }

    public void setChatCompletionSettings(CodeGPTServiceChatCompletionSettingsState chatCompletionSettings) {
        this.chatCompletionSettings = chatCompletionSettings;
    }

    public CodeGPTServiceCodeCompletionSettingsState getCodeCompletionSettings() {
        return codeCompletionSettings;
    }

    public void setCodeCompletionSettings(CodeGPTServiceCodeCompletionSettingsState codeCompletionSettings) {
        this.codeCompletionSettings = codeCompletionSettings;
    }
}

