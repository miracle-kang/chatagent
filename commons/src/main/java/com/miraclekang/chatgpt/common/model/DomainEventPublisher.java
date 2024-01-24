//package com.miraclekang.chatgpt.common.model;
//
//import org.springframework.context.ApplicationEventPublisher;
//import org.springframework.context.annotation.Lazy;
//import org.springframework.stereotype.Component;
//
//import java.util.Collection;
//
//@Component
//public class DomainEventPublisher {
//
//    private static DomainEventPublisher _this;
//    private final ApplicationEventPublisher publisher;
//
//    public DomainEventPublisher(@Lazy DomainEventPublisher aThis,
//                                ApplicationEventPublisher publisher) {
//        _this = aThis;
//        this.publisher = publisher;
//    }
//
//    public static DomainEventPublisher instance() {
//        return _this;
//    }
//
//    public void publish(DomainEvent event) {
//        this.publisher.publishEvent(event);
//    }
//
//    public void publishAll(Collection<DomainEvent> domainEvents) {
//        domainEvents.forEach(this::publish);
//    }
//}
