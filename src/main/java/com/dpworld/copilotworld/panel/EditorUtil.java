package com.dpworld.copilotworld.panel;

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.EditorKind;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.fileEditor.impl.FileEditorManagerImpl;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.testFramework.LightVirtualFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EditorUtil {

    public static Editor createEditor(Project project, String fileExtension, String code) {
        String timestamp = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now());
        String fileName = "temp_" + timestamp + fileExtension;
        LightVirtualFile lightVirtualFile = new LightVirtualFile(
                String.format("%s/%s", PathManager.getTempPath(), fileName),
                code
        );
        Document existingDocument = FileDocumentManager.getInstance().getDocument(lightVirtualFile);
        Document document = (existingDocument != null) ? existingDocument : EditorFactory.getInstance().createDocument(code);

        disableHighlighting(project, document);

        return EditorFactory.getInstance().createEditor(
                document,
                project,
                lightVirtualFile,
                true,
                EditorKind.MAIN_EDITOR
        );
    }

    public static void updateEditorDocument(Editor editor, String content) {
        Document document = editor.getDocument();
        Application application = ApplicationManager.getApplication();
        Runnable updateDocumentRunnable = () -> application.runWriteAction(() ->
                WriteCommandAction.runWriteCommandAction(editor.getProject(), () -> {
                    document.replaceString(0, document.getTextLength(), content);
                    editor.getComponent().repaint();
                    editor.getComponent().revalidate();
                })
        );

        if (application.isUnitTestMode()) {
            application.invokeAndWait(updateDocumentRunnable);
        } else {
            application.invokeLater(updateDocumentRunnable);
        }
    }

    public static boolean hasSelection(Editor editor) {
        return editor != null && editor.getSelectionModel().hasSelection();
    }

    public static Editor getSelectedEditor(Project project) {
        FileEditorManager editorManager = FileEditorManager.getInstance(project);
        return (editorManager != null) ? editorManager.getSelectedTextEditor() : null;
    }

    public static String getSelectedEditorSelectedText(Project project) {
        Editor selectedEditor = getSelectedEditor(project);
        return (selectedEditor != null) ? selectedEditor.getSelectionModel().getSelectedText() : null;
    }

    public static boolean isSelectedEditor(Editor editor) {
        Project project = editor.getProject();
        if (project != null && !project.isDisposed()) {
            FileEditorManager editorManager = FileEditorManager.getInstance(project);
            if (editorManager instanceof FileEditorManagerImpl) {
                return editor.equals(((FileEditorManagerImpl) editorManager).getSelectedTextEditor(true));
            }
            TextEditor current = (editorManager.getSelectedEditor() instanceof TextEditor) ?
                    (TextEditor) editorManager.getSelectedEditor() : null;
            return current != null && editor.equals(current.getEditor());
        }
        return false;
    }

    public static boolean isMainEditorTextSelected(Project project) {
        return hasSelection(getSelectedEditor(project));
    }

    public static void replaceMainEditorSelection(Project project, String text) {
        Application application = ApplicationManager.getApplication();
        application.invokeLater(() -> application.runWriteAction(() ->
                WriteCommandAction.runWriteCommandAction(project, () -> {
                    Editor editor = getSelectedEditor(project);
                    if (editor != null) {
                        int startOffset = editor.getSelectionModel().getSelectionStart();
                        int endOffset = editor.getSelectionModel().getSelectionEnd();
                        Document document = editor.getDocument();
                        document.replaceString(startOffset, endOffset, text);

                        if (ConfigurationSettings.getCurrentState().isAutoFormattingEnabled()) {
                            reformatDocument(project, document, startOffset, endOffset);
                        }

                        editor.getContentComponent().requestFocus();
                        editor.getSelectionModel().removeSelection();
                    }
                })
        ));
    }

    public static void reformatDocument(Project project, Document document, int startOffset, int endOffset) {
        PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(project);
        psiDocumentManager.commitDocument(document);
        var psiFile = psiDocumentManager.getPsiFile(document);
        if (psiFile != null) {
            CodeStyleManager.getInstance(project).reformatText(psiFile, startOffset, endOffset);
        }
    }

    public static void disableHighlighting(Project project, Document document) {
        var psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
        if (psiFile != null) {
            DaemonCodeAnalyzer.getInstance(project).setHighlightingEnabled(psiFile, false);
        }
    }
}
