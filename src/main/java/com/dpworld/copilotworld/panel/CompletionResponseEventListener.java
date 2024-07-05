package com.dpworld.copilotworld.panel;


import com.dpworld.copilotworld.activity.Conversation;
import com.dpworld.copilotworld.activity.Message;
import com.dpworld.copilotworld.activity.YouSerpResult;

import java.util.List;

public interface CompletionResponseEventListener {

  default void handleMessage(String message) {
  }

  default void handleError(ErrorDetails error, Throwable ex) {
  }

  default void handleTokensExceeded(Conversation conversation, Message message) {
  }

  default void handleCompleted(String fullMessage, CallParameters callParameters) {
  }

  default void handleSerpResults(List<YouSerpResult> results, Message message) {
  }
}
