package com.dpworld.copilotworld.llmServer.completion.response;

public class OllamaEmbeddingResponse {

  private double[] embedding;

  public double[] getEmbedding() {
    return embedding;
  }

  public void setEmbedding(double[] embedding) {
    this.embedding = embedding;
  }
}
