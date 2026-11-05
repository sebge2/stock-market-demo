package be.sgerard.demo.bank.client.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import be.sgerard.demo.bank.client.model.Client;
import jakarta.validation.ConstraintViolationException;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

/**
 *
 * Verifies the persistence mapping of the client entity.
 *
 */
@DataJpaTest
class ClientPersistenceTest {

  /**
   *
   * Client repository.
   *
   */
  @Autowired
  private ClientRepository clientRepository;

  /**
   *
   * The identifier and the audit timestamps are generated.
   *
   */
  @Test
  void savingGeneratesIdAndAuditFields() {
    final UUID userId = UUID.randomUUID();

    final Client saved = clientRepository.saveAndFlush(new Client("John", "Doe", userId));

    assertThat(saved.getId()).isNotNull();
    assertThat(saved.getCreatedAt()).isNotNull();
    assertThat(saved.getUpdatedAt()).isNotNull();
    assertThat(clientRepository.findById(saved.getId())).get().satisfies(client -> {
      assertThat(client.getFirstName()).isEqualTo("John");
      assertThat(client.getLastName()).isEqualTo("Doe");
      assertThat(client.getUserId()).isEqualTo(userId);
    });
  }

  /**
   *
   * The update timestamp is refreshed on update.
   *
   */
  @Test
  void updateRefreshesUpdatedAt() {
    final Client saved = clientRepository.saveAndFlush(new Client("John", "Doe", UUID.randomUUID()));
    final java.time.LocalDateTime createdAt = saved.getCreatedAt();

    saved.setFirstName("Jane");
    clientRepository.saveAndFlush(saved);

    assertThat(saved.getCreatedAt()).isEqualTo(createdAt);
    assertThat(saved.getUpdatedAt()).isAfterOrEqualTo(createdAt);
  }

  /**
   *
   * The user identifier is unique.
   *
   */
  @Test
  void userIdMustBeUnique() {
    final UUID userId = UUID.randomUUID();
    clientRepository.saveAndFlush(new Client("A", "B", userId));

    assertThatThrownBy(() -> clientRepository.saveAndFlush(new Client("C", "D", userId)))
        .isInstanceOf(DataIntegrityViolationException.class);
  }

  /**
   *
   * The user identifier and the names are mandatory.
   *
   */
  @Test
  void mandatoryFieldsAreValidated() {
    assertThatThrownBy(() -> clientRepository.saveAndFlush(new Client("A", "B", null)))
        .isInstanceOf(ConstraintViolationException.class);
    assertThatThrownBy(() -> clientRepository.saveAndFlush(new Client(" ", "B", UUID.randomUUID())))
        .isInstanceOf(ConstraintViolationException.class);
  }
}
