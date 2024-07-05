package com.dpworld.copilotworld.panel;

import com.dpworld.copilotworld.activity.Conversation;
import com.fasterxml.jackson.core.type.TypeReference;

public class ConversationConverter extends BaseConverter<Conversation> {

  public ConversationConverter() {
    super(new TypeReference<>() {});
  }
}
