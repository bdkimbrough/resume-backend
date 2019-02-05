package net.thekimbroughs;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.reactivex.CompletableHelper;
import io.vertx.reactivex.config.ConfigRetriever;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.handler.BodyHandler;
import io.vertx.reactivex.ext.web.handler.StaticHandler;
import io.vertx.reactivex.ext.web.handler.sockjs.SockJSHandler;
import net.thekimbroughs.certifications.Certification;
import net.thekimbroughs.certifications.reactivex.CertificationsService;
import net.thekimbroughs.positions.Position;
import net.thekimbroughs.positions.reactivex.PositionsService;
import net.thekimbroughs.skills.Skill;
import net.thekimbroughs.skills.reactivex.SkillsService;

import static io.netty.handler.codec.http.HttpHeaderValues.APPLICATION_JSON;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;

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

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private Router createRoutes() {
        Router router = Router.router(vertx);

        router.route("/static/*").handler(StaticHandler.create());

        Router rest = Router.router(vertx);
        rest.route().handler(ctx -> {
            ctx.response().putHeader(CONTENT_TYPE, APPLICATION_JSON);
            ctx.next();
        });
        rest.post().handler(BodyHandler.create());
        rest.put().handler(BodyHandler.create());

        Router v1 = Router.router(vertx);

        addSkillsRoutes(v1);
        addPostisionsRoutes(v1);
        addCertificationsRoutes(v1);

        rest.mountSubRouter("/v1", v1);

        rest.mountSubRouter("/rest", rest);

        return router;
    }

    private void addSkillsRoutes(Router router) {

        SkillsService skillsService = net.thekimbroughs.skills.SkillsService.createProxy(vertx);
        router.get("/skills").handler(ctx -> {
            Disposable sub = skillsService.rxGetAll().subscribe(
                    skillList -> ctx.response().setStatusCode(OK.code()).setStatusMessage(OK.reasonPhrase()).end(new JsonArray(skillList).encode()),
                    throwable -> handleErrorResult(ctx, throwable)
            );

            setDisposeTimer(sub);
        });

        router.get("/skills/:id").handler(ctx -> {
            Disposable sub = skillsService.rxGetById(ctx.pathParam("id")).subscribe(
                    skill -> ctx.response().setStatusCode(OK.code()).setStatusMessage(OK.reasonPhrase()).end(skill.toJson().encode()),
                    throwable -> handleErrorResult(ctx, throwable)
            );

            setDisposeTimer(sub);
        });

        router.post("/skills").handler(ctx -> {
            Disposable sub = skillsService.rxCreateOne(new Skill(ctx.getBodyAsJson())).subscribe(
                    skill -> ctx.response().setStatusCode(CREATED.code()).setStatusMessage(CREATED.reasonPhrase()).end(skill.toJson().encode()),
                    throwable -> handleErrorResult(ctx, throwable)
            );

            setDisposeTimer(sub);
        });

        router.put("/skills/:id").handler(ctx -> {
            Disposable sub = skillsService.rxUpdateOne(ctx.pathParam("id"), new Skill(ctx.getBodyAsJson())).subscribe(
                    () -> ctx.response().setStatusCode(ACCEPTED.code()).setStatusMessage(ACCEPTED.reasonPhrase()).end(),
                    throwable -> handleErrorResult(ctx, throwable)
            );

            setDisposeTimer(sub);
        });

        router.delete("/skills/:id").handler(ctx -> {
            Disposable sub = skillsService.rxDeleteOne(ctx.pathParam("id")).subscribe(
                    () -> ctx.response().setStatusCode(ACCEPTED.code()).setStatusMessage(ACCEPTED.reasonPhrase()).end(),
                    throwable -> handleErrorResult(ctx, throwable)
            );

            setDisposeTimer(sub);
        });

        BridgeOptions opt = new BridgeOptions()
                .addOutboundPermitted(new PermittedOptions().setAddress(net.thekimbroughs.skills.SkillsService.getAddress() + ".notifications"));
        SockJSHandler sockJSHandler = SockJSHandler.create(vertx).bridge(opt);
        router.route("/skills/notifications/*").handler(sockJSHandler);
    }

    private void addPostisionsRoutes(Router router) {

        PositionsService positionsService = net.thekimbroughs.positions.PositionsService.createProxy(vertx);
        router.get("/positions").handler(ctx -> {
            Disposable sub = positionsService.rxGetAll().subscribe(
                    positionList -> ctx.response().setStatusCode(OK.code()).setStatusMessage(OK.reasonPhrase()).end(new JsonArray(positionList).encode()),
                    throwable -> handleErrorResult(ctx, throwable)
            );

            setDisposeTimer(sub);
        });

        router.get("/positions/:id").handler(ctx -> {
            Disposable sub = positionsService.rxGetById(ctx.pathParam("id")).subscribe(
                    position -> ctx.response().setStatusCode(OK.code()).setStatusMessage(OK.reasonPhrase()).end(position.toJson().encode()),
                    throwable -> handleErrorResult(ctx, throwable)
            );

            setDisposeTimer(sub);
        });

        router.post("/positions").handler(ctx -> {
            Disposable sub = positionsService.rxCreateOne(new Position(ctx.getBodyAsJson())).subscribe(
                    position -> ctx.response().setStatusCode(CREATED.code()).setStatusMessage(CREATED.reasonPhrase()).end(position.toJson().encode()),
                    throwable -> handleErrorResult(ctx, throwable)
            );

            setDisposeTimer(sub);
        });

        router.put("/positions/:id").handler(ctx -> {
            Disposable sub = positionsService.rxUpdateOne(ctx.pathParam("id"), new Position(ctx.getBodyAsJson())).subscribe(
                    () -> ctx.response().setStatusCode(ACCEPTED.code()).setStatusMessage(ACCEPTED.reasonPhrase()).end(),
                    throwable -> handleErrorResult(ctx, throwable)
            );

            setDisposeTimer(sub);
        });

        router.delete("/positions/:id").handler(ctx -> {
            Disposable sub = positionsService.rxDeleteOne(ctx.pathParam("id")).subscribe(
                    () -> ctx.response().setStatusCode(ACCEPTED.code()).setStatusMessage(ACCEPTED.reasonPhrase()).end(),
                    throwable -> handleErrorResult(ctx, throwable)
            );

            setDisposeTimer(sub);
        });

        BridgeOptions opt = new BridgeOptions()
                .addOutboundPermitted(new PermittedOptions().setAddress(net.thekimbroughs.positions.PositionsService.getAddress() + ".notifications"));
        SockJSHandler sockJSHandler = SockJSHandler.create(vertx).bridge(opt);
        router.route("/positions/notifications/*").handler(sockJSHandler);
    }

    private void addCertificationsRoutes(Router router) {

        CertificationsService certService = net.thekimbroughs.certifications.CertificationsService.createProxy(vertx);
        router.get("/certifications").handler(ctx -> {
            Disposable sub = certService.rxGetAll().subscribe(
                    certificationList -> ctx.response().setStatusCode(OK.code()).setStatusMessage(OK.reasonPhrase()).end(new JsonArray(certificationList).encode()),
                    throwable -> handleErrorResult(ctx, throwable)
            );

            setDisposeTimer(sub);
        });

        router.get("/certifications/:id").handler(ctx -> {
            Disposable sub = certService.rxGetById(ctx.pathParam("id")).subscribe(
                    certification -> ctx.response().setStatusCode(OK.code()).setStatusMessage(OK.reasonPhrase()).end(certification.toJson().encode()),
                    throwable -> handleErrorResult(ctx, throwable)
            );

            setDisposeTimer(sub);
        });

        router.post("/certifications").handler(ctx -> {
            Disposable sub = certService.rxCreateOne(new Certification(ctx.getBodyAsJson())).subscribe(
                    certification -> ctx.response().setStatusCode(CREATED.code()).setStatusMessage(CREATED.reasonPhrase()).end(certification.toJson().encode()),
                    throwable -> handleErrorResult(ctx, throwable)
            );

            setDisposeTimer(sub);
        });

        router.put("/certifications/:id").handler(ctx -> {
            Disposable sub = certService.rxUpdateOne(ctx.pathParam("id"), new Certification(ctx.getBodyAsJson())).subscribe(
                    () -> ctx.response().setStatusCode(ACCEPTED.code()).setStatusMessage(ACCEPTED.reasonPhrase()).end(),
                    throwable -> handleErrorResult(ctx, throwable)
            );

            setDisposeTimer(sub);
        });

        router.delete("/certifications/:id").handler(ctx -> {
            Disposable sub = certService.rxDeleteOne(ctx.pathParam("id")).subscribe(
                    () -> ctx.response().setStatusCode(ACCEPTED.code()).setStatusMessage(ACCEPTED.reasonPhrase()).end(),
                    throwable -> handleErrorResult(ctx, throwable)
            );

            setDisposeTimer(sub);
        });

        BridgeOptions opt = new BridgeOptions()
                .addOutboundPermitted(new PermittedOptions().setAddress(net.thekimbroughs.certifications.CertificationsService.getAddress() + ".notifications"));
        SockJSHandler sockJSHandler = SockJSHandler.create(vertx).bridge(opt);
        router.route("/certifications/notifications/*").handler(sockJSHandler);
    }

    private void setDisposeTimer(Disposable sub) {
        vertx.setTimer(20000, res -> {
            if (!sub.isDisposed()) {
                sub.dispose();
            }
        });
    }

    private void handleErrorResult(RoutingContext ctx, Throwable throwable) {
        ctx.response().setStatusCode(INTERNAL_SERVER_ERROR.code()).setStatusMessage(INTERNAL_SERVER_ERROR.reasonPhrase()).end(throwable.getLocalizedMessage());
    }

}
