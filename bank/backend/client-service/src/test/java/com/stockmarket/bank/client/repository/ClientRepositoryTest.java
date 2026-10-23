package com.stockmarket.bank.client.repository;

import com.stockmarket.bank.client.domain.Client;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class ClientRepositoryTest {

    @Autowired
    private ClientRepository clientRepository;

    @Test
    void savingClientGeneratesUuid() {
        Client saved = clientRepository.saveAndFlush(
                new Client("John", "Doe", "john.doe@example.com", "$2a$10$hash"));

        assertThat(saved.getId()).isNotNull();
        assertThat(clientRepository.findById(saved.getId()))
                .get()
                .satisfies(c -> {
                    assertThat(c.getFirstName()).isEqualTo("John");
                    assertThat(c.getLastName()).isEqualTo("Doe");
                    assertThat(c.getEmail()).isEqualTo("john.doe@example.com");
                    assertThat(c.getEncryptedPassword()).isEqualTo("$2a$10$hash");
                });
    }

    @Test
    void findByEmailIgnoresCase() {
        clientRepository.saveAndFlush(new Client("Jane", "Doe", "jane.doe@example.com", "hash"));

        assertThat(clientRepository.findByEmailIgnoreCase("JANE.DOE@example.com")).isPresent();
        assertThat(clientRepository.existsByEmailIgnoreCase("Jane.Doe@Example.com")).isTrue();
        assertThat(clientRepository.existsByEmailIgnoreCase("unknown@example.com")).isFalse();
    }

    @Test
    void emailMustBeUnique() {
        clientRepository.saveAndFlush(new Client("A", "B", "dup@example.com", "hash"));

        assertThatThrownBy(() -> clientRepository.saveAndFlush(new Client("C", "D", "dup@example.com", "hash")))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void invalidEmailIsRejected() {
        assertThatThrownBy(() -> clientRepository.saveAndFlush(new Client("A", "B", "not-an-email", "hash")))
                .isInstanceOf(jakarta.validation.ConstraintViolationException.class);
    }
}
