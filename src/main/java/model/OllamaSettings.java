package model;

import com.intellij.openapi.components.BaseState;
import com.intellij.openapi.components.SimplePersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;

import java.util.ArrayList;
import java.util.List;

@State(name = "CodeGPT_OllamaSettings_210", storages = {@Storage("CodeGPT_OllamaSettings_210.xml")})
public class OllamaSettings extends SimplePersistentStateComponent<OllamaSettingsState> {
    public OllamaSettings() {
        super(new OllamaSettingsState());
    }
}


