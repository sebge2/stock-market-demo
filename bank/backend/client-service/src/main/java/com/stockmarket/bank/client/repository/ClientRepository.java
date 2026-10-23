package com.stockmarket.bank.client.repository;

import com.stockmarket.bank.client.domain.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ClientRepository extends JpaRepository<Client, UUID> {

    Optional<Client> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);
}
