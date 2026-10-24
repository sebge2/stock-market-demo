package com.stockmarket.bank.portfolio.domain;

/**
 * Lifecycle status of a {@link Purchase}.
 */
public enum PurchaseStatus {
    /** Waiting for execution. */
    PENDING,
    /** Executed. */
    EXECUTED,
    /** Cancelled. */
    CANCELLED
}
