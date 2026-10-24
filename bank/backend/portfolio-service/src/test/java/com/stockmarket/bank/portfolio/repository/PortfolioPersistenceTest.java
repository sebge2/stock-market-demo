package com.stockmarket.bank.portfolio.repository;

import com.stockmarket.bank.portfolio.domain.Portfolio;
import com.stockmarket.bank.portfolio.domain.Position;
import com.stockmarket.bank.portfolio.domain.Purchase;
import com.stockmarket.bank.portfolio.domain.PurchaseReceipt;
import com.stockmarket.bank.portfolio.domain.PurchaseStatus;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class PortfolioPersistenceTest {

    @Autowired
    private PortfolioRepository portfolioRepository;
    @Autowired
    private PositionRepository positionRepository;
    @Autowired
    private PurchaseRepository purchaseRepository;
    @Autowired
    private PurchaseReceiptRepository receiptRepository;
    @Autowired
    private TestEntityManager entityManager;

    private Portfolio newPortfolio() {
        return portfolioRepository.saveAndFlush(new Portfolio(UUID.randomUUID()));
    }

    private Position newPosition(Portfolio portfolio, UUID stockId) {
        return positionRepository.saveAndFlush(new Position(
                portfolio, stockId, "APPLE", new BigDecimal("10"), new BigDecimal("150.50")));
    }

    private PurchaseReceipt newReceipt(String number) {
        return receiptRepository.saveAndFlush(new PurchaseReceipt(
                number, UUID.randomUUID(), UUID.randomUUID(), LocalDateTime.now(), "notes"));
    }

    @Test
    void portfolioGetsGeneratedIdAndCreationDate() {
        UUID clientId = UUID.randomUUID();
        Portfolio saved = portfolioRepository.saveAndFlush(new Portfolio(clientId));

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(portfolioRepository.findByClientId(clientId)).contains(saved);
        assertThat(portfolioRepository.findByClientId(UUID.randomUUID())).isEmpty();
    }

    @Test
    void portfolioWithoutClientIsRejected() {
        assertThatThrownBy(() -> portfolioRepository.saveAndFlush(new Portfolio(null)))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void positionIsPersistedAndLinkedToPortfolio() {
        Portfolio portfolio = newPortfolio();
        UUID stockId = UUID.randomUUID();
        Position saved = newPosition(portfolio, stockId);

        entityManager.clear();

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getLastUpdatedAt()).isNotNull();
        assertThat(positionRepository.findByPortfolioId(portfolio.getId())).hasSize(1);
        assertThat(positionRepository.findByPortfolioIdAndStockId(portfolio.getId(), stockId))
                .get()
                .satisfies(p -> {
                    assertThat(p.getStockSymbol()).isEqualTo("APPLE");
                    assertThat(p.getQuantity()).isEqualByComparingTo("10");
                    assertThat(p.getAverageCost()).isEqualByComparingTo("150.50");
                });
        assertThat(portfolioRepository.findById(portfolio.getId()).orElseThrow().getPositions()).hasSize(1);
    }

    @Test
    void positionIsUniquePerPortfolioAndStock() {
        Portfolio portfolio = newPortfolio();
        UUID stockId = UUID.randomUUID();
        newPosition(portfolio, stockId);

        assertThatThrownBy(() -> newPosition(portfolio, stockId))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void sameStockAllowedInDifferentPortfolios() {
        UUID stockId = UUID.randomUUID();
        newPosition(newPortfolio(), stockId);

        assertThat(newPosition(newPortfolio(), stockId).getId()).isNotNull();
    }

    @Test
    void negativeQuantityIsRejected() {
        Portfolio portfolio = newPortfolio();

        assertThatThrownBy(() -> positionRepository.saveAndFlush(new Position(
                portfolio, UUID.randomUUID(), "APPLE", new BigDecimal("-1"), BigDecimal.ONE)))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void positionWithoutPortfolioIsRejected() {
        assertThatThrownBy(() -> positionRepository.saveAndFlush(new Position(
                null, UUID.randomUUID(), "APPLE", BigDecimal.ONE, BigDecimal.ONE)))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void positionLastUpdatedAtIsRefreshedOnUpdate() throws InterruptedException {
        Position position = newPosition(newPortfolio(), UUID.randomUUID());
        LocalDateTime before = position.getLastUpdatedAt();

        Thread.sleep(5);
        position.setQuantity(new BigDecimal("20"));
        positionRepository.saveAndFlush(position);

        assertThat(position.getLastUpdatedAt()).isAfter(before);
        assertThat(position.getCreatedAt()).isBefore(position.getLastUpdatedAt());
    }

    @Test
    void purchaseIsPersistedWithReceiptAndComputedTotal() {
        Position position = newPosition(newPortfolio(), UUID.randomUUID());
        PurchaseReceipt receipt = newReceipt("RCP-20250101-0001");
        Purchase purchase = new Purchase(position, new BigDecimal("3"), new BigDecimal("1.5"),
                LocalDateTime.now(), PurchaseStatus.EXECUTED);
        purchase.setReceipt(receipt);

        Purchase saved = purchaseRepository.saveAndFlush(purchase);
        entityManager.clear();

        Purchase found = purchaseRepository.findById(saved.getId()).orElseThrow();
        assertThat(found.getTotalAmount()).isEqualByComparingTo("4.5");
        assertThat(found.getStatus()).isEqualTo(PurchaseStatus.EXECUTED);
        assertThat(found.getCreatedAt()).isNotNull();
        assertThat(found.getReceipt().getId()).isEqualTo(receipt.getId());
        assertThat(found.getPosition().getId()).isEqualTo(position.getId());
        assertThat(purchaseRepository.findByPositionId(position.getId())).hasSize(1);
    }

    @Test
    void pendingPurchaseWithoutReceiptIsAllowed() {
        Position position = newPosition(newPortfolio(), UUID.randomUUID());

        Purchase saved = purchaseRepository.saveAndFlush(new Purchase(position, BigDecimal.ONE,
                BigDecimal.TEN, LocalDateTime.now(), PurchaseStatus.PENDING));

        assertThat(saved.getReceipt()).isNull();
    }

    @Test
    void receiptCanOnlyBelongToOnePurchase() {
        Position position = newPosition(newPortfolio(), UUID.randomUUID());
        PurchaseReceipt receipt = newReceipt("RCP-20250101-0002");
        Purchase first = new Purchase(position, BigDecimal.ONE, BigDecimal.TEN, LocalDateTime.now(), PurchaseStatus.EXECUTED);
        first.setReceipt(receipt);
        purchaseRepository.saveAndFlush(first);

        Purchase second = new Purchase(position, BigDecimal.ONE, BigDecimal.TEN, LocalDateTime.now(), PurchaseStatus.EXECUTED);
        second.setReceipt(receipt);

        assertThatThrownBy(() -> purchaseRepository.saveAndFlush(second))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void purchaseWithoutStatusIsRejected() {
        Position position = newPosition(newPortfolio(), UUID.randomUUID());

        assertThatThrownBy(() -> purchaseRepository.saveAndFlush(new Purchase(position, BigDecimal.ONE,
                BigDecimal.TEN, LocalDateTime.now(), null)))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void receiptNumberMustBeUnique() {
        newReceipt("RCP-20250101-0003");

        assertThatThrownBy(() -> newReceipt("RCP-20250101-0003"))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void receiptIsPersistedAndFoundByNumber() {
        PurchaseReceipt saved = newReceipt("RCP-20250101-0004");

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(receiptRepository.findByReceiptNumber("RCP-20250101-0004")).contains(saved);
        assertThat(receiptRepository.findByReceiptNumber("unknown")).isEmpty();
    }

    @Test
    void blankReceiptNumberIsRejected() {
        assertThatThrownBy(() -> newReceipt(" "))
                .isInstanceOf(ConstraintViolationException.class);
    }
}
