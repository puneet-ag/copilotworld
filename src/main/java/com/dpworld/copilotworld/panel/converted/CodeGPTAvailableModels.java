package com.dpworld.copilotworld.panel.converted;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CodeGPTAvailableModels {

    public static List<CodeGPTModel> getToolWindowModels() {
        List<CodeGPTModel> anonymousModels = new ArrayList<>(BASE_CHAT_MODELS);
        anonymousModels.add(new CodeGPTModel(
                "Llama 3 (8B) - FREE",
                "llama-3-8b",
                Icons.Default
        ));

        return anonymousModels;
    }

    public static final List<CodeGPTModel> BASE_CHAT_MODELS = Arrays.asList(
            new CodeGPTModel("GPT-4o", "gpt-4o",Icons.Default),
            new CodeGPTModel("GPT-3.5 Turbo", "gpt-3.5-turbo",Icons.Default),
            new CodeGPTModel("Claude 3 Opus", "claude-3-opus",Icons.Default),
            new CodeGPTModel("Claude 3.5 Sonnet", "claude-3.5-sonnet",Icons.Default),
            new CodeGPTModel("Claude 3 Sonnet", "claude-3-sonnet",Icons.Default),
            new CodeGPTModel("DBRX", "dbrx",Icons.Default),
            new CodeGPTModel("Llama 3 (70B)", "llama-3-70b",Icons.Default)
    );

    public static final List<CodeGPTModel> ALL_CHAT_MODELS = new ArrayList<>(BASE_CHAT_MODELS) {{
        addAll(Arrays.asList(
                new CodeGPTModel("Llama 3 (8B) - FREE", "llama-3-8b",Icons.Default),
                new CodeGPTModel("Code Llama (70B)", "codellama:chat",Icons.Default),
                new CodeGPTModel("Mixtral (8x22B)", "mixtral-8x22b",Icons.Default),
                new CodeGPTModel("DeepSeek Coder (33B)", "deepseek-coder-33b",Icons.Default),
                new CodeGPTModel("WizardLM-2 (8x22B)", "wizardlm-2-8x22b",Icons.Default)
        ));
    }};

    public static final List<CodeGPTModel> CODE_MODELS = Arrays.asList(
            new CodeGPTModel("GPT-3.5 Turbo Instruct", "gpt-3.5-turbo-instruct",Icons.Default),
            new CodeGPTModel("StarCoder (16B)", "starcoder-16b",Icons.Default),
            new CodeGPTModel("StarCoder (7B) - FREE", "starcoder-7b",Icons.Default),
            new CodeGPTModel("Code Llama (70B)", "codellama:code",Icons.Default),
            new CodeGPTModel("Code Llama Python (70B)", "codellama-python",Icons.Default),
            new CodeGPTModel("WizardCoder Python (34B)", "wizardcoder-python",Icons.Default),
            new CodeGPTModel("Phind Code LLaMA v2 (34B)", "phind-codellama",Icons.Default)
    );

    public static CodeGPTModel findByCode(String code) {
        List<CodeGPTModel> allModels = new ArrayList<>(ALL_CHAT_MODELS);
        allModels.addAll(CODE_MODELS);
        return allModels.stream()
                .filter(model -> model.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }
}
