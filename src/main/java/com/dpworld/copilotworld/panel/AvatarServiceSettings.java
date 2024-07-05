package com.dpworld.copilotworld.panel;


import com.intellij.openapi.components.*;

@Service
@State(
        name = "Avatar_AvatarServiceSettings_280",
        storages = @Storage("Avatar_AvatarServiceSettings_280.xml")
)
public final class AvatarServiceSettings extends SimplePersistentStateComponent<AvatarServiceSettingsState> {

    public AvatarServiceSettings() {
        super(new AvatarServiceSettingsState());
    }
}






