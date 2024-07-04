package panel;

import com.intellij.codeInsight.inline.completion.*;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.editor.event.DocumentListenerManager;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.util.ui.UIUtil;
import model.OllamaSettings;
import panel.converted.*;

import java.util.concurrent.atomic.AtomicReference;

public class CodeGPTInlineCompletionProvider implements InlineCompletionProvider {

    private static final Logger logger = Logger.getInstance(CodeGPTInlineCompletionProvider.class);

    private final AtomicReference<EditorEx> currentEditor = new AtomicReference<>();

    @Override
    public InlineCompletionProviderID getId() {
        return new InlineCompletionProviderID("CodeGPTInlineCompletionProvider");
    }

    @Override
    public InlineCompletionSession startInlineCompletion(InlineCompletionSession.Context context) {
        Editor editor = context.getEditor();
        Project project = editor.getProject();
        if (project == null) {
            logger.error("Could not find project");
            return InlineCompletionSession.EMPTY;
        }

        return new InlineCompletionSessionBase(context) {
            @Override
            public void start() {
                EditorEx editorEx = (EditorEx) editor;
                currentEditor.set(editorEx);

                Document document = editorEx.getDocument();
                PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
                if (psiFile == null) {
                    cancelInlineCompletion();
                    return;
                }

                TextRange range = context.getRange();
                String prefix = document.getText(new TextRange(0, range.getStartOffset()));
                String suffix = document.getText(new TextRange(range.getEndOffset(), document.getTextLength()));

                CodeGPTService codeGPTService = project.getService(CodeGPTService.class);
                codeGPTService.getCodeCompletionAsync(prefix, suffix, new CodeCompletionListener() {
                    @Override
                    public void onComplete(String result) {
                        UIUtil.invokeLaterIfNeeded(() -> {
                            if (!editorEx.isDisposed()) {
                                InlineCompletionSession session = getSession();
                                session.addInlineElement(new InlineCompletionGrayTextElement(result));
                                session.show();
                            }
                        });
                    }

                    @Override
                    public void onCancel() {
                        logger.warn("Code completion request canceled.");
                    }

                    @Override
                    public void onError(String errorMessage, Throwable exception) {
                        logger.error(errorMessage, exception);
                        OverlayUtil.showNotification(errorMessage, NotificationType.ERROR);
                    }
                });
            }
        };
    }

    @Override
    public boolean isEnabledForContext(InlineCompletionSession.Context context) {
        Editor editor = context.getEditor();
        Project project = editor.getProject();
        if (project == null) {
            return false;
        }

        GeneralSettings generalSettings = GeneralSettings.getInstance();
        ConfigurationSettings configurationSettings = ConfigurationSettings.getInstance();
        ServiceType selectedService = generalSettings.getSelectedService();

        switch (selectedService) {
            case OLLAMA:
                return project.getService(OllamaSettings.class).getState().isCodeCompletionEnabled();
            default:
                return false;
        }
    }

    @Override
    public void dispose() {
        EditorEx editor = currentEditor.getAndSet(null);
        if (editor != null && !editor.isDisposed()) {
            DocumentListenerManager listenerManager = editor.getDocument().getDocumentListener();
            if (listenerManager != null) {
                DocumentListener[] listeners = listenerManager.getListeners();
                for (DocumentListener listener : listeners) {
                    if (listener instanceof UserDataHolderBase) {
                        ((UserDataHolderBase) listener).putUserData(CodeGPTKeys.PREVIOUS_INLAY_TEXT, null);
                    }
                }
            }
        }
    }
}

