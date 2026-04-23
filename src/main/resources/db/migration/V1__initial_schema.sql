CREATE TABLE IF NOT EXISTS empresa (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    cnpj VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS usuario (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL,
    senha VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'MEMBER',
    empresa_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_usuario_empresa FOREIGN KEY (empresa_id) REFERENCES empresa (id)
);

CREATE TABLE IF NOT EXISTS produto (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    preco DOUBLE PRECISION NOT NULL,
    empresa_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_produto_empresa FOREIGN KEY (empresa_id) REFERENCES empresa (id)
);

CREATE TABLE IF NOT EXISTS pedido (
    id BIGSERIAL PRIMARY KEY,
    data DATE NOT NULL,
    empresa_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_pedido_empresa FOREIGN KEY (empresa_id) REFERENCES empresa (id)
);

CREATE TABLE IF NOT EXISTS item_pedido (
    id BIGSERIAL PRIMARY KEY,
    quantidade INTEGER NOT NULL,
    preco DOUBLE PRECISION NOT NULL,
    pedido_id BIGINT NOT NULL,
    produto_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_item_pedido_pedido FOREIGN KEY (pedido_id) REFERENCES pedido (id) ON DELETE CASCADE,
    CONSTRAINT fk_item_pedido_produto FOREIGN KEY (produto_id) REFERENCES produto (id)
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_empresa_cnpj ON empresa (cnpj);
CREATE UNIQUE INDEX IF NOT EXISTS uk_usuario_email ON usuario (email);
CREATE INDEX IF NOT EXISTS idx_usuario_empresa_id ON usuario (empresa_id);
CREATE INDEX IF NOT EXISTS idx_produto_empresa_id ON produto (empresa_id);
CREATE INDEX IF NOT EXISTS idx_pedido_empresa_id ON pedido (empresa_id);
CREATE INDEX IF NOT EXISTS idx_item_pedido_pedido_id ON item_pedido (pedido_id);
CREATE INDEX IF NOT EXISTS idx_item_pedido_produto_id ON item_pedido (produto_id);
