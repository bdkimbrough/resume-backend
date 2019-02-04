package net.thekimbroughs.positions;

import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.ProxyClose;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.reactivex.core.Vertx;

import java.util.List;

@VertxGen
@ProxyGen
public interface PositionsService {


    @GenIgnore
    static PositionsService create() {
        return new PositionsServiceImpl();
    }

    @GenIgnore
    static net.thekimbroughs.positions.reactivex.PositionsService createProxy(Vertx vertx) {
        return net.thekimbroughs.positions.reactivex.PositionsService.newInstance(new PositionsServiceVertxEBProxy(vertx.getDelegate(), getAddress()));
    }

    @GenIgnore
    static String getAddress() {
        return PositionsService.class.getPackage().getName();
    }

    void getAll(Handler<AsyncResult<List<Position>>> handler);

    void getById(String id, Handler<AsyncResult<Position>> handler);

    void createOne(Position position, Handler<AsyncResult<Position>> handler);

    void updateOne(String id, Position position, Handler<AsyncResult<Void>> handler);

    void deleteOne(String id, Handler<AsyncResult<Void>> handler);

    @ProxyClose
    void close();
}
