package com.dpworld.copilotworld.panel.converted;

import com.dpworld.copilotworld.activity.Conversation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConversationsContainer {

  private Map<String, List<Conversation>> conversationsMapping = new HashMap<>();

  public Map<String, List<Conversation>> getConversationsMapping() {
    return conversationsMapping;
  }

  public void setConversationsMapping(Map<String, List<Conversation>> conversationsMapping) {
    this.conversationsMapping = conversationsMapping;
  }
}
