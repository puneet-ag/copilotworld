package com.dpworld.copilotworld.panel.converted;


import com.intellij.openapi.components.*;

@Service
@State(
        name = "CodeGPT_CodeGPTServiceSettings_280",
        storages = @Storage("CodeGPT_CodeGPTServiceSettings_280.xml")
)
public final class CodeGPTServiceSettings extends SimplePersistentStateComponent<CodeGPTServiceSettingsState> {

    public CodeGPTServiceSettings() {
        super(new CodeGPTServiceSettingsState());
    }
}






