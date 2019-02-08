package net.thekimbroughs.positions;

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
public class PositionsServiceImpl implements PositionsService {

    public static final String POSITIONS_AGGREGATE = "positions";

    private JDBCClient sqlClient;
    private Vertx vertx;

    public PositionsServiceImpl(Vertx vertx) {
        this.vertx = vertx;
        sqlClient = JDBCClient.createShared(vertx, vertx.getOrCreateContext().config().getJsonObject("db"));
    }

    @Override
    public void getAll(Handler<AsyncResult<List<Position>>> handler) {
        Disposable sub = PostgresDatabaseUtil.getAll(sqlClient, POSITIONS_AGGREGATE).subscribe(
                resultSet -> handler.handle(Future.succeededFuture(resultSet.getRows().stream().map(Position::new).collect(Collectors.toList()))),
                throwable -> handler.handle(Future.failedFuture(throwable))
        );

        setDisposeTimer(vertx, sub);
    }

    @Override
    public void getById(String id, Handler<AsyncResult<Position>> handler) {
        Disposable sub = PostgresDatabaseUtil.getById(sqlClient, POSITIONS_AGGREGATE, id).subscribe(
                resultSet -> {
                    if (resultSet.getNumRows() > 0) {
                        handler.handle(Future.succeededFuture(new Position(resultSet.getRows().get(0))));
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
    public void createOne(Position position, Handler<AsyncResult<Position>> handler) {
        Disposable sub = PostgresDatabaseUtil.createOne(sqlClient, POSITIONS_AGGREGATE, position.toJson()).subscribe(
                updateResult -> handler.handle(Future.succeededFuture(new Position(updateResult.toJson()))),
                throwable -> handler.handle(Future.failedFuture(throwable))
        );

        setDisposeTimer(vertx, sub);
    }

    @Override
    public void updateOne(String id, Position position, Handler<AsyncResult<Void>> handler) {
        Disposable sub = PostgresDatabaseUtil.updateOne(sqlClient, POSITIONS_AGGREGATE, id, position.toJson()).subscribe(
                updateResult -> handler.handle(Future.succeededFuture()),
                throwable -> handler.handle(Future.failedFuture(throwable))
        );

        setDisposeTimer(vertx, sub);
    }

    @Override
    public void deleteOne(String id, Handler<AsyncResult<Void>> handler) {
        Disposable sub = PostgresDatabaseUtil.deleteOne(sqlClient, POSITIONS_AGGREGATE, id).subscribe(
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
