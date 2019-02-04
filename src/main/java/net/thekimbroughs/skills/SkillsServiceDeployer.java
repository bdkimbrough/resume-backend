package net.thekimbroughs.skills;

import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.serviceproxy.ServiceBinder;

public class SkillsServiceDeployer extends AbstractVerticle {

    @Override
    public void start() {
        SkillsService skillsService = SkillsService.create();

        new ServiceBinder(vertx.getDelegate())
                .setAddress(SkillsService.getAddress())
                .register(SkillsService.class, skillsService);
    }
}
