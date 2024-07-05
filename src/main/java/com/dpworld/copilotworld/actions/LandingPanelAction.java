package com.dpworld.copilotworld.actions;

public enum LandingPanelAction {
    FIND_BUGS(
            "Find Bugs",
            "Find bugs in this code",
            "Find bugs and output code with bugs fixed in the selected code: {{selectedCode}}"
    ),
    WRITE_TESTS(
            "Write Tests",
            "Write unit tests for this code",
            "Write unit tests for the selected code: {{selectedCode}}"
    ),
    EXPLAIN(
            "Explain",
            "Explain the selected code",
            "Explain the selected code: {{selectedCode}}"
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
