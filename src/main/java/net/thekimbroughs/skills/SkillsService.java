package net.thekimbroughs.skills;

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
public interface SkillsService {

    @GenIgnore
    static SkillsService create() {
        return new SkillsServiceImpl();
    }

    @GenIgnore
    static net.thekimbroughs.skills.reactivex.SkillsService createProxy(Vertx vertx) {
        return net.thekimbroughs.skills.reactivex.SkillsService.newInstance(new SkillsServiceVertxEBProxy(vertx.getDelegate(), getAddress()));
    }

    @GenIgnore
    static String getAddress() {
        return SkillsService.class.getPackage().getName();
    }

    void getAll(Handler<AsyncResult<List<Skill>>> handler);

    void getById(String id, Handler<AsyncResult<Skill>> handler);

    void createOne(Skill skill, Handler<AsyncResult<Skill>> handler);

    void updateOne(String id, Skill skill, Handler<AsyncResult<Void>> handler);

    void deleteOne(String id, Handler<AsyncResult<Void>> handler);

    @ProxyClose
    void close();
}
