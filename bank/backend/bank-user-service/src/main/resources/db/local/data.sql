INSERT INTO permission (id, code, description, created_at, updated_at)
VALUES ('00000000-0000-4000-8000-000000000001', 'ADMIN', 'Administrative access', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('00000000-0000-4000-8000-000000000002', 'ROLE_LIST', 'List and read roles', CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP),
       ('00000000-0000-4000-8000-000000000003', 'ROLE_CREATE', 'Create roles', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('00000000-0000-4000-8000-000000000004', 'ROLE_UPDATE', 'Update roles', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('00000000-0000-4000-8000-000000000005', 'ROLE_DELETE', 'Delete roles', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('00000000-0000-4000-8000-000000000006', 'PERMISSION_LIST', 'List and read permissions', CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP),
       ('00000000-0000-4000-8000-000000000007', 'PERMISSION_CREATE', 'Create permissions', CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP),
       ('00000000-0000-4000-8000-000000000008', 'PERMISSION_UPDATE', 'Update permissions', CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP),
       ('00000000-0000-4000-8000-000000000009', 'PERMISSION_DELETE', 'Delete permissions', CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP),
       ('00000000-0000-4000-8000-000000000010', 'USER_LIST', 'List and read users', CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP),
       ('00000000-0000-4000-8000-000000000011', 'USER_CREATE', 'Create users', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('00000000-0000-4000-8000-000000000012', 'USER_UPDATE', 'Update users', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('00000000-0000-4000-8000-000000000013', 'USER_DELETE', 'Delete users', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('00000000-0000-4000-8000-000000000014', 'EMPLOYEE_LIST', 'List and read employees', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('00000000-0000-4000-8000-000000000015', 'EMPLOYEE_CREATE', 'Create employees', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('00000000-0000-4000-8000-000000000016', 'EMPLOYEE_UPDATE', 'Update employees', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('00000000-0000-4000-8000-000000000017', 'EMPLOYEE_DELETE', 'Delete employees', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('00000000-0000-4000-8000-000000000018', 'DEPARTMENT_LIST', 'List and read departments', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('00000000-0000-4000-8000-000000000019', 'DEPARTMENT_CREATE', 'Create departments', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('00000000-0000-4000-8000-000000000020', 'DEPARTMENT_UPDATE', 'Update departments', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('00000000-0000-4000-8000-000000000021', 'DEPARTMENT_DELETE', 'Delete departments', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('00000000-0000-4000-8000-000000000022', 'CLIENT_LIST', 'List and read clients', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('00000000-0000-4000-8000-000000000023', 'CLIENT_CREATE', 'Create clients', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('00000000-0000-4000-8000-000000000024', 'CLIENT_UPDATE', 'Update clients', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('00000000-0000-4000-8000-000000000025', 'CLIENT_DELETE', 'Delete clients', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('00000000-0000-4000-8000-000000000026', 'PORTFOLIO_LIST', 'List and read portfolios', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('00000000-0000-4000-8000-000000000027', 'PORTFOLIO_CREATE', 'Create portfolios', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('00000000-0000-4000-8000-000000000028', 'PORTFOLIO_UPDATE', 'Update portfolios', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('00000000-0000-4000-8000-000000000029', 'PORTFOLIO_DELETE', 'Delete portfolios', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('00000000-0000-4000-8000-000000000030', 'PORTFOLIO_VALUATION_READ', 'Read portfolio valuations', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('00000000-0000-4000-8000-000000000031', 'QUOTATION_READ', 'Read stock quotations', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO role (id, name, description, status, created_at, updated_at)
VALUES ('00000000-0000-4000-9000-000000000001', 'ADMIN', 'Administrator role', 'ACTIVE', CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP);

INSERT INTO role_permission (role_id, permission_id)
VALUES ('00000000-0000-4000-9000-000000000001', '00000000-0000-4000-8000-000000000001'),
       ('00000000-0000-4000-9000-000000000001', '00000000-0000-4000-8000-000000000002'),
       ('00000000-0000-4000-9000-000000000001', '00000000-0000-4000-8000-000000000003'),
       ('00000000-0000-4000-9000-000000000001', '00000000-0000-4000-8000-000000000004'),
       ('00000000-0000-4000-9000-000000000001', '00000000-0000-4000-8000-000000000005'),
       ('00000000-0000-4000-9000-000000000001', '00000000-0000-4000-8000-000000000006'),
       ('00000000-0000-4000-9000-000000000001', '00000000-0000-4000-8000-000000000007'),
       ('00000000-0000-4000-9000-000000000001', '00000000-0000-4000-8000-000000000008'),
       ('00000000-0000-4000-9000-000000000001', '00000000-0000-4000-8000-000000000009'),
       ('00000000-0000-4000-9000-000000000001', '00000000-0000-4000-8000-000000000010'),
       ('00000000-0000-4000-9000-000000000001', '00000000-0000-4000-8000-000000000011'),
       ('00000000-0000-4000-9000-000000000001', '00000000-0000-4000-8000-000000000012'),
       ('00000000-0000-4000-9000-000000000001', '00000000-0000-4000-8000-000000000013'),
  ('00000000-0000-4000-9000-000000000001', '00000000-0000-4000-8000-000000000014'),
  ('00000000-0000-4000-9000-000000000001', '00000000-0000-4000-8000-000000000015'),
  ('00000000-0000-4000-9000-000000000001', '00000000-0000-4000-8000-000000000016'),
  ('00000000-0000-4000-9000-000000000001', '00000000-0000-4000-8000-000000000017'),
  ('00000000-0000-4000-9000-000000000001', '00000000-0000-4000-8000-000000000018'),
  ('00000000-0000-4000-9000-000000000001', '00000000-0000-4000-8000-000000000019'),
  ('00000000-0000-4000-9000-000000000001', '00000000-0000-4000-8000-000000000020'),
  ('00000000-0000-4000-9000-000000000001', '00000000-0000-4000-8000-000000000021'),
  ('00000000-0000-4000-9000-000000000001', '00000000-0000-4000-8000-000000000022'),
  ('00000000-0000-4000-9000-000000000001', '00000000-0000-4000-8000-000000000023'),
  ('00000000-0000-4000-9000-000000000001', '00000000-0000-4000-8000-000000000024'),
  ('00000000-0000-4000-9000-000000000001', '00000000-0000-4000-8000-000000000025'),
  ('00000000-0000-4000-9000-000000000001', '00000000-0000-4000-8000-000000000026'),
  ('00000000-0000-4000-9000-000000000001', '00000000-0000-4000-8000-000000000027'),
  ('00000000-0000-4000-9000-000000000001', '00000000-0000-4000-8000-000000000028'),
  ('00000000-0000-4000-9000-000000000001', '00000000-0000-4000-8000-000000000029'),
  ('00000000-0000-4000-9000-000000000001', '00000000-0000-4000-8000-000000000030'),
  ('00000000-0000-4000-9000-000000000001', '00000000-0000-4000-8000-000000000031');

-- Password: changeit (BCrypt) for every account below
-- Users referenced by the clients of the bank-client-service local data (same password, no role)
INSERT INTO user_account (id, username, full_name, email, password_hash)
VALUES ('72e91c48-68b6-4de7-9a40-21b9b6224101', 'alice.martin', 'Alice Martin', 'alice.martin@local.test',
        '$2a$10$CclDGOA7HDXSN6jsyNvyiu.dWqkMEAUjF.jZKDvvlle8GxXjipfse'),
       ('72e91c48-68b6-4de7-9a40-21b9b6224102', 'bob.dupont', 'Bob Dupont', 'bob.dupont@local.test',
        '$2a$10$CclDGOA7HDXSN6jsyNvyiu.dWqkMEAUjF.jZKDvvlle8GxXjipfse'),
       ('72e91c48-68b6-4de7-9a40-21b9b6224103', 'claire.lambert', 'Claire Lambert', 'claire.lambert@local.test',
        '$2a$10$CclDGOA7HDXSN6jsyNvyiu.dWqkMEAUjF.jZKDvvlle8GxXjipfse');

-- Users of the Bank employees (bank-employee-service local data, same password, no role)
INSERT INTO user_account (id, username, full_name, email, password_hash)
VALUES ('72e91c48-68b6-4de7-9a40-21b9b6224202', 'alexander.muller', 'Alexander Müller', 'alexander.muller@gerardson.biz', '$2a$10$CclDGOA7HDXSN6jsyNvyiu.dWqkMEAUjF.jZKDvvlle8GxXjipfse'),
       ('72e91c48-68b6-4de7-9a40-21b9b6224203', 'sophie.laurent', 'Sophie Laurent', 'sophie.laurent@gerardson.biz', '$2a$10$CclDGOA7HDXSN6jsyNvyiu.dWqkMEAUjF.jZKDvvlle8GxXjipfse'),
       ('72e91c48-68b6-4de7-9a40-21b9b6224204', 'claire.moreau', 'Claire Moreau', 'claire.moreau@gerardson.biz', '$2a$10$CclDGOA7HDXSN6jsyNvyiu.dWqkMEAUjF.jZKDvvlle8GxXjipfse'),
       ('72e91c48-68b6-4de7-9a40-21b9b6224205', 'pieter.janssen', 'Pieter Janssen', 'pieter.janssen@gerardson.biz', '$2a$10$CclDGOA7HDXSN6jsyNvyiu.dWqkMEAUjF.jZKDvvlle8GxXjipfse'),
       ('72e91c48-68b6-4de7-9a40-21b9b6224206', 'elena.garcia', 'Elena García', 'elena.garcia@gerardson.biz', '$2a$10$CclDGOA7HDXSN6jsyNvyiu.dWqkMEAUjF.jZKDvvlle8GxXjipfse'),
       ('72e91c48-68b6-4de7-9a40-21b9b6224207', 'marco.bianchi', 'Marco Bianchi', 'marco.bianchi@gerardson.biz', '$2a$10$CclDGOA7HDXSN6jsyNvyiu.dWqkMEAUjF.jZKDvvlle8GxXjipfse'),
       ('72e91c48-68b6-4de7-9a40-21b9b6224208', 'lukas.schneider', 'Lukas Schneider', 'lukas.schneider@gerardson.biz', '$2a$10$CclDGOA7HDXSN6jsyNvyiu.dWqkMEAUjF.jZKDvvlle8GxXjipfse'),
       ('72e91c48-68b6-4de7-9a40-21b9b6224209', 'oliver.fischer', 'Oliver Fischer', 'oliver.fischer@gerardson.biz', '$2a$10$CclDGOA7HDXSN6jsyNvyiu.dWqkMEAUjF.jZKDvvlle8GxXjipfse'),
       ('72e91c48-68b6-4de7-9a40-21b9b6224210', 'antoine.lefebvre', 'Antoine Lefebvre', 'antoine.lefebvre@gerardson.biz', '$2a$10$CclDGOA7HDXSN6jsyNvyiu.dWqkMEAUjF.jZKDvvlle8GxXjipfse'),
       ('72e91c48-68b6-4de7-9a40-21b9b6224211', 'sofia.almeida', 'Sofia Almeida', 'sofia.almeida@gerardson.biz', '$2a$10$CclDGOA7HDXSN6jsyNvyiu.dWqkMEAUjF.jZKDvvlle8GxXjipfse'),
       ('72e91c48-68b6-4de7-9a40-21b9b6224212', 'henrik.nielsen', 'Henrik Nielsen', 'henrik.nielsen@gerardson.biz', '$2a$10$CclDGOA7HDXSN6jsyNvyiu.dWqkMEAUjF.jZKDvvlle8GxXjipfse'),
       ('72e91c48-68b6-4de7-9a40-21b9b6224213', 'anna.nowak', 'Anna Nowak', 'anna.nowak@gerardson.biz', '$2a$10$CclDGOA7HDXSN6jsyNvyiu.dWqkMEAUjF.jZKDvvlle8GxXjipfse'),
       ('72e91c48-68b6-4de7-9a40-21b9b6224214', 'isabella.rossi', 'Isabella Rossi', 'isabella.rossi@gerardson.biz', '$2a$10$CclDGOA7HDXSN6jsyNvyiu.dWqkMEAUjF.jZKDvvlle8GxXjipfse'),
       ('72e91c48-68b6-4de7-9a40-21b9b6224215', 'francois.lambert', 'François Lambert', 'francois.lambert@gerardson.biz', '$2a$10$CclDGOA7HDXSN6jsyNvyiu.dWqkMEAUjF.jZKDvvlle8GxXjipfse'),
       ('72e91c48-68b6-4de7-9a40-21b9b6224216', 'marta.fernandez', 'Marta Fernández', 'marta.fernandez@gerardson.biz', '$2a$10$CclDGOA7HDXSN6jsyNvyiu.dWqkMEAUjF.jZKDvvlle8GxXjipfse'),
       ('72e91c48-68b6-4de7-9a40-21b9b6224217', 'erik.johansson', 'Erik Johansson', 'erik.johansson@gerardson.biz', '$2a$10$CclDGOA7HDXSN6jsyNvyiu.dWqkMEAUjF.jZKDvvlle8GxXjipfse'),
       ('72e91c48-68b6-4de7-9a40-21b9b6224218', 'julia.weber', 'Julia Weber', 'julia.weber@gerardson.biz', '$2a$10$CclDGOA7HDXSN6jsyNvyiu.dWqkMEAUjF.jZKDvvlle8GxXjipfse'),
       ('72e91c48-68b6-4de7-9a40-21b9b6224219', 'thomas.dubois', 'Thomas Dubois', 'thomas.dubois@gerardson.biz', '$2a$10$CclDGOA7HDXSN6jsyNvyiu.dWqkMEAUjF.jZKDvvlle8GxXjipfse'),
       ('72e91c48-68b6-4de7-9a40-21b9b6224220', 'camille.bernard', 'Camille Bernard', 'camille.bernard@gerardson.biz', '$2a$10$CclDGOA7HDXSN6jsyNvyiu.dWqkMEAUjF.jZKDvvlle8GxXjipfse'),
       ('72e91c48-68b6-4de7-9a40-21b9b6224221', 'matteo.ricci', 'Matteo Ricci', 'matteo.ricci@gerardson.biz', '$2a$10$CclDGOA7HDXSN6jsyNvyiu.dWqkMEAUjF.jZKDvvlle8GxXjipfse'),
       ('72e91c48-68b6-4de7-9a40-21b9b6224222', 'laura.hoffmann', 'Laura Hoffmann', 'laura.hoffmann@gerardson.biz', '$2a$10$CclDGOA7HDXSN6jsyNvyiu.dWqkMEAUjF.jZKDvvlle8GxXjipfse'),
       ('72e91c48-68b6-4de7-9a40-21b9b6224223', 'niels.de.vries', 'Niels de Vries', 'niels.de.vries@gerardson.biz', '$2a$10$CclDGOA7HDXSN6jsyNvyiu.dWqkMEAUjF.jZKDvvlle8GxXjipfse'),
       ('72e91c48-68b6-4de7-9a40-21b9b6224224', 'emma.van.dijk', 'Emma van Dijk', 'emma.van.dijk@gerardson.biz', '$2a$10$CclDGOA7HDXSN6jsyNvyiu.dWqkMEAUjF.jZKDvvlle8GxXjipfse'),
       ('72e91c48-68b6-4de7-9a40-21b9b6224225', 'lucas.martin', 'Lucas Martin', 'lucas.martin@gerardson.biz', '$2a$10$CclDGOA7HDXSN6jsyNvyiu.dWqkMEAUjF.jZKDvvlle8GxXjipfse'),
       ('72e91c48-68b6-4de7-9a40-21b9b6224226', 'giulia.conti', 'Giulia Conti', 'giulia.conti@gerardson.biz', '$2a$10$CclDGOA7HDXSN6jsyNvyiu.dWqkMEAUjF.jZKDvvlle8GxXjipfse'),
       ('72e91c48-68b6-4de7-9a40-21b9b6224227', 'andreas.bauer', 'Andreas Bauer', 'andreas.bauer@gerardson.biz', '$2a$10$CclDGOA7HDXSN6jsyNvyiu.dWqkMEAUjF.jZKDvvlle8GxXjipfse'),
       ('72e91c48-68b6-4de7-9a40-21b9b6224228', 'elise.peeters', 'Elise Peeters', 'elise.peeters@gerardson.biz', '$2a$10$CclDGOA7HDXSN6jsyNvyiu.dWqkMEAUjF.jZKDvvlle8GxXjipfse'),
       ('72e91c48-68b6-4de7-9a40-21b9b6224229', 'mateusz.kowalski', 'Mateusz Kowalski', 'mateusz.kowalski@gerardson.biz', '$2a$10$CclDGOA7HDXSN6jsyNvyiu.dWqkMEAUjF.jZKDvvlle8GxXjipfse'),
       ('72e91c48-68b6-4de7-9a40-21b9b6224230', 'felix.wagner', 'Felix Wagner', 'felix.wagner@gerardson.biz', '$2a$10$CclDGOA7HDXSN6jsyNvyiu.dWqkMEAUjF.jZKDvvlle8GxXjipfse'),
       ('72e91c48-68b6-4de7-9a40-21b9b6224231', 'hugo.petit', 'Hugo Petit', 'hugo.petit@gerardson.biz', '$2a$10$CclDGOA7HDXSN6jsyNvyiu.dWqkMEAUjF.jZKDvvlle8GxXjipfse'),
       ('72e91c48-68b6-4de7-9a40-21b9b6224232', 'lars.andersen', 'Lars Andersen', 'lars.andersen@gerardson.biz', '$2a$10$CclDGOA7HDXSN6jsyNvyiu.dWqkMEAUjF.jZKDvvlle8GxXjipfse'),
       ('72e91c48-68b6-4de7-9a40-21b9b6224233', 'eva.svensson', 'Eva Svensson', 'eva.svensson@gerardson.biz', '$2a$10$CclDGOA7HDXSN6jsyNvyiu.dWqkMEAUjF.jZKDvvlle8GxXjipfse'),
       ('72e91c48-68b6-4de7-9a40-21b9b6224234', 'daniel.novak', 'Daniel Novak', 'daniel.novak@gerardson.biz', '$2a$10$CclDGOA7HDXSN6jsyNvyiu.dWqkMEAUjF.jZKDvvlle8GxXjipfse');

-- Alexander Müller (CEO) is the only administrator
INSERT INTO user_account_role (user_id, role_id)
VALUES ('72e91c48-68b6-4de7-9a40-21b9b6224202', '00000000-0000-4000-9000-000000000001');

-- OAuth2 clients (public clients using the authorization code flow with PKCE)
INSERT INTO oauth_client (id, client_id, client_secret, authentication_method, require_proof_key,
                          require_authorization_consent, created_at, updated_at)
VALUES ('00000000-0000-4000-a000-000000000001', 'bank-frontend', NULL, 'none', TRUE, FALSE, CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP),
       ('00000000-0000-4000-a000-000000000002', 'swagger-ui', NULL, 'none', TRUE, FALSE, CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP),
       ('00000000-0000-4000-a000-000000000003', 'employee-swagger-ui', NULL, 'none', TRUE, FALSE, CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP),
       ('00000000-0000-4000-a000-000000000004', 'client-swagger-ui', NULL, 'none', TRUE, FALSE, CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP),
       ('00000000-0000-4000-a000-000000000005', 'portfolio-swagger-ui', NULL, 'none', TRUE, FALSE, CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP),
       ('00000000-0000-4000-a000-000000000006', 'valuation-swagger-ui', NULL, 'none', TRUE, FALSE, CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP);

INSERT INTO oauth_client_grant_type (oauth_client_id, grant_type)
VALUES ('00000000-0000-4000-a000-000000000001', 'authorization_code'),
       ('00000000-0000-4000-a000-000000000001', 'refresh_token'),
       ('00000000-0000-4000-a000-000000000002', 'authorization_code'),
  ('00000000-0000-4000-a000-000000000003', 'authorization_code'),
  ('00000000-0000-4000-a000-000000000004', 'authorization_code'),
  ('00000000-0000-4000-a000-000000000005', 'authorization_code'),
  ('00000000-0000-4000-a000-000000000006', 'authorization_code');

INSERT INTO oauth_client_redirect_uri (oauth_client_id, redirect_uri)
VALUES ('00000000-0000-4000-a000-000000000001', 'http://localhost:4200/login/oauth2/code/bank'),
       ('00000000-0000-4000-a000-000000000001', 'http://localhost:3000/auth/callback'),
       ('00000000-0000-4000-a000-000000000002', 'http://localhost:9000/swagger-ui/oauth2-redirect.html'),
  ('00000000-0000-4000-a000-000000000003', 'http://localhost:8083/api/employee/swagger-ui/oauth2-redirect.html'),
  ('00000000-0000-4000-a000-000000000004', 'http://localhost:8084/api/client/swagger-ui/oauth2-redirect.html'),
  ('00000000-0000-4000-a000-000000000005', 'http://localhost:8085/api/portfolio/swagger-ui/oauth2-redirect.html'),
  ('00000000-0000-4000-a000-000000000006', 'http://localhost:8088/api/valuation/swagger-ui/oauth2-redirect.html');

INSERT INTO oauth_client_scope (oauth_client_id, scope)
VALUES ('00000000-0000-4000-a000-000000000001', 'openid'),
       ('00000000-0000-4000-a000-000000000001', 'profile'),
       ('00000000-0000-4000-a000-000000000001', 'user.read'),
       ('00000000-0000-4000-a000-000000000001', 'employee.read'),
       ('00000000-0000-4000-a000-000000000002', 'user.read'),
  ('00000000-0000-4000-a000-000000000003', 'employee.read'),
  ('00000000-0000-4000-a000-000000000004', 'client.read'),
  ('00000000-0000-4000-a000-000000000005', 'portfolio.read'),
  ('00000000-0000-4000-a000-000000000006', 'valuation.read');
