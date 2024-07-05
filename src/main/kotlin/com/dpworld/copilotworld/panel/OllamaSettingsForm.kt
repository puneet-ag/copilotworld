package com.dpworld.copilotworld.panel

import com.dpworld.copilotworld.model.OllamaSettings
import com.dpworld.copilotworld.ollama.OllamaClient
import com.dpworld.copilotworld.panel.converted.VisionBundle
import com.dpworld.copilotworld.panel.converted.OverlayUtil
import com.dpworld.copilotworld.panel.converted.UIUtil
import com.intellij.notification.NotificationType
import com.intellij.openapi.application.invokeLater
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.observable.util.whenTextChangedFromUi
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.MessageType
import com.intellij.ui.TitledSeparator
import com.intellij.ui.components.JBPasswordField
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
        JButton(VisionBundle.get("settingsConfigurable.service.ollama.models.refresh"))
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
        //refreshModelsButton.addActionListener { refreshModels(getModel() ?: settings.model) }

        refreshModels(settings.model)
    }

    fun getForm(): JPanel = FormBuilder.createFormBuilder()
        .addComponent(TitledSeparator(VisionBundle.get("shared.configuration")))
        .addComponent(
            FormBuilder.createFormBuilder()
                .setFormLeftIndent(16)
                .addLabeledComponent(
                    VisionBundle.get("settingsConfigurable.shared.baseHost.label"),
                    hostField
                )
                .addLabeledComponent(
                    VisionBundle.get("settingsConfigurable.shared.model.label"),
                    JPanel(BorderLayout(8, 0)).apply {
                        add(modelComboBox, BorderLayout.CENTER)
                        //add(refreshModelsButton, BorderLayout.EAST)
                    }
                )
                //.addComponent(TitledSeparator(VisionBundle.get("settingsConfigurable.shared.authentication.title")))
                .setFormLeftIndent(32)

                //.addComponentToRightColumn(UIUtil.createComment("settingsConfigurable.shared.apiKey.comment"))
                .panel
        )
        .addComponent(TitledSeparator(VisionBundle.get("shared.codeCompletions")))
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
            codeCompletionConfigurationForm.isCodeCompletionsEnabled = isCodeCompletionsEnabled
            codeCompletionConfigurationForm.fimTemplate = fimTemplate
        }
    }

    fun applyChanges() {
        service<OllamaSettings>().state.run {
            host = hostField.text
            model = modelComboBox.item
            isCodeCompletionsEnabled = codeCompletionConfigurationForm.isCodeCompletionsEnabled
            fimTemplate = codeCompletionConfigurationForm.fimTemplate!!
        }
    }

    fun isModified() = service<OllamaSettings>().state.run {
        hostField.text != host
                || (modelComboBox.item != model && modelComboBox.isEnabled)
                || codeCompletionConfigurationForm.isCodeCompletionsEnabled != isCodeCompletionsEnabled
                || codeCompletionConfigurationForm.fimTemplate != fimTemplate
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
                                        VisionBundle.get("validation.error.model.notExists"),
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
