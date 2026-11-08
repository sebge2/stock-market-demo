package be.sgerard.demo.company.model;

/**
 *
 * Lifecycle status of a security.
 *
 */
public enum SecurityStatus {

  /**
   *
   * Security is listed and can be traded.
   *
   */
  LISTED,

  /**
   *
   * Trading in the security is temporarily suspended.
   *
   */
  SUSPENDED,

  /**
   *
   * Security is undergoing admission to trading.
   *
   */
  ADMISSION,

  /**
   *
   * Security has been delisted.
   *
   */
  DELISTED;
}
