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

public abstract class DocumentCheckboxTree extends CheckboxTree {

  public DocumentCheckboxTree(DocumentCheckboxTreeCellRenderer cellRenderer, CheckedTreeNode rootNode) {
    super(cellRenderer, rootNode);
  }
  public abstract List<ReferencedFile> fetchReferencedFiles();

  protected static void renderDocumentDetails(
      ColoredTreeCellRenderer coloredTreeCellRenderer,
      @NotNull VirtualFile virtualDocument) {
    var fileType = FileTypeManager.getInstance().getFileTypeByFile(virtualDocument);
    coloredTreeCellRenderer.setIcon(fileType.getIcon());
    coloredTreeCellRenderer.append(virtualDocument.getName());
    coloredTreeCellRenderer.append(" - " + FileUtilIntellij.formatFileSize(virtualDocument.getLength()));
  }
}
