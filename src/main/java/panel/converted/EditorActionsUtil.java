package panel.converted;

import activity.Message;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.project.Project;

import org.apache.commons.text.CaseUtils;
import panel.converted.*;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

import static java.lang.String.format;

public class EditorActionsUtil {

  public static final Map<String, String> DEFAULT_ACTIONS = new LinkedHashMap<>(Map.of(
      "Find Bugs", "Find bugs and output code with bugs "
          + "fixed in the following code: {{selectedCode}}",
      "Write Tests", "Write Tests for the selected code {{selectedCode}}",
      "Explain", "Explain the selected code {{selectedCode}}",
      "Refactor", "Refactor the selected code {{selectedCode}}",
      "Optimize", "Optimize the selected code {{selectedCode}}"));

  public static final String[][] DEFAULT_ACTIONS_ARRAY = toArray(DEFAULT_ACTIONS);

  public static String[][] toArray(Map<String, String> actionsMap) {
    return actionsMap.entrySet()
        .stream()
        .map(entry -> new String[]{entry.getKey(), entry.getValue()})
        .toArray(String[][]::new);
  }

  public static void refreshActions() {
    AnAction actionGroup =
        ActionManager.getInstance().getAction("action.editor.group.EditorActionGroup");
    if (actionGroup instanceof DefaultActionGroup group) {
      group.removeAll();
      group.add(new AskAction());
      group.add(new CustomPromptAction());
      group.addSeparator();

      var configuredActions = ConfigurationSettings.getCurrentState().getTableData();
      configuredActions.forEach((label, prompt) -> {
        // using label as action description to prevent com.intellij.diagnostic.PluginException
        // https://github.com/carlrobertoh/CodeGPT/issues/95
        var action = new BaseEditorAction(label, label) {
          @Override
          protected void actionPerformed(Project project, Editor editor, String selectedText) {
            var fileExtension = IntellijFileUtil.getFileExtension(editor.getVirtualFile().getName());
            var message = new Message(prompt.replace(
                "{{selectedCode}}",
                format("%n```%s%n%s%n```", fileExtension, selectedText)));
            message.setUserMessage(prompt.replace("{{selectedCode}}", ""));
            var toolWindowContentManager =
                project.getService(ChatToolWindowContentManager.class);
            toolWindowContentManager.getToolWindow().show();

            message.setReferencedFilePaths(
                Stream.ofNullable(project.getUserData(CodeGPTKeys.SELECTED_FILES))
                    .flatMap(Collection::stream)
                    .map(ReferencedFile::getFilePath)
                    .toList());
            toolWindowContentManager.sendMessage(message);
          }
        };
        group.add(action);
      });
      group.addSeparator();
      group.add(new IncludeFilesInContextAction("action.includeFileInContext.title"));
    }
  }

  public static void registerAction(AnAction action) {
    ActionManager actionManager = ActionManager.getInstance();
    var actionId = convertToId(action.getTemplateText());
    if (actionManager.getAction(actionId) == null) {
      actionManager.registerAction(actionId, action, PluginId.getId("ee.carlrobert.chatgpt"));
    }
  }

  public static String convertToId(String label) {
    return "codegpt." + CaseUtils.toCamelCase(label, true);
  }
}
