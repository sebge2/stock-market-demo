package be.sgerard.demo.employee.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import be.sgerard.demo.employee.model.DepartmentStatus;
import be.sgerard.demo.employee.model.EmployeeStatus;
import be.sgerard.demo.employee.model.dto.DepartmentDto;
import be.sgerard.demo.employee.model.dto.DepartmentRequestDto;
import be.sgerard.demo.employee.model.dto.EmployeeRequestDto;
import be.sgerard.demo.employee.repository.EmployeeRepository;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
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

  /** Manager of the IT & Systems department in the local data. */
  private static final UUID MANAGER_ID = UUID.fromString("00000000-0000-4000-c000-000000000029");

  /** Service under test. */
  @Autowired
  private DepartmentService departmentService;

  /** IT department of the local data, managed by {@link #MANAGER_ID}. */
  private static final UUID IT_ID = UUID.fromString("00000000-0000-4000-b000-000000000001");

  /** Employee service. */
  @Autowired
  private EmployeeService employeeService;

  /**
   *
   * Frees the manager of the IT department, so that the manager can be assigned to a new department.
   *
   */
  @BeforeEach
  void freeManager() {
    departmentService.update(IT_ID, request("IT", null, null));
  }

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

  }

  /**
   *
   * A person manages at most one department and cannot leave the department they manage.
   *
   */
  @Test
  void personBelongsToASingleDepartment() {
    final DepartmentDto managed = departmentService.create(request("Managed", MANAGER_ID, null));

    assertThatThrownBy(() -> departmentService.create(request("Second", MANAGER_ID, null)))
        .isInstanceOf(ResponseStatusException.class);

    final EmployeeRequestDto move = new EmployeeRequestDto();
    move.setFirstName("S");
    move.setLastName("G");
    move.setEmail("mateusz.kowalski@gerardson.biz");
    move.setStatus(EmployeeStatus.ACTIVE);
    move.setDepartmentId(IT_ID);
    assertThatThrownBy(() -> employeeService.update(MANAGER_ID, move)).isInstanceOf(ResponseStatusException.class);

    move.setDepartmentId(managed.getId());
    assertThat(employeeService.update(MANAGER_ID, move).getDepartmentId()).isEqualTo(managed.getId());
  }
}
