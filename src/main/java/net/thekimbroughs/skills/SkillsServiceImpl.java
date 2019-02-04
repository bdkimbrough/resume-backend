package net.thekimbroughs.skills;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

import java.util.List;

public class SkillsServiceImpl implements SkillsService {

    @Override
    public void getAll(Handler<AsyncResult<List<Skill>>> handler) {

    }

    @Override
    public void getById(String id, Handler<AsyncResult<Skill>> handler) {

    }

    @Override
    public void createOne(Skill skill, Handler<AsyncResult<Skill>> handler) {

    }

    @Override
    public void updateOne(String id, Skill skill, Handler<AsyncResult<Void>> handler) {

    }

    @Override
    public void deleteOne(String id, Handler<AsyncResult<Void>> handler) {

    }

    @Override
    public void close() {
        //Nothing to do here
    }
}
