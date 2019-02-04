package net.thekimbroughs.certifications;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

import java.util.List;

public class CertificationsServiceImpl implements CertificationsService {
    @Override
    public void getAll(Handler<AsyncResult<List<Certification>>> handler) {

    }

    @Override
    public void getById(String id, Handler<AsyncResult<Certification>> handler) {

    }

    @Override
    public void createOne(Certification cert, Handler<AsyncResult<Certification>> handler) {

    }

    @Override
    public void updateOne(String id, Certification cert, Handler<AsyncResult<Void>> handler) {

    }

    @Override
    public void deleteOne(String id, Handler<AsyncResult<Void>> handler) {

    }

    @Override
    public void close() {
        //nothing to do here
    }
}
