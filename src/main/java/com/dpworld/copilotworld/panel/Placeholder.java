package com.dpworld.copilotworld.panel;
public enum Placeholder {
    DATE_ISO_8601("Current date in ISO 8601 format, e.g. 2021-01-01."),
    BRANCH_NAME("The name of the current branch."),
    PREFIX("Code before the cursor."),
    SUFFIX("Code after the cursor."),
    FIM_PROMPT("Prebuilt Fill-In-The-Middle (FIM) prompt using the specified template.");

    private final String description;

    Placeholder(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}


