package com.dpworld.copilotworld.conversation;

import com.dpworld.copilotworld.panel.BaseConverter;
import com.fasterxml.jackson.core.type.TypeReference;

public class ConversationsConverter extends BaseConverter<ConversationsContainer> {

  public ConversationsConverter() {
    super(new TypeReference<>() {});
  }
}
