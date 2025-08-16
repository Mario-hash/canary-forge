package com.canaryforge.adapter.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface EventRepository extends ReactiveCrudRepository<EventDoc, String> {

}
