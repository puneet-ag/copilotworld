package panel.converted;

import activity.YouSerpResult;
import completion.CompletionEventListener;

import java.util.List;

public interface YouCompletionEventListener extends CompletionEventListener<String> {

    default void onSerpResults(List<YouSerpResult> results) {
    }
}