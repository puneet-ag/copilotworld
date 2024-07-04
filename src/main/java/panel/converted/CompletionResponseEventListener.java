package panel.converted;


import activity.Conversation;
import activity.Message;
import activity.YouSerpResult;
import panel.converted.CallParameters;
import panel.converted.ErrorDetails;

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
