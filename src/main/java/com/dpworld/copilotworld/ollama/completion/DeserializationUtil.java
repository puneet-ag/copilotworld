package com.dpworld.copilotworld.ollama.completion;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.Response;

import java.io.IOException;

public class DeserializationUtil {

  private DeserializationUtil() {
  }

  public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
      .setSerializationInclusion(JsonInclude.Include.NON_NULL);

  public static <T> T mapResponse(Response response, Class<T> clazz) {
    var body = response.body();
    try {
      return OBJECT_MAPPER.readValue(body.string(), clazz);
    } catch (IOException ex) {
      throw new RuntimeException("Could not deserialize response", ex);
    }
  }
}
