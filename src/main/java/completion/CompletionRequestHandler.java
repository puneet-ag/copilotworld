//package completion;
//
//import activity.YouSerpResult;
//import okhttp3.sse.EventSource;
//import panel.converted.CompletionResponseEventListener;
//import panel.converted.ErrorDetails;
//import panel.converted.GeneralSettings;
//import panel.converted.CallParameters;
//
//import javax.swing.*;
//import java.util.List;
//
//public class CompletionRequestHandler {
//
//  private final StringBuilder messageBuilder = new StringBuilder();
//  private final CompletionResponseEventListener completionResponseEventListener;
//  private SwingWorker<Void, String> swingWorker;
//  private EventSource eventSource;
//
//  public CompletionRequestHandler(CompletionResponseEventListener completionResponseEventListener) {
//    this.completionResponseEventListener = completionResponseEventListener;
//  }
//
//  public void call(CallParameters callParameters) {
//    swingWorker = new CompletionRequestWorker(callParameters);
//    swingWorker.execute();
//  }
//
//  public void cancel() {
//    if (eventSource != null) {
//      eventSource.cancel();
//    }
//    swingWorker.cancel(true);
//  }
//
//  private EventSource startCall(
//      CallParameters callParameters,
//      CompletionEventListener<String> eventListener) {
//    try {
//      return CompletionRequestService.getInstance()
//          .getChatCompletionAsync(callParameters, eventListener);
//    } catch (Throwable ex) {
//      handleCallException(ex);
//      throw ex;
//    }
//  }
//
//  private void handleCallException(Throwable ex) {
//    var errorMessage = "Something went wrong";
//    if (ex instanceof TotalUsageExceededException) {
//      errorMessage =
//          "The length of the context exceeds the maximum limit that the model can handle. "
//              + "Try reducing the input message or maximum completion token size.";
//    }
//    completionResponseEventListener.handleError(new ErrorDetails(errorMessage), ex);
//  }
//
//  private class CompletionRequestWorker extends SwingWorker<Void, String> {
//
//    private final CallParameters callParameters;
//
//    public CompletionRequestWorker(CallParameters callParameters) {
//      this.callParameters = callParameters;
//    }
//
//    protected Void doInBackground() {
//      var settings = GeneralSettings.getCurrentState();
//      try {
//        eventSource = startCall(callParameters, new YouRequestCompletionEventListener());
//      } catch (TotalUsageExceededException e) {
//        completionResponseEventListener.handleTokensExceeded(
//            callParameters.getConversation(),
//            callParameters.getMessage());
//      } finally {
//        sendInfo(settings);
//      }
//      return null;
//    }
//
//    protected void process(List<String> chunks) {
//      callParameters.getMessage().setResponse(messageBuilder.toString());
//      for (String text : chunks) {
//        messageBuilder.append(text);
//        completionResponseEventListener.handleMessage(text);
//      }
//    }
//
//    class YouRequestCompletionEventListener implements YouCompletionEventListener {
//
//      @Override
//      public void onSerpResults(List<YouSerpResult> results) {
//        completionResponseEventListener.handleSerpResults(results, callParameters.getMessage());
//      }
//
//      @Override
//      public void onMessage(String message, EventSource eventSource) {
//        publish(message);
//      }
//
//      @Override
//      public void onComplete(StringBuilder messageBuilder) {
//        completionResponseEventListener.handleCompleted(messageBuilder.toString(), callParameters);
//      }
//
//      @Override
//      public void onCancelled(StringBuilder messageBuilder) {
//        completionResponseEventListener.handleCompleted(messageBuilder.toString(), callParameters);
//      }
//
//      @Override
//      public void onError(ErrorDetails error, Throwable ex) {
//        try {
//          completionResponseEventListener.handleError(error, ex);
//        } finally {
//        }
//      }
//    }
//
//
//  }
//}
