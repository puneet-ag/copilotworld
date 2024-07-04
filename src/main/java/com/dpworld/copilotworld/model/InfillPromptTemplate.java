package com.dpworld.copilotworld.model;

import java.util.List;

public enum InfillPromptTemplate {

//    OPENAI("OpenAI", null) {
//        @Override
//        public String buildPrompt(String prefix, String suffix) {
//            return " " + prefix + " " + suffix + " ";
//        }
//    },
//    CODE_LLAMA("Code Llama", List.of("<EOT>")) {
//        @Override
//        public String buildPrompt(String prefix, String suffix) {
//            return "<PRE> " + prefix + " <SUF>" + suffix + " <MID>";
//        }
//    },
//    CODE_GEMMA("CodeGemma Instruct", List.of("", "", "", "", "<eos>")) {
//        @Override
//        public String buildPrompt(String prefix, String suffix) {
//            return prefix + suffix;
//        }
//    },
//    CODE_QWEN("CodeQwen1.5", List.of("")) {
//        @Override
//        public String buildPrompt(String prefix, String suffix) {
//            return "<fim_prefix>" + prefix + "<fim_suffix>" + suffix + "<fim_middle>";
//        }
//    },
//    STABILITY("Stability AI", List.of("")) {
//        @Override
//        public String buildPrompt(String prefix, String suffix) {
//            return "<fim_prefix>" + prefix + "<fim_suffix>" + suffix + "<fim_middle>";
//        }
//    },
    DEEPSEEK_CODER("DeepSeek Coder", List.of("")) {
        @Override
        public String buildPrompt(String prefix, String suffix) {
            return "<｜fim▁begin｜>" + prefix + "<｜fim▁hole｜>" + suffix + "<｜fim▁end｜>";
        }
    };
//    CODESTRAL("Codestral", List.of("</s>")) {
//        @Override
//        public String buildPrompt(String prefix, String suffix) {
//            return "[SUFFIX]" + suffix + "[PREFIX] " + prefix;
//        }
//    };

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

