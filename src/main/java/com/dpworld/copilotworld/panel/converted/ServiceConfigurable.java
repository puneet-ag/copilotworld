//package com.dpworld.copilotworld.panel;
//
//import com.dpworld.copilotworld.panel.converted.ApplicationUtil;
//import com.intellij.openapi.components.ServiceManager;
//import com.intellij.openapi.options.Configurable;
//import com.dpworld.copilotworld.panel.converted.ChatToolWindowContentManager;
//import com.dpworld.copilotworld.panel.converted.ConversationsState;
//import com.dpworld.copilotworld.panel.converted.GeneralSettings;
//
//import javax.swing.JComponent;
//
//public class ServiceConfigurable implements Configurable {
//
//    private ServiceConfigurableComponent component;
//
//    @Override
//    public String getDisplayName() {
//        return "Vision: Services";
//    }
//
//    @Override
//    public JComponent createComponent() {
//        component = new ServiceConfigurableComponent();
//        return component.getPanel();
//    }
//
//    @Override
//    public boolean isModified() {
//        return !component.getSelectedService().equals(ServiceManager.getService(GeneralSettings.class).getState().getSelectedService());
//    }
//
//    @Override
//    public void apply() {
//        GeneralSettings state = ServiceManager.getService(GeneralSettings.class).getState();
//        String previousService = state.getSelectedService();
//        String newService = component.getSelectedService();
//        state.setSelectedService(newService);
//
//        boolean serviceChanged = !newService.equals(previousService);
//        if (serviceChanged) {
//            resetActiveTab();
//        }
//    }
//
//    @Override
//    public void reset() {
//        GeneralSettings state = ServiceManager.getService(GeneralSettings.class).getState();
//        component.setSelectedService(state.getSelectedService());
//    }
//
//    private void resetActiveTab() {
//        ServiceManager.getService(ConversationsState.class).setCurrentConversation(null);
//        var project = ApplicationUtil.findCurrentProject();
//        if (project == null) {
//            throw new RuntimeException("Could not find current project.");
//        }
//        project.getService(ChatToolWindowContentManager.class).resetAll();
//    }
//}
//
