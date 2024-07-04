package com.dpworld.copilotworld.panel

import com.dpworld.copilotworld.panel.converted.*
import com.intellij.openapi.components.service
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBPasswordField
import com.intellij.util.ui.FormBuilder
import org.jdesktop.swingx.combobox.ListComboBoxModel
import java.awt.Component
import javax.swing.DefaultListCellRenderer
import javax.swing.JList
import javax.swing.JPanel

class VisionServiceForm {

    private val apiKeyField = JBPasswordField().apply {
        columns = 30
    }

    private val chatCompletionModelComboBox =
        ComboBox(ListComboBoxModel(VisionAvailableModels.ALL_CHAT_MODELS)).apply {
            selectedItem =
                VisionAvailableModels.findByCode(service<VisionServiceSettings>().state.chatCompletionSettings.model)
            renderer = CustomComboBoxRenderer()
        }

    private val codeCompletionsEnabledCheckBox = JBCheckBox(
        VisionBundle.get("codeCompletionsForm.enableFeatureText"),
        service<VisionServiceSettings>().state.codeCompletionSettings.isCodeCompletionsEnabled
    )

    private val codeCompletionModelComboBox =
        ComboBox(ListComboBoxModel(VisionAvailableModels.CODE_MODELS)).apply {
            selectedItem =
                VisionAvailableModels.findByCode(service<VisionServiceSettings>().state.codeCompletionSettings.model)
            renderer = CustomComboBoxRenderer()
        }

    init {

    }

    fun getForm(): JPanel = FormBuilder.createFormBuilder()
        .addLabeledComponent(
            VisionBundle.get("settingsConfigurable.shared.apiKey.label"),
            apiKeyField
        )
        .addComponentToRightColumn(
            UIUtil.createComment("settingsConfigurable.service.vision.apiKey.comment")
        )
        .addLabeledComponent("Chat model:", chatCompletionModelComboBox)
        .addComponentToRightColumn(
            UIUtil.createComment("settingsConfigurable.service.vision.chatCompletionModel.comment")
        )
        .addLabeledComponent("Code model:", codeCompletionModelComboBox)
        .addComponentToRightColumn(
            UIUtil.createComment("settingsConfigurable.service.vision.codeCompletionModel.comment")
        )
        .addVerticalGap(4)
        .addComponent(codeCompletionsEnabledCheckBox)
        .addComponentFillVertically(JPanel(), 0)
        .panel

    fun getApiKey() = String(apiKeyField.password).ifEmpty { null }

    fun isModified() = service<VisionServiceSettings>().state.run {
        (chatCompletionModelComboBox.selectedItem as VisionModel).code != chatCompletionSettings.model
                || (codeCompletionModelComboBox.selectedItem as VisionModel).code != codeCompletionSettings.model
                || codeCompletionsEnabledCheckBox.isSelected != codeCompletionSettings.isCodeCompletionsEnabled

    }

    fun applyChanges() {
        service<VisionServiceSettings>().state.run {
            chatCompletionSettings.model =
                (chatCompletionModelComboBox.selectedItem as VisionModel).code
            codeCompletionSettings.isCodeCompletionsEnabled =
                codeCompletionsEnabledCheckBox.isSelected
            codeCompletionSettings.model =
                (codeCompletionModelComboBox.selectedItem as VisionModel).code
        }
    }

    fun resetForm() {
        service<VisionServiceSettings>().state.run {
            chatCompletionModelComboBox.selectedItem = chatCompletionSettings.model
            codeCompletionModelComboBox.selectedItem = codeCompletionSettings.model
            codeCompletionsEnabledCheckBox.isSelected =
                codeCompletionSettings.isCodeCompletionsEnabled
        }
    }

    private class CustomComboBoxRenderer : DefaultListCellRenderer() {
        override fun getListCellRendererComponent(
            list: JList<*>,
            value: Any?,
            index: Int,
            isSelected: Boolean,
            cellHasFocus: Boolean
        ): Component {
            val component =
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus)
            if (value is VisionModel) {
                text = value.name
            }
            return component
        }
    }
}