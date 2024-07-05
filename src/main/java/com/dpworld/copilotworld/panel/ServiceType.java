package com.dpworld.copilotworld.panel;

import com.dpworld.copilotworld.avatar.AvatarBundle;

import java.util.HashMap;
import java.util.Map;

public enum ServiceType {
  OLLAMA("OLLAMA", "service.ollama.title", "ollama.chat.completion"),
  AVATAR("AVATAR", "service.avatar.title", "avatar.chat.completion");


  private final String code;
  private final String label;
  private final String completionCode;

  private static final Map<String, ServiceType> CLIENT_CODE_MAP = new HashMap<>();

  static {
    for (ServiceType type : values()) {
      CLIENT_CODE_MAP.put(type.getCompletionCode(), type);
    }
  }

  ServiceType(String code, String messageKey, String completionCode) {
    this.code = code;
    this.label = AvatarBundle.get(messageKey);
    this.completionCode = completionCode;
  }

  public String getCode() {
    return code;
  }

  public String getLabel() {
    return label;
  }

  public String getCompletionCode() {
    return completionCode;
  }

  @Override
  public String toString() {
    return label;
  }

  public static ServiceType fromClientCode(String clientCode) {
    ServiceType serviceType = CLIENT_CODE_MAP.get(clientCode);
    if (serviceType == null) {
      throw new RuntimeException("Provided client code '" + clientCode + "' is not supported");
    }
    return serviceType;
  }
}
