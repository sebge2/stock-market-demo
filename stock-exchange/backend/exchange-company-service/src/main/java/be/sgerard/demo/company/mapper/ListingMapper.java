package be.sgerard.demo.company.mapper;

import be.sgerard.demo.company.model.Listing;
import be.sgerard.demo.company.model.dto.ListingDto;
import org.springframework.stereotype.Component;

/**
 *
 * Maps listings to their DTO representation.
 *
 */
@Component
public class ListingMapper {

  /**
   *
   * Converts a listing to its DTO.
   *
   * @param listing listing to convert
   * @return the DTO
   */
  public ListingDto toDto(Listing listing) {
    return new ListingDto(listing.getId(), listing.getSecurity().getId(), listing.getExchange().getId(),
        listing.getTicker(), listing.getTradingCurrency(), listing.getListingDate(), listing.getDelistingDate(),
        listing.getStatus(), listing.isPrimary());
  }
}
