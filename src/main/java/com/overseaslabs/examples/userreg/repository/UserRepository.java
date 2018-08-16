package com.overseaslabs.examples.userreg.repository;

import com.overseaslabs.examples.userreg.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
}