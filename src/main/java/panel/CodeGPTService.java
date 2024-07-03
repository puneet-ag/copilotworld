package panel;


import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;

import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.Dispatchers;


@Service
public final class CodeGPTService {

    private final Project project;
    private final CoroutineScope serviceScope = new CoroutineScope(new SupervisorJob() + Dispatchers.Default);

    private CodeGPTService(Project project) {
        this.project = project;
    }

    public static CodeGPTService getInstance(Project project) {
        return ServiceManager.getService(project, CodeGPTService.class);
    }

    public void syncUserDetailsAsync() {
        syncUserDetailsAsync(CredentialsStore.getCredential(CredentialsStore.CredentialKey.CODEGPT_API_KEY));
    }

    public void syncUserDetailsAsync(String apiKey) {
        serviceScope.launch(() -> {
            var userDetails = withContext(Dispatchers.IO, () -> {
                if (apiKey == null || apiKey.isEmpty()) {
                    return null;
                }
                return CompletionClientProvider.getCodeGPTClient().getUserDetails(apiKey);
            });

            if (userDetails != null && userDetails.getPricingPlan() != null) {
                CodeGPTKeys.CODEGPT_USER_DETAILS.set(project, userDetails);
                if (userDetails.getFullName() != null && !userDetails.getFullName().isEmpty()) {
                    GeneralSettings generalSettings = ServiceManager.getService(GeneralSettings.class);
                    generalSettings.getState().setDisplayName(userDetails.getFullName());
                }
            }

            project.getMessageBus()
                    .syncPublisher(CodeGPTUserDetailsNotifier.CODEGPT_USER_DETAILS_TOPIC)
                    .userDetailsObtained(userDetails);
        });
    }
}

