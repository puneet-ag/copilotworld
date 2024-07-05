package com.dpworld.copilotworld.conversation.chat;

import com.dpworld.copilotworld.actions.ModelComboBoxAction;
import com.dpworld.copilotworld.activity.Conversation;
import com.dpworld.copilotworld.activity.Message;
import com.dpworld.copilotworld.avatar.AvatarKeys;
import com.dpworld.copilotworld.completion.CompletionRequestHandler;
import com.dpworld.copilotworld.completion.CompletionRequestProvider;
import com.dpworld.copilotworld.completion.CompletionRequestService;
import com.dpworld.copilotworld.configurations.GeneralSettings;
import com.dpworld.copilotworld.conversation.ConversationService;
import com.dpworld.copilotworld.conversation.ConversationType;
import com.dpworld.copilotworld.listener.ToolWindowCompletionResponseEventListener;
import com.dpworld.copilotworld.panel.*;
import com.dpworld.copilotworld.util.CodeEditorHelper;
import com.dpworld.copilotworld.util.FileUtilIntellij;
import com.dpworld.copilotworld.util.OverlayUtil;
import com.dpworld.copilotworld.util.UIUtil;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.JBUI.Borders;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static java.lang.String.format;

public class ChatToolWindowTabPanel implements Disposable {

  private static final Logger LOG = Logger.getInstance(ChatToolWindowTabPanel.class);

  private final Project project;
  private final JPanel rootPanel;
  private final Conversation conversation;
  private final UserPromptTextArea userPromptTextArea;
  private final ConversationService conversationService;
  private final TotalTokensPanel totalTokensPanel;
  private final ChatToolWindowScrollablePanel toolWindowScrollablePanel;

  public ChatToolWindowTabPanel(@NotNull Project project, @NotNull Conversation conversation) {
    this.project = project;
    this.conversation = conversation;
    conversationService = ConversationService.getInstance();
    toolWindowScrollablePanel = new ChatToolWindowScrollablePanel();
    totalTokensPanel = new TotalTokensPanel(
        project,
        conversation,
        CodeEditorHelper.fetchSelectedText(project),
        this);
    userPromptTextArea = new UserPromptTextArea(this::handleSubmit, totalTokensPanel);
    rootPanel = createRootPanel();
    userPromptTextArea.requestFocusInWindow();
    userPromptTextArea.requestFocus();

    if (conversation.getMessages().isEmpty()) {
      displayLandingView();
    } else {
      displayConversation(conversation);
    }
  }

  public void dispose() {
    LOG.info("Disposing BaseChatToolWindowTabPanel component");
  }

  public JComponent getContent() {
    return rootPanel;
  }

  public Conversation getConversation() {
    return conversation;
  }

  public TotalTokensDetails getTokenDetails() {
    return totalTokensPanel.getTokenDetails();
  }

  public void requestFocusForTextArea() {
    userPromptTextArea.focus();
  }

  public void displayLandingView() {
    toolWindowScrollablePanel.displayLandingView(getLandingView());
    totalTokensPanel.updateConversationTokens(conversation);
  }

  public void sendMessage(Message message) {
    sendMessage(message, ConversationType.DEFAULT);
  }

  public void sendMessage(Message message, ConversationType conversationType) {
    SwingUtilities.invokeLater(() -> {
      var referencedFiles = project.getUserData(AvatarKeys.SELECTED_FILES);
      var chatToolWindowPanel = project.getService(ChatToolWindowContentManager.class)
          .tryFindChatToolWindowPanel();
      if (referencedFiles != null && !referencedFiles.isEmpty()) {
        var referencedFilePaths = referencedFiles.stream()
            .map(ReferencedFile::getFilePath)
            .toList();
        message.setReferencedFilePaths(referencedFilePaths);
        message.setUserMessage(message.getPrompt());
        message.setPrompt(CompletionRequestProvider.getPromptWithContext(referencedFiles, message.getPrompt()));

        totalTokensPanel.updateReferencedFilesTokens(referencedFiles);

        chatToolWindowPanel.ifPresent(panel -> panel.clearNotifications(project));
      }

      var userMessagePanel = new UserMessagePanel(project, message, this);
      var attachedFilePath = AvatarKeys.IMAGE_ATTACHMENT_FILE_PATH.get(project);
      var callParameters = getCallParameters(conversationType, message, attachedFilePath);
      if (callParameters.getImageData() != null) {
        message.setImageFilePath(attachedFilePath);
        chatToolWindowPanel.ifPresent(panel -> panel.clearNotifications(project));
        userMessagePanel.displayImage(attachedFilePath);
      }

      var messagePanel = toolWindowScrollablePanel.addMessage(message.getId());
      messagePanel.add(userMessagePanel);

      var responsePanel = createResponsePanel(message, conversationType);
      messagePanel.add(responsePanel);
      updateTotalTokens(message);
      call(callParameters, responsePanel);
    });
  }

  private CallParameters getCallParameters(
      ConversationType conversationType,
      Message message,
      @Nullable String attachedFilePath) {
    var callParameters = new CallParameters(conversation, conversationType, message, false);
    if (attachedFilePath != null && !attachedFilePath.isEmpty()) {
      try {
        callParameters.setImageData(Files.readAllBytes(Path.of(attachedFilePath)));
        callParameters.setImageMediaType(FileUtilIntellij.getImageMIMEType(attachedFilePath));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    return callParameters;
  }

  private void updateTotalTokens(Message message) {
    int userPromptTokens = EncodingManager.getInstance().countTokens(message.getPrompt());
    int conversationTokens = EncodingManager.getInstance().countConversationTokens(conversation);
    totalTokensPanel.updateConversationTokens(conversationTokens + userPromptTokens);
  }

  private ResponsePanel createResponsePanel(Message message, ConversationType conversationType) {
    return new ResponsePanel()
        .withReloadAction(() -> reloadMessage(message, conversation, conversationType))
        .withDeleteAction(() -> removeMessage(message.getId(), conversation))
        .addContent(new ChatMessageResponseBody(project, true, this));
  }

  private void reloadMessage(
      Message message,
      Conversation conversation,
      ConversationType conversationType) {
    ResponsePanel responsePanel = null;
    try {
      responsePanel = toolWindowScrollablePanel.getMessageResponsePanel(message.getId());
      ((ChatMessageResponseBody) responsePanel.getContent()).clear();
      toolWindowScrollablePanel.update();
    } catch (Exception e) {
      throw new RuntimeException("Could not delete the existing message component", e);
    } finally {
      LOG.debug("Reloading message: " + message.getId());

      if (responsePanel != null) {
        message.setResponse("");
        conversationService.saveMessage(conversation, message);
        call(new CallParameters(conversation, conversationType, message, true), responsePanel);
      }

      totalTokensPanel.updateConversationTokens(conversation);

    }
  }

  private void removeMessage(UUID messageId, Conversation conversation) {
    toolWindowScrollablePanel.removeMessage(messageId);
    conversation.removeMessage(messageId);
    conversationService.saveConversation(conversation);
    totalTokensPanel.updateConversationTokens(conversation);

    if (conversation.getMessages().isEmpty()) {
      displayLandingView();
    }
  }

  private void clearWindow() {
    toolWindowScrollablePanel.clearAll();
    totalTokensPanel.updateConversationTokens(conversation);
  }

  private void call(CallParameters callParameters, ResponsePanel responsePanel) {
    var responseContainer = (ChatMessageResponseBody) responsePanel.getContent();

    if (!CompletionRequestService.getInstance().isAllowed()) {
      responseContainer.displayMissingCredential();
      return;
    }

    var requestHandler = new CompletionRequestHandler(
        new ToolWindowCompletionResponseEventListener(
            conversationService,
            responsePanel,
            totalTokensPanel,
            userPromptTextArea) {
          @Override
          public void handleTokensExceededPolicyAccepted() {
            call(callParameters, responsePanel);
          }
        });
    userPromptTextArea.setRequestHandler(requestHandler);
    userPromptTextArea.setSubmitEnabled(false);

    requestHandler.call(callParameters);
  }

  private void handleSubmit(String text) {
    var message = new Message(text);
    var editor = CodeEditorHelper.fetchSelectedEditor(project);
    if (editor != null) {
      var selectionModel = editor.getSelectionModel();
      var selectedText = selectionModel.getSelectedText();
      if (selectedText != null && !selectedText.isEmpty()) {
        var fileExtension = FileUtilIntellij.extractFileExtension(editor.getVirtualFile().getName());
        message = new Message(text + format("%n```%s%n%s%n```", fileExtension, selectedText));
        selectionModel.removeSelection();
      }
    }
    message.setUserMessage(text);
    sendMessage(message, ConversationType.DEFAULT);
  }

  private JPanel createUserPromptPanel(ServiceType selectedService) {
    var panel = new JPanel(new BorderLayout());
    panel.setBorder(Borders.compound(
        Borders.customLine(JBColor.border(), 1, 0, 0, 0),
        Borders.empty(8)));
    var contentManager = project.getService(ChatToolWindowContentManager.class);
    panel.add(JBUI.Panels.simplePanel(createUserPromptTextAreaHeader(
        project,
        selectedService,
        () -> {
          ConversationService.getInstance().startConversation();
          contentManager.createNewTabPanel();
        })), BorderLayout.NORTH);
    panel.add(JBUI.Panels.simplePanel(userPromptTextArea), BorderLayout.CENTER);
    return panel;
  }

  private JPanel createUserPromptTextAreaHeader(
      Project project,
      ServiceType selectedService,
      Runnable onModelChange) {
    return JBUI.Panels.simplePanel()
        .withBorder(Borders.emptyBottom(8))
        .andTransparent()
        
        .addToLeft(new ModelComboBoxAction(project, onModelChange, selectedService)
            .createCustomComponent(ActionPlaces.UNKNOWN));
  }

  private JComponent getLandingView() {
    return new ChatToolWindowLandingPanel((action, locationOnScreen) -> {
      var editor = CodeEditorHelper.fetchSelectedEditor(project);
      if (editor == null || !editor.getSelectionModel().hasSelection()) {
        OverlayUtil.showWarningBalloon(
            editor == null ? "Unable to locate a selected editor"
                : "Please select a target code before proceeding",
            locationOnScreen);
      }

      var fileExtension = FileUtilIntellij.extractFileExtension(editor.getVirtualFile().getName());
      var message = new Message(action.getPrompt().replace(
          "{{selectedCode}}",
          format("%n```%s%n%s%n```", fileExtension, editor.getSelectionModel().getSelectedText())));
      message.setUserMessage(action.getUserMessage());

      sendMessage(message, ConversationType.DEFAULT);
    });
  }

  private void displayConversation(@NotNull Conversation conversation) {
    clearWindow();
    conversation.getMessages().forEach(message -> {
      var messageResponseBody =
          new ChatMessageResponseBody(project, this).withResponse(message.getResponse());

      messageResponseBody.hideCaret();

      var userMessagePanel = new UserMessagePanel(project, message, this);
      var imageFilePath = message.getImageFilePath();
      if (imageFilePath != null && !imageFilePath.isEmpty()) {
        userMessagePanel.displayImage(imageFilePath);
      }

      var messagePanel = toolWindowScrollablePanel.addMessage(message.getId());
      messagePanel.add(userMessagePanel);
      messagePanel.add(new ResponsePanel()
          .withReloadAction(() -> reloadMessage(message, conversation, ConversationType.DEFAULT))
          .withDeleteAction(() -> removeMessage(message.getId(), conversation))
          .addContent(messageResponseBody));
    });
  }

  private JPanel createRootPanel() {
    var gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weighty = 1;
    gbc.weightx = 1;
    gbc.gridx = 0;
    gbc.gridy = 0;

    var rootPanel = new JPanel(new GridBagLayout());
    rootPanel.add(UIUtil.createScrollPaneWithSmartScroller(toolWindowScrollablePanel), gbc);

    gbc.weighty = 0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridy = 1;
    rootPanel.add(
        createUserPromptPanel(GeneralSettings.getSelectedService()), gbc);
    return rootPanel;
  }
}
