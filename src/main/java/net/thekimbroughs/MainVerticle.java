package net.thekimbroughs;

import io.reactivex.Completable;
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
import static net.thekimbroughs.util.DisposeUtil.setDisposeTimer;

public class MainVerticle extends AbstractVerticle {

    private static final String SKILLS = "/skills";
    private static final String CERTIFICATIONS = "/certifications";
    private static final String POSITIONS = "/positions";
    private static final String STAR = "/*";
    private static final String ID = "/:id";
    private static final String NOTIFICATIONS = "/notifications" + STAR;
    private static final String PATH_ID = "id";

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
        addPositionsRoutes(v1);
        addCertificationsRoutes(v1);

        rest.mountSubRouter("/v1", v1);

        router.mountSubRouter("/rest", rest);

        Router sockjs = Router.router(vertx);

        SockJSHandler certificationsNotificationsHandler = SockJSHandler.create(vertx)
                .bridge(getBridgeOptions(true, false, net.thekimbroughs.certifications.CertificationsService.getAddress() + ".notifications"));
        SockJSHandler certificationsServiceHandler = SockJSHandler.create(vertx)
                .bridge(getBridgeOptions(true, true, net.thekimbroughs.certifications.CertificationsService.getAddress()));

        sockjs.route(CERTIFICATIONS + NOTIFICATIONS).handler(certificationsNotificationsHandler);
        sockjs.route(CERTIFICATIONS + STAR).handler(certificationsServiceHandler);

        SockJSHandler positionsNotificationsHandler = SockJSHandler.create(vertx)
                .bridge(getBridgeOptions(true, false, net.thekimbroughs.positions.PositionsService.getAddress() + ".notifications"));
        SockJSHandler positionsServiceHandler = SockJSHandler.create(vertx)
                .bridge(getBridgeOptions(true, true, net.thekimbroughs.positions.PositionsService.getAddress()));

        sockjs.route(POSITIONS + NOTIFICATIONS).handler(positionsNotificationsHandler);
        sockjs.route(POSITIONS + STAR).handler(positionsServiceHandler);

        SockJSHandler skillsNotificationsHandler = SockJSHandler.create(vertx)
                .bridge(getBridgeOptions(true, false, net.thekimbroughs.skills.SkillsService.getAddress() + ".notifications"));
        SockJSHandler skillsServiceHandler = SockJSHandler.create(vertx)
                .bridge(getBridgeOptions(true, true, net.thekimbroughs.skills.SkillsService.getAddress()));

        sockjs.route(SKILLS + NOTIFICATIONS).handler(skillsNotificationsHandler);
        sockjs.route(SKILLS + STAR).handler(skillsServiceHandler);

        router.mountSubRouter("/sockjs", sockjs);

        return router;
    }

    private BridgeOptions getBridgeOptions(boolean outboundPermitted, boolean inboundPermitted, String address) {

        BridgeOptions opt = new BridgeOptions();

        if (outboundPermitted) {
            opt.addOutboundPermitted(new PermittedOptions().setAddress(address));
        }

        if (inboundPermitted) {
            opt.addInboundPermitted(new PermittedOptions().setAddress(address));
        }

        return opt;
    }

    @SuppressWarnings("Duplicates")
    private void addCertificationsRoutes(Router router) {

        CertificationsService certService = net.thekimbroughs.certifications.CertificationsService.createProxy(vertx);
        router.get(CERTIFICATIONS).handler(ctx -> {
            Disposable sub = certService.rxGetAll().subscribe(
                    certificationList -> ctx.response().setStatusCode(OK.code()).setStatusMessage(OK.reasonPhrase()).end(new JsonArray(certificationList).encode()),
                    throwable -> handleErrorResult(ctx, throwable)
            );

            setDisposeTimer(vertx, sub);
        });

        router.get(CERTIFICATIONS + ID).handler(ctx -> {
            Disposable sub = certService.rxGetById(ctx.pathParam(PATH_ID)).subscribe(
                    certification -> ctx.response().setStatusCode(OK.code()).setStatusMessage(OK.reasonPhrase()).end(certification.toJson().encode()),
                    throwable -> handleErrorResult(ctx, throwable)
            );

            setDisposeTimer(vertx, sub);
        });

        router.post(CERTIFICATIONS).handler(ctx -> {
            Disposable sub = certService.rxCreateOne(new Certification(ctx.getBodyAsJson())).subscribe(
                    certification -> ctx.response().setStatusCode(CREATED.code()).setStatusMessage(CREATED.reasonPhrase()).end(certification.toJson().encode()),
                    throwable -> handleErrorResult(ctx, throwable)
            );

            setDisposeTimer(vertx, sub);
        });

        router.put(CERTIFICATIONS + ID).handler(ctx -> {
            Disposable sub = certService.rxUpdateOne(ctx.pathParam(PATH_ID), new Certification(ctx.getBodyAsJson())).subscribe(
                    () -> ctx.response().setStatusCode(ACCEPTED.code()).setStatusMessage(ACCEPTED.reasonPhrase()).end(),
                    throwable -> handleErrorResult(ctx, throwable)
            );

            setDisposeTimer(vertx, sub);
        });

        router.delete(CERTIFICATIONS + ID).handler(ctx -> {
            Disposable sub = certService.rxDeleteOne(ctx.pathParam(PATH_ID)).subscribe(
                    () -> ctx.response().setStatusCode(ACCEPTED.code()).setStatusMessage(ACCEPTED.reasonPhrase()).end(),
                    throwable -> handleErrorResult(ctx, throwable)
            );

            setDisposeTimer(vertx, sub);
        });
    }

    @SuppressWarnings("Duplicates")
    private void addPositionsRoutes(Router router) {

        PositionsService positionsService = net.thekimbroughs.positions.PositionsService.createProxy(vertx);
        router.get(POSITIONS).handler(ctx -> {
            Disposable sub = positionsService.rxGetAll().subscribe(
                    positionList -> ctx.response().setStatusCode(OK.code()).setStatusMessage(OK.reasonPhrase()).end(new JsonArray(positionList).encode()),
                    throwable -> handleErrorResult(ctx, throwable)
            );

            setDisposeTimer(vertx, sub);
        });

        router.get(POSITIONS + ID).handler(ctx -> {
            Disposable sub = positionsService.rxGetById(ctx.pathParam(PATH_ID)).subscribe(
                    position -> ctx.response().setStatusCode(OK.code()).setStatusMessage(OK.reasonPhrase()).end(position.toJson().encode()),
                    throwable -> handleErrorResult(ctx, throwable)
            );

            setDisposeTimer(vertx, sub);
        });

        router.post(POSITIONS).handler(ctx -> {
            Disposable sub = positionsService.rxCreateOne(new Position(ctx.getBodyAsJson())).subscribe(
                    position -> ctx.response().setStatusCode(CREATED.code()).setStatusMessage(CREATED.reasonPhrase()).end(position.toJson().encode()),
                    throwable -> handleErrorResult(ctx, throwable)
            );

            setDisposeTimer(vertx, sub);
        });

        router.put(POSITIONS + ID).handler(ctx -> {
            Disposable sub = positionsService.rxUpdateOne(ctx.pathParam(PATH_ID), new Position(ctx.getBodyAsJson())).subscribe(
                    () -> ctx.response().setStatusCode(ACCEPTED.code()).setStatusMessage(ACCEPTED.reasonPhrase()).end(),
                    throwable -> handleErrorResult(ctx, throwable)
            );

            setDisposeTimer(vertx, sub);
        });

        router.delete(POSITIONS + ID).handler(ctx -> {
            Disposable sub = positionsService.rxDeleteOne(ctx.pathParam(PATH_ID)).subscribe(
                    () -> ctx.response().setStatusCode(ACCEPTED.code()).setStatusMessage(ACCEPTED.reasonPhrase()).end(),
                    throwable -> handleErrorResult(ctx, throwable)
            );

            setDisposeTimer(vertx, sub);
        });
    }

    @SuppressWarnings("Duplicates")
    private void addSkillsRoutes(Router router) {

        SkillsService skillsService = net.thekimbroughs.skills.SkillsService.createProxy(vertx);
        router.get(SKILLS).handler(ctx -> {
            Disposable sub = skillsService.rxGetAll().subscribe(
                    skillList -> ctx.response().setStatusCode(OK.code()).setStatusMessage(OK.reasonPhrase()).end(new JsonArray(skillList).encode()),
                    throwable -> handleErrorResult(ctx, throwable)
            );

            setDisposeTimer(vertx, sub);
        });

        router.get(SKILLS + ID).handler(ctx -> {
            Disposable sub = skillsService.rxGetById(ctx.pathParam(PATH_ID)).subscribe(
                    skill -> ctx.response().setStatusCode(OK.code()).setStatusMessage(OK.reasonPhrase()).end(skill.toJson().encode()),
                    throwable -> handleErrorResult(ctx, throwable)
            );

            setDisposeTimer(vertx, sub);
        });

        router.post(SKILLS).handler(ctx -> {
            Disposable sub = skillsService.rxCreateOne(new Skill(ctx.getBodyAsJson())).subscribe(
                    skill -> ctx.response().setStatusCode(CREATED.code()).setStatusMessage(CREATED.reasonPhrase()).end(skill.toJson().encode()),
                    throwable -> handleErrorResult(ctx, throwable)
            );

            setDisposeTimer(vertx, sub);
        });

        router.put(SKILLS + ID).handler(ctx -> {
            Disposable sub = skillsService.rxUpdateOne(ctx.pathParam(PATH_ID), new Skill(ctx.getBodyAsJson())).subscribe(
                    () -> ctx.response().setStatusCode(ACCEPTED.code()).setStatusMessage(ACCEPTED.reasonPhrase()).end(),
                    throwable -> handleErrorResult(ctx, throwable)
            );

            setDisposeTimer(vertx, sub);
        });

        router.delete(SKILLS + ID).handler(ctx -> {
            Disposable sub = skillsService.rxDeleteOne(ctx.pathParam(PATH_ID)).subscribe(
                    () -> ctx.response().setStatusCode(ACCEPTED.code()).setStatusMessage(ACCEPTED.reasonPhrase()).end(),
                    throwable -> handleErrorResult(ctx, throwable)
            );

            setDisposeTimer(vertx, sub);
        });
    }

    private void handleErrorResult(RoutingContext ctx, Throwable throwable) {
        ctx.response().setStatusCode(INTERNAL_SERVER_ERROR.code()).setStatusMessage(INTERNAL_SERVER_ERROR.reasonPhrase()).end(throwable.getLocalizedMessage());
    }

}
