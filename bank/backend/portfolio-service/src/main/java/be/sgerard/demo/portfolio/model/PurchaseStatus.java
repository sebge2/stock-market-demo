package be.sgerard.demo.portfolio.model;

/**
 *
 * Lifecycle status of a {@link Purchase}.
 *
 */
public enum PurchaseStatus {
    /** Waiting for execution. */
    PENDING,
    /** Executed. */
    EXECUTED,
    /** Cancelled. */
    CANCELLED
}
