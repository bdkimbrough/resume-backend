package net.thekimbroughs.skills;

import io.reactivex.disposables.Disposable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.jdbc.JDBCClient;
import io.vertx.serviceproxy.ServiceException;
import net.thekimbroughs.util.PostgresDatabaseUtil;

import java.util.List;
import java.util.stream.Collectors;

import static net.thekimbroughs.util.DisposeUtil.setDisposeTimer;

@SuppressWarnings("Duplicates")
public class SkillsServiceImpl implements SkillsService {

    public static final String SKILLS_AGGREGATE = "skills";

    private JDBCClient sqlClient;
    private Vertx vertx;

    public SkillsServiceImpl(Vertx vertx) {
        this.vertx = vertx;
        sqlClient = JDBCClient.createShared(vertx, vertx.getOrCreateContext().config().getJsonObject("db"));
    }

    @Override
    public void getAll(Handler<AsyncResult<List<Skill>>> handler) {
        Disposable sub = PostgresDatabaseUtil.getAll(sqlClient, SKILLS_AGGREGATE).subscribe(
                resultSet -> handler.handle(Future.succeededFuture(resultSet.getRows().stream().map(Skill::new).collect(Collectors.toList()))),
                throwable -> handler.handle(Future.failedFuture(throwable))
        );

        setDisposeTimer(vertx, sub);
    }

    @Override
    public void getById(String id, Handler<AsyncResult<Skill>> handler) {
        Disposable sub = PostgresDatabaseUtil.getById(sqlClient, SKILLS_AGGREGATE, id).subscribe(
                resultSet -> {
                    if (resultSet.getNumRows() > 0) {
                        handler.handle(Future.succeededFuture(new Skill(resultSet.getRows().get(0))));
                    }
                    else {
                        handler.handle(ServiceException.fail(404, "Id not found"));
                    }
                },
                throwable -> handler.handle(Future.failedFuture(throwable))
        );

        setDisposeTimer(vertx, sub);
    }

    @Override
    public void createOne(Skill skill, Handler<AsyncResult<Skill>> handler) {
        Disposable sub = PostgresDatabaseUtil.createOne(sqlClient, SKILLS_AGGREGATE, skill.toJson()).subscribe(
                updateResult -> handler.handle(Future.succeededFuture(new Skill(updateResult.toJson()))),
                throwable -> handler.handle(Future.failedFuture(throwable))
        );

        setDisposeTimer(vertx, sub);
    }

    @Override
    public void updateOne(String id, Skill skill, Handler<AsyncResult<Void>> handler) {
        Disposable sub = PostgresDatabaseUtil.updateOne(sqlClient, SKILLS_AGGREGATE, id, skill.toJson()).subscribe(
                updateResult -> handler.handle(Future.succeededFuture()),
                throwable -> handler.handle(Future.failedFuture(throwable))
        );

        setDisposeTimer(vertx, sub);
    }

    @Override
    public void deleteOne(String id, Handler<AsyncResult<Void>> handler) {
        Disposable sub = PostgresDatabaseUtil.deleteOne(sqlClient, SKILLS_AGGREGATE, id).subscribe(
                updateResult -> handler.handle(Future.succeededFuture()),
                throwable -> handler.handle(Future.failedFuture(throwable))
        );

        setDisposeTimer(vertx, sub);
    }

    @Override
    public void close() {
        sqlClient.close();
    }
}
