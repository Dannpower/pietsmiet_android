package de.pscom.pietsmiet.model;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

public class YoutubeThumbnails {
    @SerializedName("default")
    private YoutubeThumbnail _default;

    private YoutubeThumbnail medium;
    private Map<String, Object> additionalProperties = new HashMap<>();

    /**
     * @return The _default
     */
    public YoutubeThumbnail getDefault() {
        return _default;
    }

    /**
     * @param _default The default
     */
    public void setDefault(YoutubeThumbnail _default) {
        this._default = _default;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
