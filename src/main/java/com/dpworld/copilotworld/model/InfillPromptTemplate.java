package com.dpworld.copilotworld.model;

import java.util.List;

public enum InfillPromptTemplate {

    DEEPSEEK_CODER("DeepSeek Coder", List.of("")) {
        @Override
        public String buildPrompt(String prefix, String suffix) {
            return "<｜fim▁begin｜>" + prefix + "<｜fim▁hole｜>" + suffix + "<｜fim▁end｜>";
        }
    };
    private final String label;
    private final List<String> stopTokens;

    InfillPromptTemplate(String label, List<String> stopTokens) {
        this.label = label;
        this.stopTokens = stopTokens;
    }

    public abstract String buildPrompt(String prefix, String suffix);

    @Override
    public String toString() {
        return label;
    }

    public String getLabel() {
        return label;
    }

    public List<String> getStopTokens() {
        return stopTokens;
    }
}

