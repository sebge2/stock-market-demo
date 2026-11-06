package be.sgerard.demo.exchange.user.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 *
 * Payload used to create or update a permission.
 *
 */
@Schema(name = "PermissionRequest")
public class PermissionRequestDto {

  /**
   * Permission code.
   */
  @NotBlank
  @Size(max = 100)
  @Schema(description = "Permission code")
  private String code;

  /**
   * Permission description.
   */
  @Size(max = 500)
  @Schema(description = "Permission description")
  private String description;

  /**
   * @return the permission code
   */
  public String getCode() { return code; }

  /**
   * @param code the permission code
   */
  public void setCode(String code) { this.code = code; }

  /**
   * @return the permission description
   */
  public String getDescription() { return description; }

  /**
   * @param description the permission description
   */
  public void setDescription(String description) { this.description = description; }
}
