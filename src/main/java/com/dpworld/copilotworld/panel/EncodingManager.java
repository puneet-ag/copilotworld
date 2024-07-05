package com.dpworld.copilotworld.panel;

import com.dpworld.copilotworld.activity.Conversation;
import com.dpworld.copilotworld.activity.Message;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.diagnostic.Logger;
import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingRegistry;
import com.knuddels.jtokkit.api.EncodingType;
import com.knuddels.jtokkit.api.IntArrayList;

import java.util.List;
import java.util.stream.Stream;

@Service
public final class EncodingManager {

  private static final Logger LOG = Logger.getInstance(EncodingManager.class);

  private final EncodingRegistry registry = Encodings.newDefaultEncodingRegistry();
  private final Encoding encoding = registry.getEncoding(EncodingType.CL100K_BASE);

  private EncodingManager() {
  }

  public static EncodingManager getInstance() {
    return ApplicationManager.getApplication().getService(EncodingManager.class);
  }

  public int countConversationTokens(Conversation conversation) {
    return (conversation == null ? Stream.<Message>empty() : conversation.getMessages().stream())
        .mapToInt(
            message -> countTokens(message.getPrompt()) + countTokens(message.getResponse()))
        .sum();
  }


  public int countMessageTokens(String role, String content) {
    var tokensPerMessage = 4; 
    return countTokens(role + content) + tokensPerMessage;
  }

  public int countTokens(String text) {
    try {
      
      return encoding.countTokens(text);
    } catch (Exception | Error ex) {
      LOG.warn("Could not count tokens for: " + text, ex);
      return 0;
    }
  }
  public String truncateText(String text, int maxTokens, boolean fromStart) {
    var tokens = encoding.encode(text);
    int tokensToRetrieve = Math.min(maxTokens, tokens.size());
    int startIndex = fromStart ? 0 : tokens.size() - tokensToRetrieve;
    var truncatedList =
        tokens.boxed().subList(startIndex, startIndex + tokensToRetrieve);
    return encoding.decode(convertToIntArrayList(truncatedList));
  }

  private IntArrayList convertToIntArrayList(List<Integer> tokens) {
    var result = new IntArrayList(tokens.size());
    tokens.forEach(result::add);
    return result;
  }
}
