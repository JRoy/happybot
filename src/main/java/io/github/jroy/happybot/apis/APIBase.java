package io.github.jroy.happybot.apis;

import io.github.jroy.happybot.apis.exceptions.IllegalAPIState;

public abstract class APIBase {

    private String apiName = "NULL";
    private boolean loaded = false;

    public APIBase(String apiName) {
        if (apiName != null) {
            this.apiName = apiName;
        }
    }

    public abstract void loadApi();

    public void loginApi() throws IllegalAPIState {
        if (loaded)
            throw new IllegalAPIState("API is already loaded.");
        loadApi();
        loaded = true;
    }

    public String getApiName() {
        return apiName;
    }

    public boolean isLoaded() {
        return loaded;
    }
}
