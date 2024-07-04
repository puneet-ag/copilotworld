package panel.converted;


import com.intellij.util.messages.Topic;

public interface ProviderChangeNotifier {

    void providerChanged(ServiceType provider);

    // Equivalent to Kotlin's companion object
    Topic<ProviderChangeNotifier> PROVIDER_CHANGE_TOPIC = Topic.create("providerChange", ProviderChangeNotifier.class);
}

