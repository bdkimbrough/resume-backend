package net.thekimbroughs.certifications;

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

public class CertificationsServiceImpl implements CertificationsService {

    public static final String CERTIFICATIONS_AGGREGATE = "certifications";

    private JDBCClient sqlClient;
    private Vertx vertx;

    public CertificationsServiceImpl(Vertx vertx) {
        this.vertx = vertx;
        sqlClient = JDBCClient.createShared(vertx, vertx.getOrCreateContext().config().getJsonObject("db"));
    }

    @Override
    public void getAll(Handler<AsyncResult<List<Certification>>> handler) {
        Disposable sub = PostgresDatabaseUtil.getAll(sqlClient, CERTIFICATIONS_AGGREGATE).subscribe(
                resultSet -> handler.handle(Future.succeededFuture(resultSet.getRows().stream().map(Certification::new).collect(Collectors.toList()))),
                throwable -> handler.handle(Future.failedFuture(throwable))
        );

        setDisposeTimer(vertx, sub);
    }

    @Override
    public void getById(String id, Handler<AsyncResult<Certification>> handler) {
        Disposable sub = PostgresDatabaseUtil.getById(sqlClient, CERTIFICATIONS_AGGREGATE, id).subscribe(
                resultSet -> {
                    if (resultSet.getNumRows() > 0) {
                        handler.handle(Future.succeededFuture(new Certification(resultSet.getRows().get(0))));
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
    public void createOne(Certification cert, Handler<AsyncResult<Certification>> handler) {
        Disposable sub = PostgresDatabaseUtil.createOne(sqlClient, CERTIFICATIONS_AGGREGATE, cert.toJson()).subscribe(
                updateResult -> handler.handle(Future.succeededFuture(new Certification(updateResult.toJson()))),
                throwable -> handler.handle(Future.failedFuture(throwable))
        );

        setDisposeTimer(vertx, sub);
    }

    @Override
    public void updateOne(String id, Certification cert, Handler<AsyncResult<Void>> handler) {
        Disposable sub = PostgresDatabaseUtil.updateOne(sqlClient, CERTIFICATIONS_AGGREGATE, id, cert.toJson()).subscribe(
                updateResult -> handler.handle(Future.succeededFuture()),
                throwable -> handler.handle(Future.failedFuture(throwable))
        );

        setDisposeTimer(vertx, sub);
    }

    @Override
    public void deleteOne(String id, Handler<AsyncResult<Void>> handler) {
        Disposable sub = PostgresDatabaseUtil.deleteOne(sqlClient, CERTIFICATIONS_AGGREGATE, id).subscribe(
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
