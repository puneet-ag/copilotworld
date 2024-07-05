package com.dpworld.copilotworld.configuration;


import com.dpworld.copilotworld.completion.CompletionRequestProvider;
import com.dpworld.copilotworld.util.EditorActionsUtil;

import java.util.Map;
import java.util.Objects;


public class ConfigurationState {

  private String systemPrompt = CompletionRequestProvider.COMPLETION_SYSTEM_PROMPT;
  private String commitMessagePrompt = CompletionRequestProvider.GENERATE_COMMIT_MESSAGE_SYSTEM_PROMPT;
  private int maxTokens = 1000;
  private double temperature = 0.1;
  private boolean checkForPluginUpdates = true;
  private boolean checkForNewScreenshots = false;
  private boolean createNewChatOnEachAction;
  private boolean ignoreGitCommitTokenLimit;
  private boolean methodNameGenerationEnabled = true;
  private boolean captureCompileErrors = true;
  private boolean autoFormattingEnabled = true;
  private boolean autocompletionPostProcessingEnabled = false;
  private Map<String, String> tableData = EditorActionsUtil.DEFAULT_ACTIONS;

  public String getSystemPrompt() {
    return systemPrompt;
  }

  public String getCommitMessagePrompt() {
    return commitMessagePrompt;
  }

  public void setSystemPrompt(String systemPrompt) {
    this.systemPrompt = systemPrompt;
  }

  public void setCommitMessagePrompt(String commitMessagePrompt) {
    this.commitMessagePrompt = commitMessagePrompt;
  }

  public int getMaxTokens() {
    return maxTokens;
  }

  public void setMaxTokens(int maxTokens) {
    this.maxTokens = maxTokens;
  }

  public double getTemperature() {
    return temperature;
  }

  public void setTemperature(double temperature) {
    this.temperature = temperature;
  }

  public boolean isCreateNewChatOnEachAction() {
    return createNewChatOnEachAction;
  }

  public void setCreateNewChatOnEachAction(boolean createNewChatOnEachAction) {
    this.createNewChatOnEachAction = createNewChatOnEachAction;
  }

  public boolean isCheckForNewScreenshots() {
    return checkForNewScreenshots;
  }

  public void setCheckForNewScreenshots(boolean checkForNewScreenshots) {
    this.checkForNewScreenshots = checkForNewScreenshots;
  }

  public Map<String, String> getTableData() {
    return tableData;
  }

  public void setTableData(Map<String, String> tableData) {
    this.tableData = tableData;
  }

  public boolean isCheckForPluginUpdates() {
    return checkForPluginUpdates;
  }

  public void setCheckForPluginUpdates(boolean checkForPluginUpdates) {
    this.checkForPluginUpdates = checkForPluginUpdates;
  }

  public boolean isIgnoreGitCommitTokenLimit() {
    return ignoreGitCommitTokenLimit;
  }

  public void setIgnoreGitCommitTokenLimit(boolean ignoreGitCommitTokenLimit) {
    this.ignoreGitCommitTokenLimit = ignoreGitCommitTokenLimit;
  }

  public boolean isMethodNameGenerationEnabled() {
    return methodNameGenerationEnabled;
  }

  public void setMethodNameGenerationEnabled(boolean methodNameGenerationEnabled) {
    this.methodNameGenerationEnabled = methodNameGenerationEnabled;
  }

  public boolean isCaptureCompileErrors() {
    return captureCompileErrors;
  }

  public void setCaptureCompileErrors(boolean captureCompileErrors) {
    this.captureCompileErrors = captureCompileErrors;
  }

  public boolean isAutoFormattingEnabled() {
    return autoFormattingEnabled;
  }

  public void setAutoFormattingEnabled(boolean autoFormattingEnabled) {
    this.autoFormattingEnabled = autoFormattingEnabled;
  }

  public boolean isAutocompletionPostProcessingEnabled() {
    return autocompletionPostProcessingEnabled;
  }

  public void setAutocompletionPostProcessingEnabled(boolean autocompletionPostProcessingEnabled) {
    this.autocompletionPostProcessingEnabled = autocompletionPostProcessingEnabled;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ConfigurationState that)) {
      return false;
    }
    return maxTokens == that.maxTokens
        && Double.compare(temperature, that.temperature) == 0
        && checkForPluginUpdates == that.checkForPluginUpdates
        && checkForNewScreenshots == that.checkForNewScreenshots
        && createNewChatOnEachAction == that.createNewChatOnEachAction
        && ignoreGitCommitTokenLimit == that.ignoreGitCommitTokenLimit
        && methodNameGenerationEnabled == that.methodNameGenerationEnabled
        && captureCompileErrors == that.captureCompileErrors
        && autoFormattingEnabled == that.autoFormattingEnabled
        && autocompletionPostProcessingEnabled == that.autocompletionPostProcessingEnabled
        && Objects.equals(systemPrompt, that.systemPrompt)
        && Objects.equals(commitMessagePrompt, that.commitMessagePrompt)
        && Objects.equals(tableData, that.tableData);
  }

  @Override
  public int hashCode() {
    return Objects.hash(systemPrompt, commitMessagePrompt, maxTokens, temperature,
        checkForPluginUpdates, checkForNewScreenshots, createNewChatOnEachAction,
        ignoreGitCommitTokenLimit, methodNameGenerationEnabled, captureCompileErrors,
        autoFormattingEnabled, autocompletionPostProcessingEnabled, tableData);
  }
}
