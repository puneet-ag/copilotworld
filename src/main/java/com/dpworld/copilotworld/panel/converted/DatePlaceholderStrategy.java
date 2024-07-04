package com.dpworld.copilotworld.panel.converted;

import java.time.LocalDate;

public class DatePlaceholderStrategy implements PlaceholderStrategy {
    @Override
    public String getReplacementValue() {
        return LocalDate.now().toString();
    }
}
