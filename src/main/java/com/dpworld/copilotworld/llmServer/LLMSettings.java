package com.dpworld.copilotworld.llmServer;

import com.intellij.openapi.components.SimplePersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;

@State(name = "Avatar_OllamaSettings_210", storages = {@Storage("Avatar_OllamaSettings_210.xml")})
public class LLMSettings extends SimplePersistentStateComponent<LLMStateSettings> {
    public LLMSettings() {
        super(new LLMStateSettings());
    }
}


