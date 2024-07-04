package com.dpworld.copilotworld.panel.converted;


public class GeneralSettingsState {

  private String displayName = "";
  private ServiceType selectedService = ServiceType.VISION;

  public String getDisplayName() {
    if (displayName == null || displayName.isEmpty()) {
      var systemUserName = System.getProperty("user.name");
      if (systemUserName == null || systemUserName.isEmpty()) {
        return "User";
      }
      return systemUserName;
    }
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public ServiceType getSelectedService() {
    return selectedService;
  }

  public void setSelectedService(ServiceType selectedService) {
    this.selectedService = selectedService;
  }
}
