TRUNCATE TABLE order_entity CASCADE;
TRUNCATE TABLE partner_entity CASCADE;

INSERT INTO partner_entity (id, name, credit_available)
VALUES ('11111111-1111-1111-1111-111111111111', 'Parceiro Teste', 1000.00);

INSERT INTO order_entity (id, partner_id, total_value, status, created_at, updated_at)
VALUES
('22222222-2222-2222-2222-222222222222', '11111111-1111-1111-1111-111111111111', 250.00, 'PENDENTE', NOW(), NOW());