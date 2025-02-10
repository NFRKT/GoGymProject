package com.GoGym.repository;

import com.GoGym.module.ClientDetails;
import com.GoGym.module.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientDetailsRepository extends JpaRepository<ClientDetails, Long> {
    ClientDetails findByUser(User user);
}
