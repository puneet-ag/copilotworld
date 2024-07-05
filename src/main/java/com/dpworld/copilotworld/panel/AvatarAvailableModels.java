package com.dpworld.copilotworld.panel;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AvatarAvailableModels {

    public static List<AvatarModel> getToolWindowModels() {
        List<AvatarModel> anonymousModels = new ArrayList<>(BASE_CHAT_MODELS);


        return anonymousModels;
    }

    public static final List<AvatarModel> BASE_CHAT_MODELS = Arrays.asList(
    );

    public static final List<AvatarModel> ALL_CHAT_MODELS = new ArrayList<>(BASE_CHAT_MODELS) {{
    }};

    public static final List<AvatarModel> CODE_MODELS = Arrays.asList(
         );

    public static AvatarModel findByCode(String code) {
        List<AvatarModel> allModels = new ArrayList<>(ALL_CHAT_MODELS);
        allModels.addAll(CODE_MODELS);
        return allModels.stream()
                .filter(model -> model.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }
}
