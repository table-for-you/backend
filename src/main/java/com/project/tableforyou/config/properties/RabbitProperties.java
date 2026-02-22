package com.project.tableforyou.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter @Setter
@ConfigurationProperties(prefix = "rabbitmq")
public class RabbitProperties {

    private Exchange exchange;
    private Queue queue;
    private Routing routing;
    private Ttl ttl;

    @Getter @Setter
    public static class Exchange {
        private String reservation;
        private String timeoutDelay;
        private String timeout;
    }

    @Getter @Setter
    public static class Queue {
        private String joined;
        private String attemptFinished;
        private String timeoutDelay;
        private String timeout;
    }

    @Getter @Setter
    public static class Routing {
        private String queueJoined;
        private String attemptFinished;
        private String timeoutDelay;
        private String timeout;
    }

    @Getter @Setter
    public static class Ttl {
        private Integer attempt;
    }
}
