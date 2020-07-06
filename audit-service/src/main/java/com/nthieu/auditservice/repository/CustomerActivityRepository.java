package com.nthieu.auditservice.repository;

import com.nthieu.auditservice.domain.CustomerActivity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "customer-activities", path = "customer-activities")
public interface CustomerActivityRepository extends MongoRepository<CustomerActivity, String> {
}
