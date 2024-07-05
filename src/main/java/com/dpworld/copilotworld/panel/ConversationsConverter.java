package com.dpworld.copilotworld.panel;

import com.fasterxml.jackson.core.type.TypeReference;

public class ConversationsConverter extends BaseConverter<ConversationsContainer> {

  public ConversationsConverter() {
    super(new TypeReference<>() {});
  }
}
