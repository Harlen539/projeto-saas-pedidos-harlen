ALTER TABLE produto
    ALTER COLUMN preco TYPE NUMERIC(19,2) USING preco::numeric(19,2);

ALTER TABLE item_pedido
    ALTER COLUMN preco TYPE NUMERIC(19,2) USING preco::numeric(19,2);
