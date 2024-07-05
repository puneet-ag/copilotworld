package com.dpworld.copilotworld.panel;

import com.dpworld.copilotworld.activity.Message;
import com.intellij.compiler.CompilerMessageImpl;
import com.intellij.notification.NotificationAction;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.compiler.CompilationStatusListener;
import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompilerMessage;
import com.intellij.openapi.compiler.CompilerMessageCategory;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;

public class ProjectCompilationStatusListener implements CompilationStatusListener {

  private final Project project;

  public ProjectCompilationStatusListener(Project project) {
    this.project = project;
  }

  @Override
  public void compilationFinished(
      boolean aborted,
      int errors,
      int warnings,
      @NotNull CompileContext compileContext) {
    var success = !ConfigurationSettings.getCurrentState().isCaptureCompileErrors()
        || (!aborted && errors == 0 && warnings == 0);
    if (success) {
      return;
    }
    if (errors > 0) {
      OverlayUtil.getDefaultNotification(
              AvatarBundle.get("notification.compilationError.description"),
              NotificationType.INFORMATION)
          .addAction(NotificationAction.createSimpleExpiring(
              AvatarBundle.get("notification.compilationError.okLabel"),
              () -> project.getService(ChatToolWindowContentManager.class)
                  .sendMessage(getMultiFileMessage(compileContext), ConversationType.FIX_COMPILE_ERRORS)))
          .addAction(NotificationAction.createSimpleExpiring(
              AvatarBundle.get("shared.notification.doNotShowAgain"),
              () -> ConfigurationSettings.getCurrentState().setCaptureCompileErrors(false)))
          .notify(project);
    }
  }

  private Message getMultiFileMessage(CompileContext compileContext) {
    var errorMapping = getErrorMapping(compileContext);
    var prompt = errorMapping.values().stream()
        .flatMap(Collection::stream)
        .collect(joining("\n\n"));

    var message = new Message("Fix the following compile errors:\n\n" + prompt);
    message.setReferencedFilePaths(errorMapping.keySet().stream()
        .map(ReferencedFile::getFilePath)
        .toList());
    message.setUserMessage(message.getPrompt());
    message.setPrompt(CompletionRequestProvider.getPromptWithContext(
        new ArrayList<>(errorMapping.keySet()),
        prompt));
    return message;
  }

  private HashMap<ReferencedFile, List<String>> getErrorMapping(CompileContext compileContext) {
    var errorMapping = new HashMap<ReferencedFile, List<String>>();
    for (var compilerMessage : compileContext.getMessages(CompilerMessageCategory.ERROR)) {
      var key = new ReferencedFile(new File(compilerMessage.getVirtualFile().getPath()));
      var prevValue = errorMapping.get(key);
      if (prevValue == null) {
        prevValue = new ArrayList<>();
      }
      prevValue.add(getCompilerErrorDetails(compilerMessage));
      errorMapping.put(key, prevValue);
    }
    return errorMapping;
  }

  private String getCompilerErrorDetails(CompilerMessage compilerMessage) {
    if (compilerMessage instanceof CompilerMessageImpl compilerMessageImpl) {
      return format(
          "%s:%d:%d - `%s`",
          compilerMessage.getVirtualFile().getName(),
          compilerMessageImpl.getLine(),
          compilerMessageImpl.getColumn(),
          compilerMessage.getMessage());
    }
    return format(
        "%s - `%s`",
        compilerMessage.getVirtualFile().getName(),
        compilerMessage.getMessage());
  }
}
