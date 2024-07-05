package com.dpworld.copilotworld.forms

import com.dpworld.copilotworld.model.InfillPromptTemplate
import com.intellij.util.ui.FormBuilder
import javax.swing.JPanel

class CodeCompletionConfigurationForm(
    codeCompletionsEnabled: Boolean,
    fimTemplate: InfillPromptTemplate?
) {

    fun getForm(): JPanel {
        val formBuilder = FormBuilder.createFormBuilder()
        return formBuilder.panel
    }

}
