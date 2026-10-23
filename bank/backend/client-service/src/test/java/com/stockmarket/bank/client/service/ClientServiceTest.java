package com.stockmarket.bank.client.service;

import com.stockmarket.bank.client.domain.Client;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class ClientServiceTest {

    @Autowired
    private ClientService clientService;

    @Test
    void createEncryptsPassword() {
        Client client = clientService.create("John", "Doe", "john@example.com", "s3cret!");

        assertThat(client.getId()).isNotNull();
        assertThat(client.getEncryptedPassword()).isNotEqualTo("s3cret!").startsWith("$2");
        assertThat(clientService.passwordMatches(client, "s3cret!")).isTrue();
        assertThat(clientService.passwordMatches(client, "wrong")).isFalse();
        assertThat(clientService.findByEmail("john@example.com")).contains(client);
    }

    @Test
    void createRejectsDuplicateEmail() {
        clientService.create("John", "Doe", "dup@example.com", "pwd");

        assertThatThrownBy(() -> clientService.create("Other", "Person", "DUP@example.com", "pwd"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void createRejectsBlankPassword() {
        assertThatThrownBy(() -> clientService.create("John", "Doe", "blank@example.com", " "))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
