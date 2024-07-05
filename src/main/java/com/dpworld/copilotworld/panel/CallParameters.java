package com.dpworld.copilotworld.panel;


import com.dpworld.copilotworld.activity.Conversation;
import com.dpworld.copilotworld.activity.Message;
import com.dpworld.copilotworld.conversation.ConversationType;
import org.jetbrains.annotations.Nullable;

public class CallParameters {

  private final Conversation conversation;
  private final ConversationType conversationType;
  private final Message message;
  private final boolean retry;
  private @Nullable String imageMediaType;
  private byte[] imageData;

  public CallParameters(Conversation conversation, Message message) {
    this(conversation, ConversationType.DEFAULT, message, false);
  }

  public CallParameters(
      Conversation conversation,
      ConversationType conversationType,
      Message message,
      boolean retry) {
    this.conversation = conversation;
    this.conversationType = conversationType;
    this.message = message;
    this.retry = retry;
  }

  public Conversation getConversation() {
    return conversation;
  }

  public ConversationType getConversationType() {
    return conversationType;
  }

  public Message getMessage() {
    return message;
  }

  public boolean isRetry() {
    return retry;
  }

  public @Nullable String getImageMediaType() {
    return imageMediaType;
  }

  public void setImageMediaType(@Nullable String imageMediaType) {
    this.imageMediaType = imageMediaType;
  }

  public byte[] getImageData() {
    return imageData;
  }

  public void setImageData(byte[] imageData) {
    this.imageData = imageData;
  }
}
