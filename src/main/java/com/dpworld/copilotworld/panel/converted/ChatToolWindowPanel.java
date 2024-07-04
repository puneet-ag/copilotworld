package com.dpworld.copilotworld.panel.converted;

import com.intellij.diff.actions.impl.OpenInEditorAction;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultCompactActionGroup;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.util.Disposer;
import com.intellij.ui.components.ActionLink;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.Collections.emptyList;

public class ChatToolWindowPanel extends SimpleToolWindowPanel {

  private final ToolWindowFooterNotification selectedFilesNotification;
  private final ToolWindowFooterNotification imageFileAttachmentNotification;
  private final ActionLink upgradePlanLink;
  private ChatToolWindowTabbedPane tabbedPane;

  public ChatToolWindowPanel(
      @NotNull Project project,
      @NotNull Disposable parentDisposable) {
    super(true);
    selectedFilesNotification = new ToolWindowFooterNotification(
        () -> clearSelectedFilesNotification(project));
    imageFileAttachmentNotification = new ToolWindowFooterNotification(() ->
        project.putUserData(CodeGPTKeys.IMAGE_ATTACHMENT_FILE_PATH, ""));
    upgradePlanLink = new ActionLink("Upgrade your plan", event -> {
      BrowserUtil.browse("https://codegpt.ee/#pricing");
    });
    upgradePlanLink.setFont(JBUI.Fonts.smallFont());
    upgradePlanLink.setExternalLinkIcon();
    upgradePlanLink.setVisible(false);

    init(project, parentDisposable);

    var messageBusConnection = project.getMessageBus().connect();
    messageBusConnection.subscribe(IncludeFilesInContextNotifier.FILES_INCLUDED_IN_CONTEXT_TOPIC,
        (IncludeFilesInContextNotifier) this::displaySelectedFilesNotification);
    messageBusConnection.subscribe(AttachImageNotifier.IMAGE_ATTACHMENT_FILE_PATH_TOPIC,
        (AttachImageNotifier) filePath -> imageFileAttachmentNotification.show(
            Path.of(filePath).getFileName().toString(),
            "File path: " + filePath));
    messageBusConnection.subscribe(ProviderChangeNotifier.PROVIDER_CHANGE_TOPIC,
        (ProviderChangeNotifier) provider -> {
          if (provider == ServiceType.CODEGPT) {
            var userDetails = CodeGPTKeys.CODEGPT_USER_DETAILS.get(project);

          } else {
            upgradePlanLink.setVisible(false);
          }
        });
    messageBusConnection.subscribe(CodeGPTUserDetailsNotifier.getCODEGPT_USER_DETAILS_TOPIC(),
        (CodeGPTUserDetailsNotifier) userDetails -> {
          if (userDetails != null) {
            var provider = ApplicationManager.getApplication().getService(GeneralSettings.class)
                .getState()
                .getSelectedService();
          }
        });
  }

  public ChatToolWindowTabbedPane getChatTabbedPane() {
    return tabbedPane;
  }

  public void displaySelectedFilesNotification(List<ReferencedFile> referencedFiles) {
    if (referencedFiles.isEmpty()) {
      return;
    }

    var referencedFilePaths = referencedFiles.stream()
        .map(ReferencedFile::getFilePath)
        .toList();
    selectedFilesNotification.show(
        referencedFiles.size() + " files selected",
        selectedFilesNotificationDescription(referencedFilePaths));
  }

  private String selectedFilesNotificationDescription(List<String> referencedFilePaths) {
    var html = referencedFilePaths.stream()
        .map(filePath -> format("<li>%s</li>", Paths.get(filePath).getFileName().toString()))
        .collect(Collectors.joining());
    return format("<ul style=\"margin: 4px 12px;\">%s</ul>", html);
  }

  public void clearNotifications(Project project) {
    selectedFilesNotification.hideNotification();
    imageFileAttachmentNotification.hideNotification();

    project.putUserData(CodeGPTKeys.IMAGE_ATTACHMENT_FILE_PATH, "");
    project.putUserData(CodeGPTKeys.SELECTED_FILES, emptyList());
  }

  private void init(Project project, Disposable parentDisposable) {
    var conversation = ConversationsState.getCurrentConversation();
    if (conversation == null) {
      conversation = ConversationService.getInstance().startConversation();
    }

    var tabPanel = new ChatToolWindowTabPanel(project, conversation);
    tabbedPane = createTabbedPane(tabPanel, parentDisposable);
    Runnable onAddNewTab = () -> {
      tabbedPane.addNewTab(new ChatToolWindowTabPanel(
          project,
          ConversationService.getInstance().startConversation()));
      repaint();
      revalidate();
    };
    var actionToolbarPanel = new JPanel(new BorderLayout());
    actionToolbarPanel.add(
        createActionToolbar(project, tabbedPane, onAddNewTab).getComponent(),
        BorderLayout.LINE_START);
    actionToolbarPanel.add(upgradePlanLink, BorderLayout.LINE_END);

    setToolbar(actionToolbarPanel);
    var notificationContainer = new JPanel(new BorderLayout());
    notificationContainer.setLayout(new BoxLayout(notificationContainer, BoxLayout.PAGE_AXIS));
    notificationContainer.add(selectedFilesNotification);
    notificationContainer.add(imageFileAttachmentNotification);
    setContent(JBUI.Panels.simplePanel(tabbedPane).addToBottom(notificationContainer));

    Disposer.register(parentDisposable, tabPanel);
  }

  private ActionToolbar createActionToolbar(
      Project project,
      ChatToolWindowTabbedPane tabbedPane,
      Runnable onAddNewTab) {
    var actionGroup = new DefaultCompactActionGroup("TOOLBAR_ACTION_GROUP", false);
    actionGroup.add(new CreateNewConversationAction(onAddNewTab));
    actionGroup.add(
        new ClearChatWindowAction(() -> tabbedPane.resetCurrentlyActiveTabPanel(project)));
    actionGroup.addSeparator();
    actionGroup.add(new OpenInEditorAction());

    var toolbar = ActionManager.getInstance()
        .createActionToolbar("NAVIGATION_BAR_TOOLBAR", actionGroup, true);
    toolbar.setTargetComponent(this);
    return toolbar;
  }

  private ChatToolWindowTabbedPane createTabbedPane(
      ChatToolWindowTabPanel tabPanel,
      Disposable parentDisposable) {
    var tabbedPane = new ChatToolWindowTabbedPane(parentDisposable);
    tabbedPane.addNewTab(tabPanel);
    return tabbedPane;
  }

  public void clearSelectedFilesNotification(Project project) {
    project.putUserData(CodeGPTKeys.SELECTED_FILES, emptyList());
    project.getMessageBus()
        .syncPublisher(IncludeFilesInContextNotifier.FILES_INCLUDED_IN_CONTEXT_TOPIC)
        .filesIncluded(emptyList());
  }
}
