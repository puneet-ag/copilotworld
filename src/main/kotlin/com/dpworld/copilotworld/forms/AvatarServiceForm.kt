package com.dpworld.copilotworld.forms

import com.dpworld.copilotworld.avatar.AvatarAvailableModels
import com.dpworld.copilotworld.avatar.AvatarBundle
import com.dpworld.copilotworld.avatar.AvatarModel
import com.dpworld.copilotworld.avatar.AvatarServiceSettings
import com.dpworld.copilotworld.util.UIUtil
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

class AvatarServiceForm {

    private val apiKeyField = JBPasswordField().apply {
        columns = 30
    }

    private val chatCompletionModelComboBox =
        ComboBox(ListComboBoxModel(AvatarAvailableModels.ALL_CHAT_MODELS)).apply {
            selectedItem =
                AvatarAvailableModels.findByCode(service<AvatarServiceSettings>().state.chatCompletionSettings.model)
            renderer = CustomComboBoxRenderer()
        }

    private val codeCompletionsEnabledCheckBox = JBCheckBox(
        AvatarBundle.get("codeCompletionsForm.enableFeatureText"),
        service<AvatarServiceSettings>().state.codeCompletionSettings.isCodeCompletionsEnabled
    )

    private val codeCompletionModelComboBox =
        ComboBox(ListComboBoxModel(AvatarAvailableModels.CODE_MODELS)).apply {
            selectedItem =
                AvatarAvailableModels.findByCode(service<AvatarServiceSettings>().state.codeCompletionSettings.model)
            renderer = CustomComboBoxRenderer()
        }

    init {

    }

    fun getForm(): JPanel = FormBuilder.createFormBuilder()
        .addLabeledComponent(
            AvatarBundle.get("settingsConfigurable.shared.apiKey.label"),
            apiKeyField
        )
        .addComponentToRightColumn(
            UIUtil.createComment("settingsConfigurable.service.avatar.apiKey.comment")
        )
        .addLabeledComponent("Chat model:", chatCompletionModelComboBox)
        .addComponentToRightColumn(
            UIUtil.createComment("settingsConfigurable.service.avatar.chatCompletionModel.comment")
        )
        .addLabeledComponent("Code model:", codeCompletionModelComboBox)
        .addComponentToRightColumn(
            UIUtil.createComment("settingsConfigurable.service.avatar.codeCompletionModel.comment")
        )
        .addVerticalGap(4)
        .addComponent(codeCompletionsEnabledCheckBox)
        .addComponentFillVertically(JPanel(), 0)
        .panel

    fun getApiKey() = String(apiKeyField.password).ifEmpty { null }

    fun isModified() = service<AvatarServiceSettings>().state.run {
        (chatCompletionModelComboBox.selectedItem as AvatarModel).code != chatCompletionSettings.model
                || (codeCompletionModelComboBox.selectedItem as AvatarModel).code != codeCompletionSettings.model
                || codeCompletionsEnabledCheckBox.isSelected != codeCompletionSettings.isCodeCompletionsEnabled

    }

    fun applyChanges() {
        service<AvatarServiceSettings>().state.run {
            chatCompletionSettings.model =
                (chatCompletionModelComboBox.selectedItem as AvatarModel).code
            codeCompletionSettings.isCodeCompletionsEnabled =
                codeCompletionsEnabledCheckBox.isSelected
            codeCompletionSettings.model =
                (codeCompletionModelComboBox.selectedItem as AvatarModel).code
        }
    }

    fun resetForm() {
        service<AvatarServiceSettings>().state.run {
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
            if (value is AvatarModel) {
                text = value.name
            }
            return component
        }
    }
}