-- V1: tabelas base do Portal do Cidadão
-- Regra do Flyway: depois que este arquivo rodar, ele NUNCA mais é editado.
-- Mudanças futuras vão em arquivos novos (V2, V3...).

-- Departamentos da prefeitura (Obras, Iluminação, etc.)
CREATE TABLE departamento (
                              id    BIGSERIAL PRIMARY KEY,
                              nome  VARCHAR(100) NOT NULL UNIQUE,
                              sigla VARCHAR(10)  NOT NULL UNIQUE
);

-- Equipes de cada departamento
CREATE TABLE equipe (
                        id              BIGSERIAL PRIMARY KEY,
                        nome            VARCHAR(100) NOT NULL,
                        departamento_id BIGINT NOT NULL REFERENCES departamento(id)
);

-- Usuários: cidadãos e funcionários ficam na mesma tabela
CREATE TABLE usuario (
                         id              BIGSERIAL PRIMARY KEY,
                         nome            VARCHAR(120) NOT NULL,
                         email           VARCHAR(150) NOT NULL UNIQUE,
                         senha_hash      VARCHAR(100) NOT NULL,   -- não guardar nunca a senha pura seu mzr!
                         perfil          VARCHAR(20)  NOT NULL
                             CHECK (perfil IN ('CIDADAO', 'FUNCIONARIO', 'ADMIN')),
                         departamento_id BIGINT REFERENCES departamento(id),  -- só para funcionários
                         criado_em       TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Categorias das ocorrências (infraestrutura, iluminação...)
CREATE TABLE categoria (
                           id                       BIGSERIAL PRIMARY KEY,
                           nome                     VARCHAR(80) NOT NULL UNIQUE,
                           descricao                VARCHAR(255),
    -- palavras separadas por vírgula, usadas depois para sugerir a categoria
                           palavras_chave           VARCHAR(500) NOT NULL DEFAULT '',
    -- gravidade padrão (1 a 5), entra no cálculo de prioridade
                           gravidade_base           INT NOT NULL DEFAULT 3
                               CHECK (gravidade_base BETWEEN 1 AND 5),
                           departamento_sugerido_id BIGINT REFERENCES departamento(id)
);