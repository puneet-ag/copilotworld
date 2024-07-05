package com.dpworld.copilotworld.llmServer;

import static java.lang.String.format;
import static com.dpworld.copilotworld.llmServer.completion.DeserializationUtil.OBJECT_MAPPER;
import static com.dpworld.copilotworld.llmServer.completion.InterceptorUtil.REWRITE_X_NDJSON_CONTENT_INTERCEPTOR;

import com.dpworld.copilotworld.llmServer.completion.response.*;
import com.dpworld.copilotworld.panel.ErrorDetails;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import java.util.Map;

import com.dpworld.copilotworld.completion.CompletionEventListener;
import com.dpworld.copilotworld.completion.CompletionEventSourceListener;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSources;
import com.dpworld.copilotworld.llmServer.completion.DeserializationUtil;
import com.dpworld.copilotworld.llmServer.completion.request.LLMChatCompletionRequest;
import com.dpworld.copilotworld.llmServer.completion.request.LLMRequestCompletion;
import com.dpworld.copilotworld.llmServer.completion.request.LLMRequestEmbedding;
import com.dpworld.copilotworld.llmServer.completion.request.LLMPullRequest;

public class LLMClient {

  private static final String BASE_URL = "http://localhost:11434";
  private static final MediaType APPLICATION_JSON = MediaType.parse("application/json");

  private final OkHttpClient httpClient;
  private final String host;
  private final Integer port;
  private final String apiKey;

  protected LLMClient(Builder builder, OkHttpClient.Builder httpClientBuilder) {
    this.httpClient = httpClientBuilder
        .addInterceptor(REWRITE_X_NDJSON_CONTENT_INTERCEPTOR)
        .build();
    this.host = builder.host;
    this.port = builder.port;
    this.apiKey = builder.apiKey;
  }

  private static RequestBody createRequestBody(Object request) throws JsonProcessingException {
    return RequestBody.create(OBJECT_MAPPER.writeValueAsString(request), APPLICATION_JSON);
  }

  public EventSource getCompletionAsync(
      LLMRequestCompletion request,
      CompletionEventListener<String> eventListener) {
    return EventSources.createFactory(httpClient)
        .newEventSource(
            buildPostRequest(request, "/api/generate", true),
            getCompletionEventSourceListener(eventListener));
  }

  public EventSource getChatCompletionAsync(
      LLMChatCompletionRequest request,
      CompletionEventListener<String> eventListener) {
    return EventSources.createFactory(httpClient)
        .newEventSource(
            buildPostRequest(request, "/api/chat", true),
            getChatCompletionEventSourceListener(eventListener));
  }

  public LLMCompletionResponse getCompletion(LLMRequestCompletion request) {
    try (var response = httpClient.newCall(buildPostRequest(request, "/api/generate")).execute()) {
      return DeserializationUtil.mapResponse(response, LLMCompletionResponse.class);
    } catch (IOException e) {
      throw new RuntimeException(
          "Could not get ollama completion for the given request:\n" + request, e);
    }
  }

  public LLMChatCompletionResponse getChatCompletion(LLMChatCompletionRequest request) {
    try (var response = httpClient.newCall(buildPostRequest(request, "/api/chat")).execute()) {
      return DeserializationUtil.mapResponse(response, LLMChatCompletionResponse.class);
    } catch (IOException e) {
      throw new RuntimeException(
          "Could not get ollama chat completion for the given request:\n" + request, e);
    }
  }

  public LLMEmbeddingResponse getEmbedding(LLMRequestEmbedding request) {
    try (var response = httpClient
        .newCall(buildPostRequest(request, "/api/embeddings"))
        .execute()) {
      return DeserializationUtil.mapResponse(response, LLMEmbeddingResponse.class);
    } catch (IOException e) {
      throw new RuntimeException(
          "Could not get ollama embedding for the given request:\n" + request, e);
    }
  }

  public LLMPullResponse pullModel(LLMPullRequest request) {
    try (var response = httpClient.newCall(buildPostRequest(request, "/api/pull")).execute()) {
      return DeserializationUtil.mapResponse(response, LLMPullResponse.class);
    } catch (IOException e) {
      throw new RuntimeException(
          "Could not pull ollama model for the given request:\n" + request, e);
    }
  }

  public EventSource pullModelAsync(
      LLMPullRequest request,
      CompletionEventListener<LLMPullResponse> eventListener) {
    return EventSources.createFactory(httpClient)
        .newEventSource(
            buildPostRequest(request, "/api/pull", true),
            getPullModelEventSourceListener(eventListener));
  }

  public boolean deleteModel(String model) {
    try (var response = httpClient
        .newCall(defaultRequest("/api/delete")
            .delete(createRequestBody(Map.of("name", model)))
            .build())
        .execute()) {
      return response.isSuccessful();
    } catch (IOException e) {
      throw new RuntimeException(
          "Could not delete ollama model for the given model name:\n" + model, e);
    }
  }

  public LLMTagsResponse getModelTags() {
    try (var response = httpClient
        .newCall(defaultRequest("/api/tags").get().build())
        .execute()) {
      return DeserializationUtil.mapResponse(response, LLMTagsResponse.class);
    } catch (IOException e) {
      throw new RuntimeException(
          "Could not get ollama model tags:\n", e);
    }
  }

  public LLMModelInfoResponse getModelInfo(String model) {
    try (var response = httpClient
        .newCall(buildPostRequest(Map.of("name", model), "/api/show"))
        .execute()) {
      return DeserializationUtil.mapResponse(response, LLMModelInfoResponse.class);
    } catch (IOException e) {
      throw new RuntimeException(
          "Could not get ollama model info for the given model name:\n" + model, e);
    }
  }

  private Request buildPostRequest(Object request, String path) {
    return buildPostRequest(request, path, false);
  }

  private Request buildPostRequest(Object request, String path, boolean stream) {
    try {
      return defaultRequest(path, stream)
          .post(createRequestBody(request))
          .build();
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  private Request.Builder defaultRequest(String path) {
    return defaultRequest(path, false);
  }

  private Request.Builder defaultRequest(String path, boolean stream) {
    var baseHost = port == null ? BASE_URL : format("http://localhost:%d", port);
    var builder = new Request.Builder()
        .url((host == null ? baseHost : host) + path)
        .header("Cache-Control", "no-cache")
        .header("Content-Type", "application/json")
        .header("Accept", stream ? "text/event-stream" : "text/json");
    if (apiKey != null) {
      builder.header("Authorization", "Bearer " + apiKey);
    }
    return builder;
  }

  private CompletionEventSourceListener<String> getChatCompletionEventSourceListener(
      CompletionEventListener<String> eventListener
  ) {
    return new CompletionEventSourceListener<>(eventListener) {
      @Override
      protected String getMessage(String data) {
        try {
          return OBJECT_MAPPER.readValue(data, LLMChatCompletionResponse.class).getMessage()
              .getContent();
        } catch (JacksonException e) {
          return "";
        }
      }

      @Override
      protected ErrorDetails getErrorDetails(String error) {
        return new ErrorDetails(error);
      }
    };
  }

  private CompletionEventSourceListener<String> getCompletionEventSourceListener(
      CompletionEventListener<String> eventListener) {
    return new CompletionEventSourceListener<>(eventListener) {
      @Override
      protected String getMessage(String data) {
        try {
          return OBJECT_MAPPER.readValue(data, LLMCompletionResponse.class).getResponse();
        } catch (JacksonException e) {
          return "";
        }
      }

      @Override
      protected ErrorDetails getErrorDetails(String error) {
        return new ErrorDetails(error);
      }
    };
  }

  private CompletionEventSourceListener<LLMPullResponse> getPullModelEventSourceListener(
      CompletionEventListener<LLMPullResponse> eventListener) {
    return new CompletionEventSourceListener<>(eventListener) {
      @Override
      protected LLMPullResponse getMessage(String data) {
        try {
          return OBJECT_MAPPER.readValue(data, LLMPullResponse.class);
        } catch (JacksonException e) {
          return null;
        }
      }

      @Override
      protected ErrorDetails getErrorDetails(String error) {
        return new ErrorDetails(error);
      }
    };
  }

  public static class Builder {

    private String host;
    private Integer port;
    private String apiKey;

    public Builder setHost(String host) {
      this.host = host;
      return this;
    }

    public Builder setPort(Integer port) {
      this.port = port;
      return this;
    }

    public Builder setApiKey(String apiKey) {
      this.apiKey = apiKey;
      return this;
    }

    public LLMClient build(OkHttpClient.Builder builder) {
      return new LLMClient(this, builder);
    }

    public LLMClient build() {
      return build(new OkHttpClient.Builder());
    }
  }
}
