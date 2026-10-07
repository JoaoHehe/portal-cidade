-- V2: dados simulados básicos (o projeto é fictício!)
-- Usuários e ocorrências de teste entram mais tarde,
-- porque as senhas precisam ser geradas com hash na Etapa 2.

-- Departamentos
INSERT INTO departamento (nome, sigla) VALUES
                                           ('Secretaria de Obras e Infraestrutura', 'SEINFRA'),
                                           ('Iluminação Pública',                   'ILUMIN'),
                                           ('Limpeza Urbana',                       'LIMPURB'),
                                           ('Secretaria de Trânsito',               'TRANSIT'),
                                           ('Companhia de Saneamento',              'SANEAM'),
                                           ('Secretaria de Meio Ambiente',          'SEMAM');

-- Equipes (o departamento é buscado pela sigla, assim não dependemos dos ids)
INSERT INTO equipe (nome, departamento_id) VALUES
                                               ('Equipe Tapa-Buraco',        (SELECT id FROM departamento WHERE sigla = 'SEINFRA')),
                                               ('Equipe Calçadas e Praças',  (SELECT id FROM departamento WHERE sigla = 'SEINFRA')),
                                               ('Equipe Postes e Lâmpadas',  (SELECT id FROM departamento WHERE sigla = 'ILUMIN')),
                                               ('Equipe Coleta Especial',    (SELECT id FROM departamento WHERE sigla = 'LIMPURB')),
                                               ('Equipe Sinalização',        (SELECT id FROM departamento WHERE sigla = 'TRANSIT')),
                                               ('Equipe Esgoto e Galerias',  (SELECT id FROM departamento WHERE sigla = 'SANEAM')),
                                               ('Equipe Poda e Arborização', (SELECT id FROM departamento WHERE sigla = 'SEMAM'));

-- Categorias, com palavras-chave (vão alimentar a sugestão automática depois)
INSERT INTO categoria (nome, descricao, palavras_chave, gravidade_base, departamento_sugerido_id) VALUES
                                                                                                      ('Infraestrutura',
                                                                                                       'Buracos, calçadas quebradas, pontes e vias danificadas',
                                                                                                       'buraco,asfalto,calçada,calcada,rua,cratera,ponte,rachadura,desabamento',
                                                                                                       4, (SELECT id FROM departamento WHERE sigla = 'SEINFRA')),
                                                                                                      ('Iluminação',
                                                                                                       'Postes apagados, lâmpadas queimadas e fios soltos',
                                                                                                       'poste,lâmpada,lampada,luz,escuro,apagado,fio,iluminação,iluminacao',
                                                                                                       3, (SELECT id FROM departamento WHERE sigla = 'ILUMIN')),
                                                                                                      ('Limpeza',
                                                                                                       'Lixo acumulado, entulho e terrenos sujos',
                                                                                                       'lixo,entulho,sujeira,mato,coleta,caçamba,cacamba,terreno',
                                                                                                       2, (SELECT id FROM departamento WHERE sigla = 'LIMPURB')),
                                                                                                      ('Trânsito',
                                                                                                       'Semáforos, placas e sinalização',
                                                                                                       'semáforo,semaforo,placa,faixa,sinalização,sinalizacao,cruzamento,lombada,acidente',
                                                                                                       4, (SELECT id FROM departamento WHERE sigla = 'TRANSIT')),
                                                                                                      ('Saneamento',
                                                                                                       'Vazamentos, esgoto a céu aberto e bueiros entupidos',
                                                                                                       'esgoto,vazamento,bueiro,cano,água,agua,alagamento,entupido,fedor',
                                                                                                       5, (SELECT id FROM departamento WHERE sigla = 'SANEAM')),
                                                                                                      ('Meio Ambiente',
                                                                                                       'Árvores caídas, poluição e descarte irregular',
                                                                                                       'árvore,arvore,poda,queimada,poluição,poluicao,rio,fumaça,fumaca,desmatamento',
                                                                                                       3, (SELECT id FROM departamento WHERE sigla = 'SEMAM'));