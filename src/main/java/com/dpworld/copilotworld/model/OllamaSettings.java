package com.dpworld.copilotworld.model;

import com.intellij.openapi.components.SimplePersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;

@State(name = "Avatar_OllamaSettings_210", storages = {@Storage("Avatar_OllamaSettings_210.xml")})
public class OllamaSettings extends SimplePersistentStateComponent<OllamaSettingsState> {
    public OllamaSettings() {
        super(new OllamaSettingsState());
    }
}


