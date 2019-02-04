package net.thekimbroughs.positions;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

import java.util.List;

public class PositionsServiceImpl implements PositionsService {
    @Override
    public void getAll(Handler<AsyncResult<List<Position>>> handler) {

    }

    @Override
    public void getById(String id, Handler<AsyncResult<Position>> handler) {

    }

    @Override
    public void createOne(Position position, Handler<AsyncResult<Position>> handler) {

    }

    @Override
    public void updateOne(String id, Position position, Handler<AsyncResult<Void>> handler) {

    }

    @Override
    public void deleteOne(String id, Handler<AsyncResult<Void>> handler) {

    }

    @Override
    public void close() {
        //nothing to do here
    }
}
