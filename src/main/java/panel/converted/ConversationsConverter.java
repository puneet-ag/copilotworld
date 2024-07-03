package panel.converted;

import com.fasterxml.jackson.core.type.TypeReference;
import panel.converted.BaseConverter;
import panel.converted.ConversationsContainer;

public class ConversationsConverter extends BaseConverter<ConversationsContainer> {

  public ConversationsConverter() {
    super(new TypeReference<>() {});
  }
}
