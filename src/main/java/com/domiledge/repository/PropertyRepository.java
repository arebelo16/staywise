package com.domiledge.repository;

import com.domiledge.model.Property;
import com.domiledge.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PropertyRepository extends JpaRepository<Property, UUID> {
    List<Property> findAllByOwner(User owner);
}
