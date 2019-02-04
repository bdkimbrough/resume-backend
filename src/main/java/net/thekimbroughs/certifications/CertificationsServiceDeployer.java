package net.thekimbroughs.certifications;

import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.serviceproxy.ServiceBinder;

public class CertificationsServiceDeployer extends AbstractVerticle {

    @Override
    public void start() {
        CertificationsService skillsService = CertificationsService.create();

        new ServiceBinder(vertx.getDelegate())
                .setAddress(CertificationsService.getAddress())
                .register(CertificationsService.class, skillsService);
    }
}