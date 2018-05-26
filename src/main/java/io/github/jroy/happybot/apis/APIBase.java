package io.github.jroy.happybot.apis;

import io.github.jroy.happybot.Main;
import io.github.jroy.happybot.apis.exceptions.IllegalAPIState;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.events.StatusChangeEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public abstract class APIBase {

    private String apiName = "NULL";
    private boolean loaded = false;
    private boolean statusLoaded = false;

    public APIBase(String apiName) {
        if (apiName != null) {
            this.apiName = apiName;
        }
    }

    public abstract void loadApi();

    private class Listener extends ListenerAdapter {

        Listener() {
            Main.registerEventListener(this);
        }

        @Override
        public void onStatusChange(StatusChangeEvent event) {
            if (event.getNewStatus() == JDA.Status.CONNECTED && !statusLoaded) {
                onJdaLogin();
                statusLoaded = true;
            }
        }
    }

    public void onJdaLogin() { }

    public void loginApi() throws IllegalAPIState {
        if (loaded)
            throw new IllegalAPIState("API is already loaded.");
        new Listener();
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
