package com.dpworld.copilotworld.configurations;

import com.dpworld.copilotworld.activity.Conversation;
import com.dpworld.copilotworld.panel.ProviderChangeNotifier;
import com.dpworld.copilotworld.panel.ServiceType;
import com.dpworld.copilotworld.util.ApplicationUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.dpworld.copilotworld.ollama.OllamaSettings;
import org.jetbrains.annotations.NotNull;

@State(name = "Avatar_GeneralSettings_270", storages = @Storage("Avatar_GeneralSettings_270.xml"))
public class GeneralSettings implements PersistentStateComponent<GeneralSettingsState> {

  private GeneralSettingsState state = new GeneralSettingsState();

  @Override
  @NotNull
  public GeneralSettingsState getState() {
    return state;
  }

  @Override
  public void loadState(@NotNull GeneralSettingsState state) {
    this.state = state;
  }

  public static GeneralSettingsState getCurrentState() {
    return getInstance().getState();
  }

  public static GeneralSettings getInstance() {
    return ApplicationManager.getApplication().getService(GeneralSettings.class);
  }

  public static ServiceType getSelectedService() {
    return getCurrentState().getSelectedService();
  }

  public static boolean isSelected(ServiceType serviceType) {
    return getSelectedService() == serviceType;
  }

  public void sync(Conversation conversation) {
    var project = ApplicationUtil.findCurrentProject();
    var provider = ServiceType.fromClientCode(conversation.getClientCode());
    switch (provider) {
      case OLLAMA:
        ApplicationManager.getApplication().getService(OllamaSettings.class).getState()
                .setModel(conversation.getModel());
        break;
      default:
        break;
    }
    state.setSelectedService(provider);
    if (project != null) {
      project.getMessageBus()
          .syncPublisher(ProviderChangeNotifier.PROVIDER_CHANGE_TOPIC)
          .providerChanged(provider);
    }
  }

  public String getModel() {
        return ApplicationManager.getApplication()
            .getService(OllamaSettings.class)
            .getState()
            .getModel();
  }
}
