package net.thekimbroughs.util;

import io.reactivex.disposables.Disposable;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;

public class CommonUtil {
    CommonUtil() {}

    public static void setDisposeTimer(Vertx vertx, Disposable disposable) {
        vertx.setTimer(60000, res -> {
            if (!disposable.isDisposed()) {
                disposable.dispose();
            }
        });
    }

    public static void sendNotification(Vertx vertx, String address, String notificationType, String id) {
        vertx.eventBus().publish(address + ".notification", new JsonObject().put("action", notificationType).put("id", id));
    }
}
