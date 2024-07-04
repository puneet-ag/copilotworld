//package actions;
//
//import com.intellij.notification.Notification;
//import com.intellij.notification.NotificationType;
//import com.intellij.notification.Notifications;
//import com.intellij.openapi.actionSystem.ActionUpdateThread;
//import com.intellij.openapi.actionSystem.AnAction;
//import com.intellij.openapi.actionSystem.AnActionEvent;
//import com.intellij.openapi.application.ApplicationManager;
//import com.intellij.openapi.command.WriteCommandAction;
//import com.intellij.openapi.project.Project;
//import com.intellij.openapi.vcs.FilePath;
//import com.intellij.openapi.vcs.VcsDataKeys;
//import com.intellij.openapi.vcs.changes.Change;
//import com.intellij.openapi.vfs.VirtualFile;
//import com.intellij.vcs.commit.CommitWorkflowUi;
//import okhttp3.sse.EventSource;
//import org.jetbrains.annotations.NotNull;
//import panel.converted.*;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.util.*;
//
//import static com.intellij.openapi.ui.Messages.OK;
//import static java.util.stream.Collectors.joining;
//
//public class GenerateGitCommitMessageAction extends AnAction {
//
//  public static final int MAX_TOKEN_COUNT_WARNING = 4096;
//  private final EncodingManager encodingManager;
//
//  public GenerateGitCommitMessageAction() {
//    super(
//        VisionBundle.get("action.generateCommitMessage.title"),
//        VisionBundle.get("action.generateCommitMessage.description"),
//        Icons.Sparkle);
//    encodingManager = EncodingManager.getInstance();
//  }
//
//  @Override
//  public void update(@NotNull AnActionEvent event) {
//    var commitWorkflowUi = event.getData(VcsDataKeys.COMMIT_WORKFLOW_UI);
//    if (commitWorkflowUi == null) {
//      event.getPresentation().setVisible(false);
//      return;
//    }
//
//    var callAllowed = CompletionRequestService.isRequestAllowed();
//    event.getPresentation().setEnabled(callAllowed
//        && new CommitWorkflowChanges(commitWorkflowUi).isFilesSelected());
//    event.getPresentation().setText(VisionBundle.get(callAllowed
//        ? "action.generateCommitMessage.title"
//        : "action.generateCommitMessage.missingCredentials"));
//  }
//
//  @Override
//  public void actionPerformed(@NotNull AnActionEvent event) {
//    var project = event.getProject();
//    if (project == null || project.getBasePath() == null) {
//      return;
//    }
//
//    var gitDiff = getGitDiff(event, project);
//    var tokenCount = encodingManager.countTokens(gitDiff);
//    if (tokenCount > MAX_TOKEN_COUNT_WARNING
//        && OverlayUtil.showTokenSoftLimitWarningDialog(tokenCount) != OK) {
//      return;
//    }
//
//    var commitWorkflowUi = event.getData(VcsDataKeys.COMMIT_WORKFLOW_UI);
//    if (commitWorkflowUi != null) {
//      CompletionRequestService.getInstance()
//          .generateCommitMessageAsync(
//              project.getService(CommitMessageTemplate.class).getSystemPrompt(),
//              gitDiff,
//              getEventListener(project, commitWorkflowUi));
//    }
//  }
//
//  @Override
//  public @NotNull ActionUpdateThread getActionUpdateThread() {
//    return ActionUpdateThread.EDT;
//  }
//
//  private CompletionEventListener<String> getEventListener(
//      Project project,
//      CommitWorkflowUi commitWorkflowUi) {
//    return new CompletionEventListener<>() {
//      private final StringBuilder messageBuilder = new StringBuilder();
//
//      @Override
//      public void onMessage(String message, EventSource eventSource) {
//        messageBuilder.append(message);
//        var application = ApplicationManager.getApplication();
//        application.invokeLater(() ->
//            application.runWriteAction(() ->
//                WriteCommandAction.runWriteCommandAction(project, () ->
//                    commitWorkflowUi.getCommitMessageUi().setText(messageBuilder.toString()))));
//      }
//
//      @Override
//      public void onError(ErrorDetails error, Throwable ex) {
//        Notifications.Bus.notify(new Notification(
//            "Vision Notification Group",
//            "Vision",
//            error.getMessage(),
//            NotificationType.ERROR));
//      }
//    };
//  }
//
//  private String getGitDiff(AnActionEvent event, Project project) {
//    var commitWorkflowUi = Optional.ofNullable(event.getData(VcsDataKeys.COMMIT_WORKFLOW_UI))
//        .orElseThrow(() -> new IllegalStateException("Could not retrieve commit workflow ui."));
//    var changes = new CommitWorkflowChanges(commitWorkflowUi);
//    var projectBasePath = project.getBasePath();
//    var gitDiff = getGitDiff(projectBasePath, changes.getIncludedVersionedFilePaths(), false);
//    var stagedGitDiff = getGitDiff(projectBasePath, changes.getIncludedVersionedFilePaths(), true);
//    var newFilesContent =
//        getNewFilesDiff(projectBasePath, changes.getIncludedUnversionedFilePaths());
//
//    return Map.of(
//            "Git diff", gitDiff,
//            "Staged git diff", stagedGitDiff,
//            "New files", newFilesContent)
//        .entrySet().stream()
//        .filter(entry -> !entry.getValue().isEmpty())
//        .map(entry -> "%s:%n%s".formatted(entry.getKey(), entry.getValue()))
//        .collect(joining("\n\n"));
//  }
//
//  private String getGitDiff(String projectPath, List<String> filePaths, boolean cached) {
//    if (filePaths.isEmpty()) {
//      return "";
//    }
//
//    var process = createGitDiffProcess(projectPath, filePaths, cached);
//    return new BufferedReader(new InputStreamReader(process.getInputStream()))
//        .lines()
//        .collect(joining("\n"));
//  }
//
//  private String getNewFilesDiff(String projectPath, List<String> filePaths) {
//    return filePaths.stream()
//        .map(pathString -> {
//          var filePath = Path.of(pathString);
//          var relativePath = Path.of(projectPath).relativize(filePath);
//          try {
//            return "New file '" + relativePath + "' content:\n" + Files.readString(filePath);
//          } catch (IOException ignored) {
//            return null;
//          }
//        })
//        .filter(Objects::nonNull)
//        .collect(joining("\n"));
//  }
//
//  private Process createGitDiffProcess(String projectPath, List<String> filePaths, boolean cached) {
//    var command = new ArrayList<String>();
//    command.add("git");
//    command.add("diff");
//    if (cached) {
//      command.add("--cached");
//    }
//    command.addAll(filePaths);
//
//    var processBuilder = new ProcessBuilder(command);
//    processBuilder.directory(new File(projectPath));
//    try {
//      return processBuilder.start();
//    } catch (IOException ex) {
//      throw new RuntimeException("Unable to start git diff process", ex);
//    }
//  }
//
//  static class CommitWorkflowChanges {
//
//    private final List<String> includedVersionedFilePaths;
//    private final List<String> includedUnversionedFilePaths;
//
//    CommitWorkflowChanges(CommitWorkflowUi commitWorkflowUi) {
//      includedVersionedFilePaths = commitWorkflowUi.getIncludedChanges().stream()
//          .map(Change::getVirtualFile)
//          .filter(Objects::nonNull)
//          .map(VirtualFile::getPath)
//          .toList();
//      includedUnversionedFilePaths = commitWorkflowUi.getIncludedUnversionedFiles().stream()
//          .map(FilePath::getPath)
//          .toList();
//    }
//
//    public List<String> getIncludedVersionedFilePaths() {
//      return includedVersionedFilePaths;
//    }
//
//    public List<String> getIncludedUnversionedFilePaths() {
//      return includedUnversionedFilePaths;
//    }
//
//    public boolean isFilesSelected() {
//      return !includedVersionedFilePaths.isEmpty() || !includedUnversionedFilePaths.isEmpty();
//    }
//  }
//}
