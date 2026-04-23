-- ÍNDICES (PERFORMANCE)
-- =========================================
CREATE INDEX idx_usuario_empresa ON usuario(empresa_id);
CREATE INDEX idx_produto_empresa ON produto(empresa_id);
CREATE INDEX idx_pedido_empresa ON pedido(empresa_id);
CREATE INDEX idx_item_pedido_pedido ON item_pedido(pedido_id);