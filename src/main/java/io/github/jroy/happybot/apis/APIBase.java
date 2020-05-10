package io.github.jroy.happybot.apis;

import io.github.jroy.happybot.Main;
import io.github.jroy.happybot.apis.exceptions.IllegalAPIState;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.StatusChangeEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public abstract class APIBase {
  @Getter
  private String apiName = "NULL";
  private boolean loaded = false;
  private boolean statusLoaded = false;

  public APIBase(String apiName) {
    if (apiName != null) {
      this.apiName = apiName;
    }
  }

  public abstract void loadApi();

  public void onJdaLogin() {
  }

  public void loginApi() throws IllegalAPIState {
    if (loaded) {
      throw new IllegalAPIState("API is already loaded.");
    }
    new Listener();
    loadApi();
    loaded = true;
  }

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

  public class ListenerImpl extends Listener {
  }
}
