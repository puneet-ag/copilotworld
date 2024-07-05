package com.dpworld.copilotworld.util;

import com.dpworld.copilotworld.configuration.ConfigurationSettings;
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
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.testFramework.LightVirtualFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CodeEditorHelper {

    public static Editor initializeEditor(Project project, String fileExtension, String code) {
        String timestamp = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now());
        String fileName = "temp_" + timestamp + fileExtension;
        LightVirtualFile lightVirtualFile = new LightVirtualFile(
                String.format("%s/%s", PathManager.getTempPath(), fileName),
                code
        );
        Document existingDoc = FileDocumentManager.getInstance().getDocument(lightVirtualFile);
        Document document = (existingDoc != null) ? existingDoc : EditorFactory.getInstance().createDocument(code);

        disableSyntaxHighlighting(project, document);

        return EditorFactory.getInstance().createEditor(
                document,
                project,
                lightVirtualFile,
                true,
                EditorKind.MAIN_EDITOR
        );
    }

    public static void modifyEditorDocument(Editor editor, String content) {
        Document doc = editor.getDocument();
        Application application = ApplicationManager.getApplication();
        Runnable updateDocumentRunnable = () -> application.runWriteAction(() ->
                WriteCommandAction.runWriteCommandAction(editor.getProject(), () -> {
                    doc.replaceString(0, doc.getTextLength(), content);
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

    public static boolean editorHasSelection(Editor editor) {
        return editor != null && editor.getSelectionModel() .hasSelection();
    }

    public static Editor fetchSelectedEditor(Project project) {
        FileEditorManager editorManager = FileEditorManager.getInstance(project);
        return (editorManager != null) ? editorManager.getSelectedTextEditor() : null;
    }

    public static String fetchSelectedText(Project project) {
        Editor activeEditor = fetchSelectedEditor(project);
        return (activeEditor != null) ? activeEditor.getSelectionModel().getSelectedText() : null;
    }

    public static boolean isSelectedTextInMainEditor(Project project) {
        return editorHasSelection(fetchSelectedEditor(project));
    }

    public static void replaceSelectionInMainEditor(Project project, String replacementText) {
        Application app = ApplicationManager.getApplication();
        app.invokeLater(() -> app.runWriteAction(() ->
                WriteCommandAction.runWriteCommandAction(project, () -> {
                    Editor editor = fetchSelectedEditor(project);
                    if (editor != null) {
                        int start = editor.getSelectionModel().getSelectionStart();
                        int end = editor.getSelectionModel().getSelectionEnd();
                        Document doc = editor.getDocument();
                        doc.replaceString(start, end, replacementText);

                        if (ConfigurationSettings.getCurrentState().isAutoFormattingEnabled()) {
                            formatDocument(project, doc, start, end);
                        }

                        editor.getContentComponent().requestFocus();
                        editor.getSelectionModel().removeSelection();
                    }
                })
        ));
    }

    public static void formatDocument(Project project, Document document, int start, int end) {
        PsiDocumentManager psiDocManager = PsiDocumentManager.getInstance(project);
        psiDocManager.commitDocument(document);
        var psiFile = psiDocManager.getPsiFile(document);
        if (psiFile != null) {
            CodeStyleManager.getInstance(project).reformatText(psiFile, start, end);
        }
    }

    public static void disableSyntaxHighlighting(Project project, Document document) {
        var psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
        if (psiFile != null) {
            DaemonCodeAnalyzer.getInstance(project).setHighlightingEnabled(psiFile, false);
        }
    }
}
