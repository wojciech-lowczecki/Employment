package pl.plh.app.employment.domain;

import com.fasterxml.jackson.annotation.JsonValue;
import com.google.gson.annotations.SerializedName;

public enum GenderDto {
    // Annotation @JsonValue is sufficient for normal data conversion
    // and @SerializedName is applied for serialization with
    // com.google.gson.Gson.toJson() (convenient in tests)
    @SerializedName("f")
    FEMALE("f"),

    @SerializedName("m")
    MALE("m");

    private final String shortcut;

    GenderDto(String shortcut) {
        this.shortcut = shortcut;
    }

    @JsonValue
    public String getShortcut() {
        return shortcut;
    }
}