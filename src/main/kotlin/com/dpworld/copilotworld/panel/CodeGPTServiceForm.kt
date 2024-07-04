package com.dpworld.copilotworld.panel

import com.dpworld.copilotworld.panel.converted.*
import com.intellij.openapi.components.service
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBPasswordField
import com.intellij.util.ui.FormBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.jdesktop.swingx.combobox.ListComboBoxModel
import java.awt.Component
import javax.swing.DefaultListCellRenderer
import javax.swing.JList
import javax.swing.JPanel

class CodeGPTServiceForm {

    private val apiKeyField = JBPasswordField().apply {
        columns = 30
    }

    private val chatCompletionModelComboBox =
        ComboBox(ListComboBoxModel(CodeGPTAvailableModels.ALL_CHAT_MODELS)).apply {
            selectedItem =
                CodeGPTAvailableModels.findByCode(service<CodeGPTServiceSettings>().state.chatCompletionSettings.model)
            renderer = CustomComboBoxRenderer()
        }

    private val codeCompletionsEnabledCheckBox = JBCheckBox(
        CodeGPTBundle.get("codeCompletionsForm.enableFeatureText"),
        service<CodeGPTServiceSettings>().state.codeCompletionSettings.isCodeCompletionsEnabled
    )

    private val codeCompletionModelComboBox =
        ComboBox(ListComboBoxModel(CodeGPTAvailableModels.CODE_MODELS)).apply {
            selectedItem =
                CodeGPTAvailableModels.findByCode(service<CodeGPTServiceSettings>().state.codeCompletionSettings.model)
            renderer = CustomComboBoxRenderer()
        }

    init {

    }

    fun getForm(): JPanel = FormBuilder.createFormBuilder()
        .addLabeledComponent(
            CodeGPTBundle.get("settingsConfigurable.shared.apiKey.label"),
            apiKeyField
        )
        .addComponentToRightColumn(
            UIUtil.createComment("settingsConfigurable.service.codegpt.apiKey.comment")
        )
        .addLabeledComponent("Chat model:", chatCompletionModelComboBox)
        .addComponentToRightColumn(
            UIUtil.createComment("settingsConfigurable.service.codegpt.chatCompletionModel.comment")
        )
        .addLabeledComponent("Code model:", codeCompletionModelComboBox)
        .addComponentToRightColumn(
            UIUtil.createComment("settingsConfigurable.service.codegpt.codeCompletionModel.comment")
        )
        .addVerticalGap(4)
        .addComponent(codeCompletionsEnabledCheckBox)
        .addComponentFillVertically(JPanel(), 0)
        .panel

    fun getApiKey() = String(apiKeyField.password).ifEmpty { null }

    fun isModified() = service<CodeGPTServiceSettings>().state.run {
        (chatCompletionModelComboBox.selectedItem as CodeGPTModel).code != chatCompletionSettings.model
                || (codeCompletionModelComboBox.selectedItem as CodeGPTModel).code != codeCompletionSettings.model
                || codeCompletionsEnabledCheckBox.isSelected != codeCompletionSettings.isCodeCompletionsEnabled

    }

    fun applyChanges() {
        service<CodeGPTServiceSettings>().state.run {
            chatCompletionSettings.model =
                (chatCompletionModelComboBox.selectedItem as CodeGPTModel).code
            codeCompletionSettings.isCodeCompletionsEnabled =
                codeCompletionsEnabledCheckBox.isSelected
            codeCompletionSettings.model =
                (codeCompletionModelComboBox.selectedItem as CodeGPTModel).code
        }
    }

    fun resetForm() {
        service<CodeGPTServiceSettings>().state.run {
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
            if (value is CodeGPTModel) {
                text = value.name
            }
            return component
        }
    }
}