package panel.converted;


import com.intellij.openapi.components.*;

@Service
@State(
        name = "CodeGPT_CodeGPTServiceSettings_280",
        storages = @Storage("CodeGPT_CodeGPTServiceSettings_280.xml")
)
public final class CodeGPTServiceSettings extends SimplePersistentStateComponent<CodeGPTServiceSettingsState> {

    public CodeGPTServiceSettings() {
        super(new CodeGPTServiceSettingsState());
    }
}

class CodeGPTServiceSettingsState extends BaseState {
    private CodeGPTServiceChatCompletionSettingsState chatCompletionSettings = new CodeGPTServiceChatCompletionSettingsState();
    private CodeGPTServiceCodeCompletionSettingsState codeCompletionSettings = new CodeGPTServiceCodeCompletionSettingsState();

    public CodeGPTServiceChatCompletionSettingsState getChatCompletionSettings() {
        return chatCompletionSettings;
    }

    public void setChatCompletionSettings(CodeGPTServiceChatCompletionSettingsState chatCompletionSettings) {
        this.chatCompletionSettings = chatCompletionSettings;
    }

    public CodeGPTServiceCodeCompletionSettingsState getCodeCompletionSettings() {
        return codeCompletionSettings;
    }

    public void setCodeCompletionSettings(CodeGPTServiceCodeCompletionSettingsState codeCompletionSettings) {
        this.codeCompletionSettings = codeCompletionSettings;
    }
}

class CodeGPTServiceChatCompletionSettingsState extends BaseState {
    private String model = "llama-3-8b";

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}

class CodeGPTServiceCodeCompletionSettingsState extends BaseState {
    private boolean codeCompletionsEnabled = false;
    private String model = "starcoder-7b";

    public boolean isCodeCompletionsEnabled() {
        return codeCompletionsEnabled;
    }

    public void setCodeCompletionsEnabled(boolean codeCompletionsEnabled) {
        this.codeCompletionsEnabled = codeCompletionsEnabled;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}

