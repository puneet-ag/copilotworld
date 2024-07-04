package com.dpworld.copilotworld.panel.converted;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.intellij.util.xmlb.Converter;

public abstract class BaseConverter<T> extends Converter<T> {

  private final TypeReference<T> typeReference;
  private final ObjectMapper objectMapper;

  protected BaseConverter(TypeReference<T> typeReference) {
    this.typeReference = typeReference;
    this.objectMapper = new ObjectMapper()
            .registerModule(new Jdk8Module())
            .registerModule(new JavaTimeModule());
  }

  @Override
  public T fromString(String value) {
    try {
      return objectMapper.readValue(value, typeReference);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Unable to deserialize conversations", e);
    }
  }

  @Override
  public String toString(T value) {
    try {
      return objectMapper.writeValueAsString(value);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Unable to serialize conversations", e);
    }
  }
}
