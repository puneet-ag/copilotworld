package com.dpworld.copilotworld.panel;


import com.intellij.util.messages.Topic;

public interface ProviderChangeNotifier {

    void providerChanged(ServiceType provider);

    
    Topic<ProviderChangeNotifier> PROVIDER_CHANGE_TOPIC = Topic.create("providerChange", ProviderChangeNotifier.class);
}

