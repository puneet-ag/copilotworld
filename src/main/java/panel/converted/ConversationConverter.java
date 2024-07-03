package panel.converted;

import activity.Conversation;
import com.fasterxml.jackson.core.type.TypeReference;
import panel.converted.BaseConverter;

public class ConversationConverter extends BaseConverter<Conversation> {

  public ConversationConverter() {
    super(new TypeReference<>() {});
  }
}
