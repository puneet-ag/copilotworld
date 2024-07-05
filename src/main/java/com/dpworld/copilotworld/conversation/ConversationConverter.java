package com.dpworld.copilotworld.conversation;

import com.dpworld.copilotworld.activity.Conversation;
import com.dpworld.copilotworld.panel.BaseConverter;
import com.fasterxml.jackson.core.type.TypeReference;

public class ConversationConverter extends BaseConverter<Conversation> {

  public ConversationConverter() {
    super(new TypeReference<>() {});
  }
}
