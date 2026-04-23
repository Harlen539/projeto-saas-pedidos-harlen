-- DADOS DE TESTE
-- =========================================
INSERT INTO empresa (nome, cnpj)
VALUES 
('Doceria da Maria', '123456789');

INSERT INTO usuario (nome, email, senha, role, empresa_id)
VALUES
('Admin Maria', 'admin@doceria.com', '123', 'ADMIN', 1);

INSERT INTO produto (nome, preco, estoque, empresa_id)
VALUES
('Brigadeiro', 5.00, 100, 1),
('Beijinho', 4.50, 80, 1);