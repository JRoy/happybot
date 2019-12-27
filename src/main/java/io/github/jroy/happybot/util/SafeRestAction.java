package io.github.jroy.happybot.util;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.RestAction;

import javax.annotation.Nullable;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@SuppressWarnings("rawtypes")
public class SafeRestAction {

  private final RestAction restAction;

  private TimeUnit timeUnit = null;
  private Long time = null;

  public SafeRestAction(RestAction restAction) {
    this.restAction = restAction;
  }

  public SafeRestAction wait(TimeUnit timeUnit, long time) {
    this.timeUnit = timeUnit;
    this.time = time;
    return this;
  }

  public void queue() {
    queue(null);
  }

  public void queue(@Nullable Consumer<Message> consumer) {
    if (timeUnit == null || time == null) {
      restAction.queue();
      return;
    }
    new Thread(new ImpendAction(this, consumer)).start();
  }


  private Long getTime() {
    return time;
  }

  private TimeUnit getTimeUnit() {
    return timeUnit;
  }

  private RestAction getRestAction() {
    return restAction;
  }

  private static class ImpendAction implements Runnable {

    private final SafeRestAction safeRestAction;
    private final Consumer consumer;

    ImpendAction(SafeRestAction safeRestAction, @Nullable Consumer<Message> consumer) {
      this.safeRestAction = safeRestAction;
      this.consumer = consumer;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void run() {
      try {
        safeRestAction.getTimeUnit().sleep(safeRestAction.getTime());
        if (consumer == null) {
          safeRestAction.getRestAction().queue();
        } else {
          safeRestAction.getRestAction().queue(consumer);
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

}