package com.dpworld.copilotworld.panel.converted;


import com.intellij.openapi.components.*;

@Service
@State(
        name = "Vision_VisionServiceSettings_280",
        storages = @Storage("Vision_VisionServiceSettings_280.xml")
)
public final class VisionServiceSettings extends SimplePersistentStateComponent<VisionServiceSettingsState> {

    public VisionServiceSettings() {
        super(new VisionServiceSettingsState());
    }
}






