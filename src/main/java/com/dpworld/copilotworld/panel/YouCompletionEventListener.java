package com.dpworld.copilotworld.panel;

import com.dpworld.copilotworld.activity.YouSerpResult;
import com.dpworld.copilotworld.completion.CompletionEventListener;

import java.util.List;

public interface YouCompletionEventListener extends CompletionEventListener<String> {

    default void onSerpResults(List<YouSerpResult> results) {
    }
}