package panel.converted;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CodeGPTService {

    private final Project project;
    private final ExecutorService serviceScope = Executors.newCachedThreadPool();

    public CodeGPTService(Project project) {
        this.project = project;
    }


    public void dispose() {
        serviceScope.shutdown();
    }

    // Simulating UserDetails class structure
    static class UserDetails {
        private String fullName;
        private String pricingPlan;

        public String getFullName() {
            return fullName;
        }

        public String getPricingPlan() {
            return pricingPlan;
        }
    }
}

