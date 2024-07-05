package com.dpworld.copilotworld.actions;

public enum LandingPanelAction {

    WRITE_TESTS(
            "Generate Test Cases",
            "Generate unit test cases for this code",
            "Generate unit test cases for the selected code: {{selectedCode}}"
    ),
    CODE_INSIGHT(
            "Code Insight",
            "Insights on the selected code",
            "Insights on the selected code: {{selectedCode}}"
    );

    private final String label;
    private final String userMessage;
    private final String prompt;

    LandingPanelAction(String label, String userMessage, String prompt) {
        this.label = label;
        this.userMessage = userMessage;
        this.prompt = prompt;
    }

    public String getLabel() {
        return label;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public String getPrompt() {
        return prompt;
    }
}
