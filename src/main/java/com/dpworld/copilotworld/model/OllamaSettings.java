package com.dpworld.copilotworld.model;

import com.intellij.openapi.components.SimplePersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;

@State(name = "Vision_OllamaSettings_210", storages = {@Storage("Vision_OllamaSettings_210.xml")})
public class OllamaSettings extends SimplePersistentStateComponent<OllamaSettingsState> {
    public OllamaSettings() {
        super(new OllamaSettingsState());
    }
}


