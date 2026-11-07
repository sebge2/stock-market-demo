package be.sgerard.demo.employee.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import be.sgerard.demo.employee.model.DepartmentStatus;
import be.sgerard.demo.employee.model.dto.DepartmentDto;
import be.sgerard.demo.employee.model.dto.DepartmentRequestDto;
import be.sgerard.demo.employee.repository.EmployeeRepository;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 *
 * Verifies the hierarchy of departments: the manager of a department reports to the manager of its parent.
 *
 */
@SpringBootTest
@ActiveProfiles("local")
@Transactional
class DepartmentHierarchyTest {

  /** Manager of the IT department in the local data. */
  private static final UUID MANAGER_ID = UUID.fromString("00000000-0000-4000-c000-000000000001");

  /** Service under test. */
  @Autowired
  private DepartmentService departmentService;

  /**
   *
   * Builds a department request.
   *
   * @param name department name
   * @param managerId manager identifier, if any
   * @param parentId parent identifier, if any
   * @return the request
   *
   */
  private DepartmentRequestDto request(String name, UUID managerId, UUID parentId) {
    final DepartmentRequestDto request = new DepartmentRequestDto();
    request.setName(name);
    request.setStatus(DepartmentStatus.ACTIVE);
    request.setManagerId(managerId);
    request.setParentId(parentId);
    return request;
  }

  /**
   *
   * A sub-department keeps its own manager and exposes its parent.
   *
   */
  @Test
  void subDepartmentsKeepTheirOwnManager() {
    final DepartmentDto parent = departmentService.create(request("Parent", MANAGER_ID, null));
    final DepartmentDto child = departmentService.create(request("Child", null, parent.getId()));

    assertThat(child.getParentId()).isEqualTo(parent.getId());
    assertThat(child.getManagerId()).isNull();
    assertThat(departmentService.findById(parent.getId()).getManagerId()).isEqualTo(MANAGER_ID);
  }

  /**
   *
   * A department cannot become a descendant of itself, nor be deleted while it has sub-departments.
   *
   */
  @Test
  void keepsTheHierarchyConsistent() {
    final DepartmentDto parent = departmentService.create(request("Parent", null, null));
    final DepartmentDto child = departmentService.create(request("Child", null, parent.getId()));

    assertThatThrownBy(() -> departmentService.update(parent.getId(), request("Parent", null, child.getId())))
        .isInstanceOf(ResponseStatusException.class);
    assertThatThrownBy(() -> departmentService.delete(parent.getId())).isInstanceOf(ResponseStatusException.class);
  }

  /** Employee repository. */
  @Autowired
  private EmployeeRepository employeeRepository;

  /**
   *
   * The manager of a department becomes a member of that department.
   *
   */
  @Test
  void managerJoinsTheDepartment() {
    final DepartmentDto created = departmentService.create(request("Managed", MANAGER_ID, null));

    assertThat(employeeRepository.findById(MANAGER_ID).orElseThrow().getDepartment().getId()).isEqualTo(created.getId());

    final DepartmentDto other = departmentService.create(request("Other", null, null));
    departmentService.update(other.getId(), request("Other", MANAGER_ID, null));

    assertThat(employeeRepository.findById(MANAGER_ID).orElseThrow().getDepartment().getId()).isEqualTo(other.getId());
  }

  /**
   *
   * A person manages at most one department and cannot leave the department they manage.
   *
   */
  @Test
  void personBelongsToASingleDepartment() {
    departmentService.create(request("Managed", MANAGER_ID, null));

    assertThatThrownBy(() -> departmentService.create(request("Second", MANAGER_ID, null)))
        .isInstanceOf(ResponseStatusException.class);
  }
}
