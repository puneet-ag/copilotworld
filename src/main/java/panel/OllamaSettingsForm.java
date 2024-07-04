package panel;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.service;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.observable.util.whenTextChangedFromUi;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.MessageType;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBPasswordField;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.runBlocking;
import model.OllamaSettings;
import ollama.OllamaClient;
import panel.converted.CodeGPTBundle;
import panel.converted.OverlayUtil;

import javax.swing.*;
import java.awt.*;
import java.net.ConnectException;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class OllamaSettingsForm {
    private final JButton refreshModelsButton = new JButton(CodeGPTBundle.get("settingsConfigurable.service.ollama.models.refresh"));
    private final JBTextField hostField;
    private final ComboBox<String> modelComboBox;
    private final CodeCompletionConfigurationForm codeCompletionConfigurationForm;
    private final JBPasswordField apiKeyField;

    private static final Logger logger = Logger.getInstance(OllamaSettingsForm.class);

    public OllamaSettingsForm() {
        OllamaSettings settings = service(OllamaSettings.class);
        codeCompletionConfigurationForm = new CodeCompletionConfigurationForm(settings.getCodeCompletionsEnabled(), settings.getFimTemplate());
        DefaultComboBoxModel<String> emptyModelsComboBoxModel = new DefaultComboBoxModel<>(new String[]{"Hit refresh to see models for this host"});
        modelComboBox = new ComboBox<>(emptyModelsComboBoxModel);
        modelComboBox.setEnabled(false);
        hostField = new JBTextField();
        hostField.setText(settings.getHost());
        hostField.whenTextChangedFromUi((oldValue, newValue) -> {
            modelComboBox.setModel(emptyModelsComboBoxModel);
            modelComboBox.setEnabled(false);
        });
        refreshModelsButton.addActionListener(e -> refreshModels(getModel() != null ? getModel() : settings.getModel()));
        apiKeyField = new JBPasswordField();
        apiKeyField.setColumns(30);
        apiKeyField.setText(runBlocking(Dispatchers.IO, () -> CredentialsStore.getCredential(CredentialKey.OLLAMA_API_KEY)));
        refreshModels(settings.getModel());
    }

    public JPanel getForm() {
        return FormBuilder.createFormBuilder()
                .addComponent(new TitledSeparator(CodeGPTBundle.get("shared.configuration")))
                .addComponent(FormBuilder.createFormBuilder().setFormLeftIndent(16)
                        .addLabeledComponent(CodeGPTBundle.get("settingsConfigurable.shared.baseHost.label"), hostField)
                        .addLabeledComponent(CodeGPTBundle.get("settingsConfigurable.shared.model.label"),
                                new JPanel(new BorderLayout(8, 0)) {
                                    {
                                        add(modelComboBox, BorderLayout.CENTER);
                                        add(refreshModelsButton, BorderLayout.EAST);
                                    }
                                })
                        .addComponent(new TitledSeparator(CodeGPTBundle.get("settingsConfigurable.shared.authentication.title")))
                        .setFormLeftIndent(32)
                        .addLabeledComponent(CodeGPTBundle.get("settingsConfigurable.shared.apiKey.label"), apiKeyField)
                        .addComponentToRightColumn(UIUtil.createComment("settingsConfigurable.shared.apiKey.comment")).getPanel())
                .addComponent(new TitledSeparator(CodeGPTBundle.get("shared.codeCompletions")))
                .addComponent(UIUtil.withEmptyLeftBorder(codeCompletionConfigurationForm.getForm()))
                .addComponentFillVertically(new JPanel(), 0).getPanel();
    }

    public String getModel() {
        return modelComboBox.isEnabled() ? modelComboBox.getItem() : null;
    }

    public String getApiKey() {
        return new String(apiKeyField.getPassword()).isEmpty() ? null : new String(apiKeyField.getPassword());
    }

    public void resetForm() {
        OllamaSettings settings = service(OllamaSettings.class);
        hostField.setText(settings.getHost());
        modelComboBox.setSelectedItem(Objects.requireNonNullElse(settings.getModel(), ""));
        codeCompletionConfigurationForm.setCodeCompletionsEnabled(settings.getCodeCompletionsEnabled());
        codeCompletionConfigurationForm.setFimTemplate(settings.getFimTemplate());
        apiKeyField.setText(CredentialsStore.getCredential(CredentialKey.OLLAMA_API_KEY));
    }

    public void applyChanges() {
        OllamaSettings settings = service(OllamaSettings.class);
        settings.setHost(hostField.getText());
        settings.setModel(modelComboBox.isEnabled() ? modelComboBox.getItem() : null);
        settings.setCodeCompletionsEnabled(codeCompletionConfigurationForm.getCodeCompletionsEnabled());
        settings.setFimTemplate(codeCompletionConfigurationForm.getFimTemplate());
    }

    public boolean isModified() {
        OllamaSettings settings = service(OllamaSettings.class);
        return !hostField.getText().equals(settings.getHost()) || (modelComboBox.isEnabled() ? !modelComboBox.getItem().equals(settings.getModel()) : false) || codeCompletionConfigurationForm.getCodeCompletionsEnabled() != settings.getCodeCompletionsEnabled() || !codeCompletionConfigurationForm.getFimTemplate().equals(settings.getFimTemplate()) || !getApiKey().equals(CredentialsStore.getCredential(CredentialKey.OLLAMA_API_KEY));
    }

    public void refreshModels(String currentModel) {
        disableModelComboBoxWithPlaceholder(new DefaultComboBoxModel<>(new String[]{"Loading"}));
        try {
            String[] models = runBlocking(Dispatchers.IO, () -> {
                OllamaClient ollamaClient = new OllamaClient.Builder().setHost(hostField.getText()).setApiKey(getApiKey()).build();
                return ollamaClient.getModelTags().getModels().stream().map(m -> m.getName()).collect(Collectors.toList()).toArray(new String[0]);
            });
            service(OllamaSettings.class).setAvailableModels(Arrays.asList(models));
            ApplicationManager.getApplication().invokeLater(() -> {
                if (models.length != 0) {
                    modelComboBox.setModel(new DefaultComboBoxModel<>(models));
                    currentModel = models;
                    if (!Arrays.asList(models).contains(currentModel)) {
                        OverlayUtil.showBalloon(format(CodeGPTBundle.get("validation.error.model.notExists"), currentModel), MessageType.ERROR, modelComboBox);
                    }
                } else {
                    modelComboBox.setModel(new DefaultComboBoxModel<>(new String[]{"No models"}));
                }
                modelComboBox.setEnabled(true);
            });
        } catch (Exception e) {
            logger.error(e);
            if (e.getCause() instanceof ConnectException) {
                OverlayUtil.showNotification("Unable to connect to Ollama server", NotificationType.ERROR);
            } else {
                OverlayUtil.showNotification(e.getMessage() != null ? e.getMessage() : "Error", NotificationType.ERROR);
            }
            disableModelComboBoxWithPlaceholder(new DefaultComboBoxModel<>(new String[]{"Unable to load models"}));
        }
    }

    private void disableModelComboBoxWithPlaceholder(ComboBoxModel<String> placeholderModel) {
        ApplicationManager.getApplication().invokeLater(() -> {
            modelComboBox.setModel(placeholderModel);
            modelComboBox.setEnabled(false);
        });
    }
}

