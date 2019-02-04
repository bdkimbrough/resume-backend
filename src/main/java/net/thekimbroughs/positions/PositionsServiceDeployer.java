package net.thekimbroughs.positions;

import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.serviceproxy.ServiceBinder;

public class PositionsServiceDeployer extends AbstractVerticle {

    @Override
    public void start() {
        PositionsService skillsService = PositionsService.create();

        new ServiceBinder(vertx.getDelegate())
                .setAddress(PositionsService.getAddress())
                .register(PositionsService.class, skillsService);
    }
}
