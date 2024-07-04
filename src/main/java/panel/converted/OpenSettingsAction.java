package panel.converted;

import com.intellij.icons.AllIcons.General;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.options.ShowSettingsUtil;

import org.jetbrains.annotations.NotNull;
import panel.ServiceConfigurable;
import panel.converted.CodeGPTBundle;

public class OpenSettingsAction extends AnAction {

  public OpenSettingsAction() {
    super(CodeGPTBundle.get("action.openSettings.title"),
        CodeGPTBundle.get("action.openSettings.description"),
        General.Settings);
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    ShowSettingsUtil.getInstance().showSettingsDialog(e.getProject(), ServiceConfigurable.class);
  }
}
