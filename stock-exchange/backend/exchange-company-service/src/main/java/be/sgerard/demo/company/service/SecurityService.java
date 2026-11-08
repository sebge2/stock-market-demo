package be.sgerard.demo.company.service;

import be.sgerard.demo.company.mapper.SecurityMapper;
import be.sgerard.demo.company.model.Company;
import be.sgerard.demo.company.model.Security;
import be.sgerard.demo.company.model.dto.SecurityDto;
import be.sgerard.demo.company.model.dto.SecurityRequestDto;
import be.sgerard.demo.company.repository.CompanyRepository;
import be.sgerard.demo.company.repository.SecurityRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 *
 * Business logic of the securities: ISIN and ticker are unique on the single exchange, and the issuer of a
 * security cannot change once created.
 *
 */
@Service
@Transactional(readOnly = true)
public class SecurityService {

  /**
   *
   * Security repository.
   *
   */
  private final SecurityRepository securityRepository;

  /**
   *
   * Company repository.
   *
   */
  private final CompanyRepository companyRepository;

  /**
   *
   * Security mapper.
   *
   */
  private final SecurityMapper securityMapper;

  /**
   *
   * Creates the service.
   *
   * @param securityRepository the security repository
   * @param companyRepository the company repository
   * @param securityMapper the security mapper
   */
  public SecurityService(final SecurityRepository securityRepository, final CompanyRepository companyRepository,
      final SecurityMapper securityMapper) {
    this.securityRepository = securityRepository;
    this.companyRepository = companyRepository;
    this.securityMapper = securityMapper;
  }

  /**
   *
   * Lists the securities, optionally restricted to the ones of a company.
   *
   * @param companyId identifier of the issuing company, or {@code null} for all the securities
   * @return the securities
   */
  public List<SecurityDto> findAll(final UUID companyId) {
    final List<Security> securities = companyId == null ? securityRepository.findAll()
        : securityRepository.findByCompanyId(companyId);
    return securities.stream().map(securityMapper::toDto).toList();
  }

  /**
   *
   * Finds a security.
   *
   * @param id security identifier
   * @return the security
   */
  public SecurityDto findById(final UUID id) {
    return securityMapper.toDto(findEntity(id));
  }

  /**
   *
   * Creates a security.
   *
   * @param request the security data
   * @return the created security
   */
  @Transactional
  public SecurityDto create(final SecurityRequestDto request) {
    ensureUnique(request, null);
    final Company company = companyRepository.findById(request.companyId()).orElseThrow(
        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found: " + request.companyId()));
    return securityMapper.toDto(securityRepository.saveAndFlush(securityMapper.toEntity(request, company)));
  }

  /**
   *
   * Updates a security.
   *
   * @param id security identifier
   * @param request the security data
   * @return the updated security
   */
  @Transactional
  public SecurityDto update(final UUID id, final SecurityRequestDto request) {
    final Security security = findEntity(id);
    if (!security.getCompany().getId().equals(request.companyId())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The issuing company of a security cannot change");
    }
    ensureUnique(request, id);
    securityMapper.updateEntity(request, security);
    return securityMapper.toDto(securityRepository.saveAndFlush(security));
  }

  /**
   *
   * Deletes a security.
   *
   * @param id security identifier
   */
  @Transactional
  public void delete(final UUID id) {
    securityRepository.delete(findEntity(id));
    securityRepository.flush();
  }

  /**
   *
   * Finds a security entity.
   *
   * @param id security identifier
   * @return the entity
   */
  private Security findEntity(final UUID id) {
    return securityRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Security not found: " + id));
  }

  /**
   *
   * Checks the share counts, and makes sure the ISIN and the ticker are not used by another security.
   *
   * @param request the security data
   * @param currentId identifier of the edited security, or {@code null} when creating
   */
  private void ensureUnique(final SecurityRequestDto request, final UUID currentId) {
    if (request.issuedShares() != null && request.outstandingShares() != null
        && request.outstandingShares() > request.issuedShares()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Outstanding shares exceed the issued shares");
    }
    securityRepository.findByIsin(request.isin()).filter(s -> !s.getId().equals(currentId)).ifPresent(s -> {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "ISIN already used: " + request.isin());
    });
    securityRepository.findByTicker(request.ticker()).filter(s -> !s.getId().equals(currentId)).ifPresent(s -> {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Ticker already used: " + request.ticker());
    });
  }
}
