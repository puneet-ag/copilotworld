package panel.converted;

import advanced.AdvancedSettings;
import com.intellij.openapi.application.ApplicationManager;

import model.OllamaSettings;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import ollama.OllamaClient;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;


public class CompletionClientProvider {


  public static OllamaClient getOllamaClient() {
    var host = ApplicationManager.getApplication()
        .getService(OllamaSettings.class)
        .getState()
        .getHost();
    var builder = new OllamaClient.Builder()
        .setHost(host);

    return builder.build(getDefaultClientBuilder());
  }

  public static OkHttpClient.Builder getDefaultClientBuilder() {
    OkHttpClient.Builder builder = new OkHttpClient.Builder();
    var advancedSettings = AdvancedSettings.getCurrentState();
    var proxyHost = advancedSettings.getProxyHost();
    var proxyPort = advancedSettings.getProxyPort();
    if (!proxyHost.isEmpty() && proxyPort != 0) {
      builder.proxy(
          new Proxy(advancedSettings.getProxyType(), new InetSocketAddress(proxyHost, proxyPort)));
      if (advancedSettings.isProxyAuthSelected()) {
        builder.proxyAuthenticator((route, response) ->
            response.request()
                .newBuilder()
                .header("Proxy-Authorization", Credentials.basic(
                    advancedSettings.getProxyUsername(),
                    advancedSettings.getProxyPassword()))
                .build());
      }
    }

    return builder
        .connectTimeout(advancedSettings.getConnectTimeout(), TimeUnit.SECONDS)
        .readTimeout(advancedSettings.getReadTimeout(), TimeUnit.SECONDS);
  }
}
