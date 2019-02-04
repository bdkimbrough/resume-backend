package net.thekimbroughs;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.CompletableHelper;
import io.vertx.reactivex.config.ConfigRetriever;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.sockjs.SockJSHandler;

public class MainVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> startFut) {

        ConfigRetriever configRetriever = ConfigRetriever.create(vertx);

        configRetriever.rxGetConfig()
                .flatMapCompletable(this::createHttpServer)
                .subscribe(CompletableHelper.toObserver(startFut));

    }


    private Completable createHttpServer(JsonObject config) {
        return vertx
                .createHttpServer(new HttpServerOptions(config))
                .requestHandler(createRoutes())
                .rxListen(config.getInteger("HTTP_PORT", 8080))
                .ignoreElement();
    }

    private Router createRoutes() {
        Router router = Router.router(vertx);

        return router;
    }

}
