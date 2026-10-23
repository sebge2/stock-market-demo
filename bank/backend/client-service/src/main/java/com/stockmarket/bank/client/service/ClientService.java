package com.stockmarket.bank.client.service;

import com.stockmarket.bank.client.domain.Client;
import com.stockmarket.bank.client.repository.ClientRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * Creates and retrieves clients. Raw passwords are encrypted before being persisted.
 */
@Service
@Transactional(readOnly = true)
public class ClientService {

    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;

    public ClientService(ClientRepository clientRepository, PasswordEncoder passwordEncoder) {
        this.clientRepository = clientRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Client create(String firstName, String lastName, String email, String rawPassword) {
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("Password must not be blank");
        }
        if (email != null && clientRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("A client already exists with email " + email);
        }
        Client client = new Client(firstName, lastName, email, passwordEncoder.encode(rawPassword));
        return clientRepository.save(client);
    }

    public Optional<Client> findById(UUID id) {
        return clientRepository.findById(id);
    }

    public Optional<Client> findByEmail(String email) {
        return clientRepository.findByEmailIgnoreCase(email);
    }

    public boolean passwordMatches(Client client, String rawPassword) {
        return passwordEncoder.matches(rawPassword, client.getEncryptedPassword());
    }
}
