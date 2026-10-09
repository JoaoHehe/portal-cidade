-- V3: tabelas das ocorrências urbanas
-- (lembrete: V1 e V2 não se mexem mais. Mudanças sempre em arquivo novo!)

-- Gerador dos números de protocolo (1, 2, 3...).
-- O Java vai usar isso para montar protocolos tipo "2026-000001".
CREATE SEQUENCE protocolo_seq START 1;

-- Onde o problema está
CREATE TABLE localizacao (
                             id            BIGSERIAL PRIMARY KEY,
                             latitude      DOUBLE PRECISION NOT NULL CHECK (latitude  BETWEEN -90  AND 90),
                             longitude     DOUBLE PRECISION NOT NULL CHECK (longitude BETWEEN -180 AND 180),
                             endereco      VARCHAR(255),
                             bairro        VARCHAR(100) NOT NULL,
    -- TRUE se for perto de escola, hospital etc. (vai aumentar a prioridade)
                             local_critico BOOLEAN NOT NULL DEFAULT FALSE
);

-- A ocorrência em si
CREATE TABLE ocorrencia (
                            id               BIGSERIAL PRIMARY KEY,
                            protocolo        VARCHAR(20)  NOT NULL UNIQUE,    -- ex: 2026-000001
                            titulo           VARCHAR(120) NOT NULL,
                            descricao        TEXT NOT NULL,
                            categoria_id     BIGINT NOT NULL REFERENCES categoria(id),
                            cidadao_id       BIGINT NOT NULL REFERENCES usuario(id),
    -- UNIQUE: cada localização pertence a uma única ocorrência
                            localizacao_id   BIGINT NOT NULL UNIQUE REFERENCES localizacao(id),
                            status           VARCHAR(20) NOT NULL DEFAULT 'RECEBIDO'
                                CHECK (status IN ('RECEBIDO', 'EM_ANALISE', 'ENCAMINHADO',
                                                  'EM_EXECUCAO', 'RESOLVIDO')),
                            prioridade       VARCHAR(10) NOT NULL DEFAULT 'MEDIA'
                                CHECK (prioridade IN ('BAIXA', 'MEDIA', 'ALTA', 'URGENTE')),
                            gravidade        INT NOT NULL DEFAULT 3 CHECK (gravidade BETWEEN 1 AND 5),
                            impacto          INT NOT NULL DEFAULT 3 CHECK (impacto   BETWEEN 1 AND 5),
                            score_prioridade INT NOT NULL DEFAULT 0,          -- pontuação calculada (Etapa 4)
                            criado_em        TIMESTAMP NOT NULL DEFAULT NOW(),
                            atualizado_em    TIMESTAMP NOT NULL DEFAULT NOW(),
                            resolvido_em     TIMESTAMP                        -- só é preenchido ao resolver
);

-- Fotos da ocorrência (antes e depois do conserto)
CREATE TABLE foto (
                      id            BIGSERIAL PRIMARY KEY,
    -- ON DELETE CASCADE: se a ocorrência for apagada, as fotos vão junto
                      ocorrencia_id BIGINT NOT NULL REFERENCES ocorrencia(id) ON DELETE CASCADE,
                      caminho       VARCHAR(255) NOT NULL,              -- onde o arquivo está salvo
                      tipo          VARCHAR(10) NOT NULL DEFAULT 'ANTES'
                          CHECK (tipo IN ('ANTES', 'DEPOIS')),
                      enviado_em    TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Diário de mudanças de status: quem mudou, de quê, para quê e quando
CREATE TABLE historico_status (
                                  id              BIGSERIAL PRIMARY KEY,
                                  ocorrencia_id   BIGINT NOT NULL REFERENCES ocorrencia(id) ON DELETE CASCADE,
                                  status_anterior VARCHAR(20),                      -- vazio no primeiro registro
                                  status_novo     VARCHAR(20) NOT NULL,
                                  usuario_id      BIGINT NOT NULL REFERENCES usuario(id),
                                  observacao      VARCHAR(500),
                                  criado_em       TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Índices: deixam as buscas e filtros mais rápidos
CREATE INDEX idx_ocorrencia_status     ON ocorrencia(status);
CREATE INDEX idx_ocorrencia_prioridade ON ocorrencia(prioridade);
CREATE INDEX idx_ocorrencia_categoria  ON ocorrencia(categoria_id);
CREATE INDEX idx_ocorrencia_cidadao    ON ocorrencia(cidadao_id);
CREATE INDEX idx_ocorrencia_criado_em  ON ocorrencia(criado_em);
CREATE INDEX idx_localizacao_bairro    ON localizacao(bairro);
CREATE INDEX idx_localizacao_lat_lng   ON localizacao(latitude, longitude);
CREATE INDEX idx_historico_ocorrencia  ON historico_status(ocorrencia_id);