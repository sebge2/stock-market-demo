INSERT INTO department (id, name, description, status, created_at, updated_at)
VALUES ('00000000-0000-4000-b000-000000000001', 'IT', 'IT Support', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- user_id matches the administrator of the bank-user-service local data
INSERT INTO employee (id, first_name, last_name, email, user_id, status, department_id, created_at, updated_at)
VALUES ('00000000-0000-4000-c000-000000000001', 'Sébastien', 'Gérard', 'sebastien.gerard@local.test',
        '72e91c48-68b6-4de7-9a40-21b9b6224001', 'ACTIVE', '00000000-0000-4000-b000-000000000001', CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP);
