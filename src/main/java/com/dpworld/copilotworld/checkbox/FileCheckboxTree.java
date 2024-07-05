package com.dpworld.copilotworld.checkbox;

import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.CheckboxTree;
import com.intellij.ui.CheckedTreeNode;
import com.intellij.ui.ColoredTreeCellRenderer;

import org.jetbrains.annotations.NotNull;
import com.dpworld.copilotworld.util.FileUtilIntellij;
import com.dpworld.copilotworld.panel.ReferencedFile;

import java.util.List;

public abstract class FileCheckboxTree extends CheckboxTree {

  public FileCheckboxTree(FileCheckboxTreeCellRenderer cellRenderer, CheckedTreeNode node) {
    super(cellRenderer, node);
  }

  public abstract List<ReferencedFile> getReferencedFiles();

  protected static void updateFilePresentation(
      ColoredTreeCellRenderer textRenderer,
      @NotNull VirtualFile virtualFile) {
    var fileType = FileTypeManager.getInstance().getFileTypeByFile(virtualFile);
    textRenderer.setIcon(fileType.getIcon());
    textRenderer.append(virtualFile.getName());
    textRenderer.append(" - " + FileUtilIntellij.formatFileSize(virtualFile.getLength()));
  }
}
