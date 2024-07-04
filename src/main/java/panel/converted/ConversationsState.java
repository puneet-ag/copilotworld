package panel.converted;

import activity.Conversation;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.OptionTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

@State(
    name = "panel.converted.ConversationsState",
    storages = @Storage("ChatGPTConversations_170.xml"))
public class ConversationsState implements PersistentStateComponent<ConversationsState> {

  @OptionTag(converter = ConversationsConverter.class)
  public ConversationsContainer conversationsContainer = new ConversationsContainer();

  @OptionTag(converter = ConversationConverter.class)
  public Conversation currentConversation;

  public boolean discardAllTokenLimits;

  public static ConversationsState getInstance() {
    return ApplicationManager.getApplication().getService(ConversationsState.class);
  }

  @Nullable
  @Override
  public ConversationsState getState() {
    return this;
  }

  @Override
  public void loadState(@NotNull ConversationsState state) {
    XmlSerializerUtil.copyBean(state, this);
  }

  public void discardAllTokenLimits() {
    this.discardAllTokenLimits = true;
  }

  public void setCurrentConversation(@Nullable Conversation conversation) {
    this.currentConversation = conversation;
  }

  public static @Nullable Conversation getCurrentConversation() {
    return getInstance().currentConversation;
  }

  public Map<String, List<Conversation>> getConversationsMapping() {
    return conversationsContainer.getConversationsMapping();
  }
}
