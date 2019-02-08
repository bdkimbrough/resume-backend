package net.thekimbroughs.util;

import io.reactivex.disposables.Disposable;
import io.vertx.reactivex.core.Vertx;

public class DisposeUtil {
    DisposeUtil() {}

    public static void setDisposeTimer(Vertx vertx, Disposable disposable) {
        vertx.setTimer(60000, res -> {
            if (!disposable.isDisposed()) {
                disposable.dispose();
            }
        });
    }
}
