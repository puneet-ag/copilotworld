package panel.converted;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import panel.converted.AvailableModel;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CodeGPTUserDetails {

    private final String fullName;
    private final List<AvailableModel> availableModels;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public CodeGPTUserDetails(
            @JsonProperty("fullName") String fullName,
            @JsonProperty("availableModels") List<AvailableModel> availableModels) {
        this.fullName = fullName;
        this.availableModels = availableModels;
    }

    public String getFullName() {
        return fullName;
    }


    public List<AvailableModel> getAvailableModels() {
        return availableModels;
    }
}