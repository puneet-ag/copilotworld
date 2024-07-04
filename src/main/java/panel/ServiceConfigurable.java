package panel;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.options.Configurable;
import panel.converted.ApplicationUtil;
import panel.converted.ChatToolWindowContentManager;
import panel.converted.ConversationsState;
import panel.converted.GeneralSettings;

import javax.swing.JComponent;
import javax.swing.JPanel;

public class ServiceConfigurable implements Configurable {

    private ServiceConfigurableComponent component;

    @Override
    public String getDisplayName() {
        return "CodeGPT: Services";
    }

    @Override
    public JComponent createComponent() {
        component = new ServiceConfigurableComponent();
        return component.getPanel();
    }

    @Override
    public boolean isModified() {
        return !component.getSelectedService().equals(ServiceManager.getService(GeneralSettings.class).getState().getSelectedService());
    }

    @Override
    public void apply() {
        GeneralSettings state = ServiceManager.getService(GeneralSettings.class).getState();
        String previousService = state.getSelectedService();
        String newService = component.getSelectedService();
        state.setSelectedService(newService);

        boolean serviceChanged = !newService.equals(previousService);
        if (serviceChanged) {
            resetActiveTab();
        }
    }

    @Override
    public void reset() {
        GeneralSettings state = ServiceManager.getService(GeneralSettings.class).getState();
        component.setSelectedService(state.getSelectedService());
    }

    private void resetActiveTab() {
        ServiceManager.getService(ConversationsState.class).setCurrentConversation(null);
        var project = ApplicationUtil.findCurrentProject();
        if (project == null) {
            throw new RuntimeException("Could not find current project.");
        }
        project.getService(ChatToolWindowContentManager.class).resetAll();
    }
}

