package com.dpworld.copilotworld.conversation.chat;

import com.dpworld.copilotworld.activity.YouSerpResult;
import com.dpworld.copilotworld.configuration.GeneralSettingsConfigurable;
import com.dpworld.copilotworld.panel.ResponseEditorPanel;
import com.dpworld.copilotworld.panel.StreamParser;
import com.dpworld.copilotworld.panel.StreamResponseType;
import com.dpworld.copilotworld.util.CodeEditorHelper;
import com.dpworld.copilotworld.util.MarkdownProcessor;
import com.dpworld.copilotworld.util.UIUtil;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ui.JBUI;
import com.vladsch.flexmark.ast.FencedCodeBlock;
import com.vladsch.flexmark.parser.Parser;


import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static javax.swing.event.HyperlinkEvent.EventType.ACTIVATED;

public class ChatMessageResponseBody extends JPanel {

  private final Project project;
  private final Disposable parentDisposable;
  private final StreamParser streamParser;
  private final boolean readOnly;
  private ResponseEditorPanel currentlyProcessedEditorPanel;
  private JTextPane currentlyProcessedTextPane;
  private boolean responseReceived;

  public ChatMessageResponseBody(Project project, Disposable parentDisposable) {
    this(project, false, parentDisposable);
  }

  public ChatMessageResponseBody(
      Project project,
      boolean withGhostText,
      Disposable parentDisposable) {
    this(project, withGhostText, false, parentDisposable);
  }

  public ChatMessageResponseBody(
      Project project,
      boolean withGhostText,
      boolean readOnly,
      Disposable parentDisposable) {
    super(new BorderLayout());
    this.project = project;
    this.parentDisposable = parentDisposable;
    this.streamParser = new StreamParser();
    this.readOnly = readOnly;
    setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
    setOpaque(false);

    if (withGhostText) {
      prepareProcessingText(!readOnly);
      currentlyProcessedTextPane.setText(
          "<html><p style=\"margin-top: 4px; margin-bottom: 8px;\">&#8205;</p></html>");
    }
  }

  public ChatMessageResponseBody withResponse(String response) {
    for (var message : MarkdownProcessor.extractCodeBlocks(response)) {
      processResponse(message, message.startsWith("```"), false);
    }

    return this;
  }

  public void update(String partialMessage) {
    for (var item : streamParser.parse(partialMessage)) {
      processResponse(item.response(), StreamResponseType.CODE.equals(item.type()), true);
    }
  }

  public void displayMissingCredential() {
    var message = "API key not provided. Open <a href=\"#\">Settings</a> to set one.";
    currentlyProcessedTextPane.setText(
        format("<html><p style=\"margin-top: 4px; margin-bottom: 8px;\">%s</p></html>", message));
    currentlyProcessedTextPane.addHyperlinkListener(e -> {
      if (e.getEventType() == ACTIVATED) {
        ShowSettingsUtil.getInstance()
            .showSettingsDialog(project, GeneralSettingsConfigurable.class);
      }
    });
    currentlyProcessedTextPane.getCaret().setVisible(false);
  }

  public void displayQuotaExceeded() {
    currentlyProcessedTextPane.setText("<html>"
        + "<p style=\"margin-top: 4px; margin-bottom: 8px;\">"
        + "You exceeded your current quota, please check your plan and billing details, "
        + "or <a href=\"#CHANGE_PROVIDER\">change</a> to a different LLM provider.</p>"
        + "</html>");

    currentlyProcessedTextPane.addHyperlinkListener(e -> {
      if (e.getEventType() == ACTIVATED) {
        ShowSettingsUtil.getInstance()
            .showSettingsDialog(project, GeneralSettingsConfigurable.class);
      }
    });
    hideCaret();
  }

  public void hideCaret() {
    if (currentlyProcessedTextPane != null) {
      currentlyProcessedTextPane.getCaret().setVisible(false);
    }
  }

  public void displayError(String message) {
    var errorText = format(
        "<html><p style=\"margin-top: 4px; margin-bottom: 8px;\">%s</p></html>",
        message);
    if (responseReceived) {
      add(createTextPane(errorText, false));
    } else {
      currentlyProcessedTextPane.setText(errorText);
    }
  }

  public void displaySerpResults(List<YouSerpResult> serpResults) {
    var html = getSearchResultsHtml(serpResults);
    if (responseReceived) {
      add(createTextPane(html, false));
    } else {
      if (currentlyProcessedTextPane == null) {
        prepareProcessingText(false);
      }
      currentlyProcessedTextPane.setText(html);
    }
  }

  public void clear() {
    removeAll();

    streamParser.clear();
    
    prepareProcessingText(true);
    currentlyProcessedTextPane.setText(
        "<html><p style=\"margin-top: 4px; margin-bottom: 8px;\">&#8205;</p></html>");

    repaint();
    revalidate();
  }

  private String getSearchResultsHtml(List<YouSerpResult> serpResults) {
    var titles = serpResults.stream()
        .map(result -> format(
            "<li style=\"margin-bottom: 4px;\"><a href=\"%s\">%s</a></li>",
            result.getUrl(),
            result.getName()))
        .collect(Collectors.joining());
    return format(
        "<html>"
            + "<p><strong>Search results:</strong></p>"
            + "<ol>%s</ol>"
            + "</html>",
        titles);
  }

  private void processResponse(String markdownInput, boolean codeResponse, boolean caretVisible) {
    responseReceived = true;

    if (codeResponse) {
      processCode(markdownInput);
    } else {
      processText(markdownInput, caretVisible);
    }
  }

  private void processCode(String markdownCode) {
    var document = Parser.builder().build().parse(markdownCode);
    var child = document.getChildOfType(FencedCodeBlock.class);
    if (child != null) {
      var codeBlock = ((FencedCodeBlock) child);
      var code = codeBlock.getContentChars().unescape();
      if (!code.isEmpty()) {
        if (currentlyProcessedEditorPanel == null) {
          prepareProcessingCode(code, codeBlock.getInfo().unescape());
        }
        CodeEditorHelper.modifyEditorDocument(currentlyProcessedEditorPanel.getEditor(), code);
      }
    }
  }

  private void processText(String markdownText, boolean caretVisible) {
    if (currentlyProcessedTextPane == null) {
      prepareProcessingText(caretVisible);
    }
    currentlyProcessedTextPane.setText(MarkdownProcessor.markdownToHtml(markdownText));
  }

  private void prepareProcessingText(boolean caretVisible) {
    currentlyProcessedEditorPanel = null;
    currentlyProcessedTextPane = createTextPane("", caretVisible);
    add(currentlyProcessedTextPane);
  }

  private void prepareProcessingCode(String code, String markdownLanguage) {
    hideCaret();
    currentlyProcessedTextPane = null;
    currentlyProcessedEditorPanel =
        new ResponseEditorPanel(project, code, markdownLanguage, readOnly, parentDisposable);
    add(currentlyProcessedEditorPanel);
  }

  private JTextPane createTextPane(String text, boolean caretVisible) {
    var textPane = UIUtil.createTextPane(text, false, event -> {
      if (FileUtil.exists(event.getDescription()) && ACTIVATED.equals(event.getEventType())) {
        VirtualFile file = LocalFileSystem.getInstance().findFileByPath(event.getDescription());
        FileEditorManager.getInstance(project).openFile(Objects.requireNonNull(file), true);
        return;
      }

      UIUtil.handleHyperlinkClicked(event);
    });
    if (caretVisible) {
      textPane.getCaret().setVisible(true);
      textPane.setCaretPosition(textPane.getDocument().getLength());
    }
    textPane.setBorder(JBUI.Borders.empty());
    return textPane;
  }
}
