package com.dpworld.copilotworld.panel

import com.dpworld.copilotworld.completion.CompletionEventListener
import com.dpworld.copilotworld.model.OllamaSettings
import com.dpworld.copilotworld.panel.converted.*
import com.dpworld.copilotworld.panel.converted.OverlayUtil.showNotification
import com.intellij.codeInsight.inline.completion.*
import com.intellij.codeInsight.inline.completion.elements.InlineCompletionGrayTextElement
import com.intellij.notification.NotificationType
import com.intellij.openapi.application.EDT
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.util.TextRange
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.sse.EventSource
import java.util.concurrent.atomic.AtomicReference

class VisionInlineCompletionProvider : InlineCompletionProvider {
    companion object {
        private val logger = thisLogger()
    }

    private val currentCall = AtomicReference<EventSource>(null)

    override val id: InlineCompletionProviderID
        get() = InlineCompletionProviderID("VisionInlineCompletionProvider")

    override suspend fun getSuggestion(request: InlineCompletionRequest): InlineCompletionSuggestion {
        val project = request.editor.project
        if (project == null) {
            logger.error("Could not find project")
            return InlineCompletionSuggestion.empty()
        }

        return InlineCompletionSuggestion.Default(channelFlow {
            val infillRequest = withContext(Dispatchers.EDT) {
                InfillRequestDetails.fromInlineCompletionRequest(request)
            }
            val (prefix, suffix) = withContext(Dispatchers.EDT) {
                val caretOffset = request.editor.caretModel.offset
                val prefix =
                    request.document.getText(TextRange(0, caretOffset))
                val suffix =
                    request.document.getText(
                        TextRange(
                            caretOffset,
                            request.document.textLength
                        )
                    )
                Pair(prefix, suffix)
            }

            currentCall.set(
                project.service<CodeCompletionService>().getCodeCompletionAsync(
                    infillRequest,
                    CodeCompletionEventListener {
                        val settings = service<ConfigurationSettings>().state
                        try {
                            var inlineText = it.toString()
                            if (settings.isAutocompletionPostProcessingEnabled) {
                                inlineText = CodeCompletionParserFactory
                                    .getParserForFileExtension(request.file.virtualFile.extension)
                                    .parse(
                                        prefix,
                                        suffix,
                                        inlineText
                                    )
                            }

                            request.editor.putUserData(VisionKeys.PREVIOUS_INLAY_TEXT, inlineText)
                            launch {
                                try {
                                    trySend(InlineCompletionGrayTextElement(inlineText))
                                } catch (e: Exception) {
                                    logger.error("Failed to send inline completion suggestion", e)
                                }
                            }
                        } catch (t: Throwable) {
                            logger.error(t)
                            settings.isAutocompletionPostProcessingEnabled = false
                        }
                    }
                )
            )
            awaitClose { cancelCurrentCall() }
        })
    }

    override fun isEnabled(event: InlineCompletionEvent): Boolean {
        val selectedService = GeneralSettings.getSelectedService()
        val codeCompletionsEnabled = when (selectedService) {
            ServiceType.VISION -> service<VisionServiceSettings>().state.codeCompletionSettings.isCodeCompletionsEnabled
            ServiceType.OLLAMA -> service<OllamaSettings>().state.isCodeCompletionsEnabled
            null -> false
        }
        return event is InlineCompletionEvent.DocumentChange && codeCompletionsEnabled
    }

    private fun cancelCurrentCall() {
        currentCall.getAndSet(null)?.cancel()
    }

    class CodeCompletionEventListener(
        private val completed: (StringBuilder) -> Unit
    ) : CompletionEventListener<String> {

        override fun onComplete(messageBuilder: StringBuilder) {
            completed(messageBuilder)
        }

        override fun onCancelled(messageBuilder: StringBuilder) {
            completed(messageBuilder)
        }

        override fun onError(error: ErrorDetails, ex: Throwable) {
            if (ex.message == null || (ex.message != null && ex.message != "Canceled")) {
                showNotification(error.message, NotificationType.ERROR)
                logger.error(error.message, ex)
            }
        }
    }
}