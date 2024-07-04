package com.dpworld.copilotworld.panel.converted;

import com.dpworld.copilotworld.activity.Conversation;
import com.fasterxml.jackson.core.type.TypeReference;

public class ConversationConverter extends BaseConverter<Conversation> {

  public ConversationConverter() {
    super(new TypeReference<>() {});
  }
}
