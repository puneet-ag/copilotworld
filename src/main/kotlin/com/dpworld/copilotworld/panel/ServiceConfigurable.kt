package com.dpworld.copilotworld.panel

import com.dpworld.copilotworld.configurations.GeneralSettings
import com.dpworld.copilotworld.conversation.ConversationsState
import com.dpworld.copilotworld.conversation.chat.ChatToolWindowContentManager
import com.dpworld.copilotworld.util.ApplicationManagerUtil.getCurrentProject
import com.intellij.openapi.components.service
import com.intellij.openapi.options.Configurable
import javax.swing.JComponent

class ServiceConfigurable : Configurable {

    private lateinit var component: ServiceConfigurableComponent

    override fun getDisplayName(): String {
        return "Avatar: Services"
    }

    override fun createComponent(): JComponent {
        component = ServiceConfigurableComponent()
        return component.getPanel()
    }

    override fun isModified(): Boolean {
        return component.getSelectedService() != service<GeneralSettings>().state.selectedService
    }

    override fun apply() {
        val state = service<GeneralSettings>().state
        state.selectedService = component.getSelectedService()

        val serviceChanged = component.getSelectedService() != state.selectedService
        if (serviceChanged) {
            resetActiveTab()
        }
    }

    override fun reset() {
        component.setSelectedService(service<GeneralSettings>().state.selectedService)
    }

    private fun resetActiveTab() {
        service<ConversationsState>().currentConversation = null
        val project = getCurrentProject()
            ?: throw RuntimeException("Could not find current project.")
        project.getService(ChatToolWindowContentManager::class.java).resetAll()
    }
}