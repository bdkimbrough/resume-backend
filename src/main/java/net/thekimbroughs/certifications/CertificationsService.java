package net.thekimbroughs.certifications;

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
public interface CertificationsService {

    @GenIgnore
    static CertificationsService create() {
        return new CertificationsServiceImpl();
    }

    @GenIgnore
    static net.thekimbroughs.certifications.reactivex.CertificationsService createProxy(Vertx vertx) {
        return net.thekimbroughs.certifications.reactivex.CertificationsService.newInstance(new CertificationsServiceVertxEBProxy(vertx.getDelegate(), getAddress()));
    }

    @GenIgnore
    static String getAddress() {
        return CertificationsService.class.getPackage().getName();
    }

    void getAll(Handler<AsyncResult<List<Certification>>> handler);

    void getById(String id, Handler<AsyncResult<Certification>> handler);

    void createOne(Certification cert, Handler<AsyncResult<Certification>> handler);

    void updateOne(String id, Certification cert, Handler<AsyncResult<Void>> handler);

    void deleteOne(String id, Handler<AsyncResult<Void>> handler);

    @ProxyClose
    void close();
}
