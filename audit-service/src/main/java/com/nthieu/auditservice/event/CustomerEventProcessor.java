package com.nthieu.auditservice.event;

import com.nthieu.auditservice.domain.CustomerActivity;
import com.nthieu.auditservice.repository.CustomerActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;

@EnableBinding(Sink.class)
public class CustomerEventProcessor {
    private final CustomerActivityRepository activityRepository;

    @Autowired
    public CustomerEventProcessor(CustomerActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    @StreamListener(Sink.INPUT)
    public void processCustomerEvent(CustomerActivity customerActivity) {
        activityRepository.save(customerActivity);
    }
}
