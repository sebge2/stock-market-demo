package be.sgerard.demo.company.model;

/**
 *
 * Lifecycle status of a company.
 *
 */
public enum CompanyStatus {

  /**
   *
   * Company is listed and can be traded.
   *
   */
  LISTED,

  /**
   *
   * Trading in the company is temporarily suspended.
   *
   */
  SUSPENDED,

  /**
   *
   * Company is undergoing admission to listing.
   *
   */
  ADMISSION,

  /**
   *
   * Company has been delisted.
   *
   */
  DELISTED;
}
