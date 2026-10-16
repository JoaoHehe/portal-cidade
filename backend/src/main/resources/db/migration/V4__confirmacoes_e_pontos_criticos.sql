-- V4: tabelas das regras inteligentes (Etapa 4)

-- Confirmações: cidadãos que dizem "esse problema também me afeta".
-- Servem para contar quantas pessoas confirmaram uma ocorrência.
CREATE TABLE confirmacao (
                             id            BIGSERIAL PRIMARY KEY,
                             ocorrencia_id BIGINT NOT NULL REFERENCES ocorrencia(id) ON DELETE CASCADE,
                             usuario_id    BIGINT NOT NULL REFERENCES usuario(id),
                             criado_em     TIMESTAMP NOT NULL DEFAULT NOW(),
    -- cada cidadão só pode confirmar a mesma ocorrência UMA vez
                             UNIQUE (ocorrencia_id, usuario_id)
);

CREATE INDEX idx_confirmacao_ocorrencia ON confirmacao(ocorrencia_id);

-- Pontos críticos da cidade: lugares onde um problema é mais grave
-- (perto de escola, hospital...). Se a ocorrência estiver perto de um deles,
-- a prioridade sobe. Os lugares abaixo são todos FICTÍCIOS.
CREATE TABLE ponto_critico (
                               id        BIGSERIAL PRIMARY KEY,
                               nome      VARCHAR(120) NOT NULL,
                               tipo      VARCHAR(30)  NOT NULL
                                   CHECK (tipo IN ('ESCOLA', 'HOSPITAL', 'CRECHE', 'TERMINAL')),
                               latitude  DOUBLE PRECISION NOT NULL,
                               longitude DOUBLE PRECISION NOT NULL
);

INSERT INTO ponto_critico (nome, tipo, latitude, longitude) VALUES
                                                                ('Escola Municipal Machado de Assis',  'ESCOLA',   -8.0505, -34.8805),
                                                                ('Hospital Municipal Boa Vida',        'HOSPITAL', -8.0620, -34.8710),
                                                                ('Creche Pequenos Passos',             'CRECHE',   -8.0450, -34.8900),
                                                                ('Terminal Integrado Central',         'TERMINAL', -8.0580, -34.8760);