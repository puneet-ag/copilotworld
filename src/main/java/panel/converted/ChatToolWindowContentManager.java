package panel.converted;

import activity.Conversation;
import activity.Message;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComponentContainer;
import com.intellij.openapi.wm.RegisterToolWindowTask;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

@Service(Service.Level.PROJECT)
public final class ChatToolWindowContentManager {

  private final Project project;

  public ChatToolWindowContentManager(Project project) {
    this.project = project;
  }

  public void sendMessage(Message message) {
    sendMessage(message, ConversationType.DEFAULT);
  }

  public void sendMessage(Message message, ConversationType conversationType) {
    getToolWindow().show();

    if (ConfigurationSettings.getCurrentState().isCreateNewChatOnEachAction()
        || ConversationsState.getCurrentConversation() == null) {
      createNewTabPanel().sendMessage(message, conversationType);
      return;
    }

    tryFindChatTabbedPane()
        .map(tabbedPane -> tabbedPane.tryFindActiveTabPanel().orElseGet(this::createNewTabPanel))
        .orElseGet(this::createNewTabPanel)
        .sendMessage(message, conversationType);
  }

  public void displayConversation(@NotNull Conversation conversation) {
    displayChatTab();
    tryFindChatTabbedPane()
        .ifPresent(tabbedPane -> tabbedPane.tryFindTabTitle(conversation.getId())
            .ifPresentOrElse(
                title -> tabbedPane.setSelectedIndex(tabbedPane.indexOfTab(title)),
                () -> tabbedPane.addNewTab(new ChatToolWindowTabPanel(project, conversation))));
  }

  public ChatToolWindowTabPanel createNewTabPanel() {
    displayChatTab();
    return tryFindChatTabbedPane()
        .map(item -> {
          var panel = new ChatToolWindowTabPanel(
              project,
              ConversationService.getInstance().startConversation());
          item.addNewTab(panel);
          return panel;
        })
        .orElseThrow();
  }

  public void displayChatTab() {
    var toolWindow = getToolWindow();
    toolWindow.show();

    var contentManager = toolWindow.getContentManager();
    tryFindFirstChatTabContent().ifPresentOrElse(
        contentManager::setSelectedContent,
        () -> contentManager.setSelectedContent(requireNonNull(contentManager.getContent(0)))
    );
  }

  public Optional<ChatToolWindowTabbedPane> tryFindChatTabbedPane() {
    var chatTabContent = tryFindFirstChatTabContent();
    if (chatTabContent.isPresent()) {
      var chatToolWindowPanel = (ChatToolWindowPanel) chatTabContent.get().getComponent();
      return Optional.of(chatToolWindowPanel.getChatTabbedPane());
    }
    return Optional.empty();
  }

  public Optional<ChatToolWindowPanel> tryFindChatToolWindowPanel() {
    return tryFindFirstChatTabContent()
        .map(ComponentContainer::getComponent)
        .filter(component -> component instanceof ChatToolWindowPanel)
        .map(component -> (ChatToolWindowPanel) component);
  }

  public void resetAll() {
    tryFindChatTabbedPane().ifPresent(tabbedPane -> {
      tabbedPane.clearAll();
      tabbedPane.addNewTab(new ChatToolWindowTabPanel(
          project,
          ConversationService.getInstance().startConversation()));
    });
  }

  public @NotNull ToolWindow getToolWindow() {
    var toolWindowManager = ToolWindowManager.getInstance(project);
    var toolWindow = toolWindowManager.getToolWindow("CodeGPT");
    // https://intellij-support.jetbrains.com/hc/en-us/community/posts/11533368171026/comments/11538403084562
    return Objects.requireNonNullElseGet(toolWindow, () -> toolWindowManager
            .registerToolWindow(RegisterToolWindowTask.closable(
                    "CodeGPT",
                    () -> "CodeGPT",
                    Icons.DefaultSmall,
                    ToolWindowAnchor.RIGHT)));
  }

  private Optional<Content> tryFindFirstChatTabContent() {
    return Arrays.stream(getToolWindow().getContentManager().getContents())
        .filter(content -> "Chat".equals(content.getTabName()))
        .findFirst();
  }
}
