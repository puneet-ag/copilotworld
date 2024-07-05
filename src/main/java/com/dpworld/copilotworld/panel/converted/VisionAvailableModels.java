package com.dpworld.copilotworld.panel.converted;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VisionAvailableModels {

    public static List<VisionModel> getToolWindowModels() {
        List<VisionModel> anonymousModels = new ArrayList<>(BASE_CHAT_MODELS);
        return anonymousModels;
    }

    public static final List<VisionModel> BASE_CHAT_MODELS = Arrays.asList(
    );

    public static final List<VisionModel> ALL_CHAT_MODELS = new ArrayList<>(BASE_CHAT_MODELS) {{
        addAll(Arrays.asList(
                new VisionModel("Llama 3 (8B) - FREE", "llama-3-8b",Icons.Default),
                new VisionModel("Code Llama (70B)", "codellama:chat",Icons.Default),
                new VisionModel("Mixtral (8x22B)", "mixtral-8x22b",Icons.Default),
                new VisionModel("DeepSeek Coder (33B)", "deepseek-coder-33b",Icons.Default),
                new VisionModel("WizardLM-2 (8x22B)", "wizardlm-2-8x22b",Icons.Default)
        ));
    }};

    public static final List<VisionModel> CODE_MODELS = Arrays.asList(
            new VisionModel("GPT-3.5 Turbo Instruct", "gpt-3.5-turbo-instruct",Icons.Default),
            new VisionModel("StarCoder (16B)", "starcoder-16b",Icons.Default),
            new VisionModel("StarCoder (7B) - FREE", "starcoder-7b",Icons.Default),
            new VisionModel("Code Llama (70B)", "codellama:code",Icons.Default),
            new VisionModel("Code Llama Python (70B)", "codellama-python",Icons.Default),
            new VisionModel("WizardCoder Python (34B)", "wizardcoder-python",Icons.Default),
            new VisionModel("Phind Code LLaMA v2 (34B)", "phind-codellama",Icons.Default)
    );

    public static VisionModel findByCode(String code) {
        List<VisionModel> allModels = new ArrayList<>(ALL_CHAT_MODELS);
        allModels.addAll(CODE_MODELS);
        return allModels.stream()
                .filter(model -> model.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }
}
