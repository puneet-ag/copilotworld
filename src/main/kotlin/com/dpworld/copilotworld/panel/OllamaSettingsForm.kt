package com.dpworld.copilotworld.panel

import com.dpworld.copilotworld.avatar.AvatarBundle
import com.dpworld.copilotworld.ollama.OllamaSettings
import com.dpworld.copilotworld.ollama.OllamaClient
import com.dpworld.copilotworld.util.OverlayUtil
import com.dpworld.copilotworld.util.UIUtil
import com.intellij.notification.NotificationType
import com.intellij.openapi.application.invokeLater
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.observable.util.whenTextChangedFromUi
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.MessageType
import com.intellij.ui.TitledSeparator
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.awt.BorderLayout
import java.lang.String.format
import java.net.ConnectException
import javax.swing.ComboBoxModel
import javax.swing.DefaultComboBoxModel
import javax.swing.JButton
import javax.swing.JPanel

class OllamaSettingsForm {

    private val refreshModelsButton =
        JButton(AvatarBundle.get("settingsConfigurable.service.ollama.models.refresh"))
    private val hostField: JBTextField
    private val modelComboBox: ComboBox<String>
    private val codeCompletionConfigurationForm: CodeCompletionConfigurationForm

    companion object {
        private val logger = thisLogger()
    }

    init {
        val settings = service<OllamaSettings>().state
        codeCompletionConfigurationForm = CodeCompletionConfigurationForm(
            settings.isCodeCompletionsEnabled,
            settings.fimTemplate
        )
        val emptyModelsComboBoxModel =
            DefaultComboBoxModel(arrayOf("Hit refresh to see models for this host"))
        modelComboBox = ComboBox(emptyModelsComboBoxModel).apply {
            isEnabled = false
        }
        hostField = URLTextField().apply {
            text = settings.host
            whenTextChangedFromUi {
                modelComboBox.model = emptyModelsComboBoxModel
                modelComboBox.isEnabled = false
            }
        }


        refreshModels(settings.model)
    }

    fun getForm(): JPanel = FormBuilder.createFormBuilder()
        .addComponent(TitledSeparator(AvatarBundle.get("shared.configuration")))
        .addComponent(
            FormBuilder.createFormBuilder()
                .setFormLeftIndent(16)
                .addLabeledComponent(
                    AvatarBundle.get("settingsConfigurable.shared.baseHost.label"),
                    hostField
                )
                .addLabeledComponent(
                    AvatarBundle.get("settingsConfigurable.shared.model.label"),
                    JPanel(BorderLayout(8, 0)).apply {
                        add(modelComboBox, BorderLayout.CENTER)

                    }
                )
                .setFormLeftIndent(32)
                .panel
        )
        .addComponent(UIUtil.withEmptyLeftBorder(codeCompletionConfigurationForm.getForm()))
        .addComponentFillVertically(JPanel(), 0)
        .panel

    fun getModel(): String? {
        return if (modelComboBox.isEnabled) {
            modelComboBox.item
        } else {
            null
        }
    }


    fun resetForm() {
        service<OllamaSettings>().state.run {
            hostField.text = host
            modelComboBox.item = model ?: ""
        }
    }

    fun applyChanges() {
        service<OllamaSettings>().state.run {
            host = hostField.text
            model = modelComboBox.item
        }
    }

    fun isModified() = service<OllamaSettings>().state.run {
        hostField.text != host
                || (modelComboBox.item != model && modelComboBox.isEnabled)
    }

    fun refreshModels(currentModel: String?) {
        disableModelComboBoxWithPlaceholder(DefaultComboBoxModel(arrayOf("Loading")))
        try {
            val models = runBlocking(Dispatchers.IO) {
                OllamaClient.Builder()
                    .setHost(hostField.text)
                    .build()
                    .modelTags
                    .models
                    .map { it.name }
            }
            service<OllamaSettings>().state.availableModels = models.toMutableList()
            invokeLater {
                modelComboBox.apply {
                    if (models.isNotEmpty()) {
                        model = DefaultComboBoxModel(models.toTypedArray())
                        currentModel?.let {
                            if (models.contains(currentModel)) {
                                selectedItem = currentModel
                            } else {
                                OverlayUtil.showBalloon(
                                    format(
                                        AvatarBundle.get("validation.error.model.notExists"),
                                        currentModel
                                    ),
                                    MessageType.ERROR,
                                    modelComboBox
                                )
                            }
                        }
                        isEnabled = true
                    } else {
                        model = DefaultComboBoxModel(arrayOf("No models"))
                    }
                }
            }
        } catch (ex: RuntimeException) {
            logger.error(ex)
            if (ex.cause is ConnectException) {
                OverlayUtil.showNotification(
                    "Unable to connect to Ollama server",
                    NotificationType.ERROR
                )
            } else {
                OverlayUtil.showNotification(ex.message ?: "Error", NotificationType.ERROR)
            }
            disableModelComboBoxWithPlaceholder(DefaultComboBoxModel(arrayOf("Unable to load models")))
        }
    }

    private fun disableModelComboBoxWithPlaceholder(placeholderModel: ComboBoxModel<String>) {
        invokeLater {
            modelComboBox.apply {
                model = placeholderModel
                isEnabled = false
            }
        }
    }
}
