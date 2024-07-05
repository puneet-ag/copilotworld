package com.dpworld.copilotworld.completion;

import com.dpworld.copilotworld.panel.ErrorDetails;
import okhttp3.sse.EventSource;

public interface CompletionEventListener<T> {

  default void onOpen() {
  }

  default void onMessage(T message, String rawMessage, EventSource eventSource) {
  }

  default void onMessage(T message, EventSource eventSource) {
  }

  default void onComplete(StringBuilder messageBuilder) {
  }

  default void onCancelled(StringBuilder messageBuilder) {
  }

  default void onError(ErrorDetails error, Throwable ex) {
  }
}
