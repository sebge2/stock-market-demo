-- user_id values reference users of the bank-user-service local data
INSERT INTO client (id, first_name, last_name, user_id, created_at, updated_at)
VALUES ('00000000-0000-4000-d000-000000000001', 'Alice', 'Martin', '72e91c48-68b6-4de7-9a40-21b9b6224101',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('00000000-0000-4000-d000-000000000002', 'Bob', 'Dupont', '72e91c48-68b6-4de7-9a40-21b9b6224102',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('00000000-0000-4000-d000-000000000003', 'Claire', 'Lambert', '72e91c48-68b6-4de7-9a40-21b9b6224103',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
