package com.dpworld.copilotworld.panel.converted;

import com.fasterxml.jackson.core.type.TypeReference;

public class ConversationsConverter extends BaseConverter<ConversationsContainer> {

  public ConversationsConverter() {
    super(new TypeReference<>() {});
  }
}
