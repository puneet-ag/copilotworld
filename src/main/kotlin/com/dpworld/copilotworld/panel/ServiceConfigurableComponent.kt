package com.dpworld.copilotworld.panel

import com.dpworld.copilotworld.avatar.AvatarBundle
import com.dpworld.copilotworld.configurations.GeneralSettings
import com.dpworld.copilotworld.forms.AvatarServiceForm

import com.dpworld.copilotworld.llmServer.LLMConfigurableSettings
import com.intellij.ide.DataManager
import com.intellij.openapi.components.service
import com.intellij.openapi.options.ex.Settings
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.EnumComboBoxModel
import com.intellij.ui.components.ActionLink
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.FormBuilder
import javax.swing.JPanel

class ServiceConfigurableComponent {

    var form: AvatarServiceForm = AvatarServiceForm()

    private var serviceComboBox: ComboBox<ServiceType> =
        ComboBox(EnumComboBoxModel(ServiceType::class.java)).apply {
            selectedItem = service<GeneralSettings>().state.selectedService
        }

    fun getSelectedService(): ServiceType {
        return serviceComboBox.selectedItem as ServiceType
    }

    fun setSelectedService(serviceType: ServiceType) {
        serviceComboBox.selectedItem = serviceType
    }

    fun getPanel(): JPanel = FormBuilder.createFormBuilder()
        .addLabeledComponent(
            AvatarBundle.get("settingsConfigurable.service.label"),
            serviceComboBox
        )
        .addVerticalGap(8)
        .addComponent(JBLabel("All available providers that can be used with Avatar:"))
        .addVerticalGap(8)
        .addComponent(FormBuilder.createFormBuilder()
            .setFormLeftIndent(20).apply {
                addLinks(this)
            }
            .panel)
        .addComponentFillVertically(JPanel(), 0)
        .panel

    private fun addLinks(formBuilder: FormBuilder) {
        mapOf(
            "Local" to LLMConfigurableSettings::class.java,
        ).entries.forEach { (name, configurableClass) ->
            formBuilder.addComponent(ActionLink(name) {
                val context = service<DataManager>().getDataContext(it.source as ActionLink)
                val settings = Settings.KEY.getData(context)
                settings?.select(settings.find(configurableClass))
            })
        }
    }
}