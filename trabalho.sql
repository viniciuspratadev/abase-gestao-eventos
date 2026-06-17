
-- REESTRUTURAÇÃO DO BANCO DE DADOS

CREATE TABLE IF NOT EXISTS usuario (
    id_usuario SERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    senha_hash VARCHAR(60) NOT NULL,
    data_cadastro TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    status_aprov BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS usuario_produtor (
    id_usuario INTEGER PRIMARY KEY,
    cnpj CHAR(14) UNIQUE NOT NULL,
    razao_social VARCHAR(255) NOT NULL,
    nome_fantasia VARCHAR(255),
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS usuario_cliente (
    id_usuario INTEGER PRIMARY KEY,
    cpf CHAR(11) UNIQUE NOT NULL,
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS usuario_admin (
    id_usuario INTEGER PRIMARY KEY,
    nivel_acesso VARCHAR(20) NOT NULL DEFAULT 'suporte'
    CHECK (nivel_acesso IN ('admin','moderador','dev','suporte')),
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS site_vendas (
    id_site SERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    url_base VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS local_evento (
    id_local SERIAL PRIMARY KEY,
    nome VARCHAR(150),
    endereco VARCHAR(255),
    latitude DECIMAL(10,8) NOT NULL,
    longitude DECIMAL(11,8) NOT NULL,
    CHECK (latitude BETWEEN -90 AND 90),
    CHECK (longitude BETWEEN -180 AND 180)
);

CREATE TABLE IF NOT EXISTS evento (
    id_evento SERIAL PRIMARY KEY,
    url_evento VARCHAR(255) UNIQUE NOT NULL,
    nome VARCHAR(150) NOT NULL,
    data_evento DATE NOT NULL,
    hora_evento TIME NOT NULL,
    id_local INTEGER NOT NULL,
    id_site INTEGER NOT NULL,
    id_produtor INTEGER NOT NULL,
    status VARCHAR(20) DEFAULT 'ativo'
    CHECK (status IN ('ativo','cancelado','suspenso')),
    visualizacoes INTEGER DEFAULT 0,
    cliques_url INTEGER DEFAULT 0,
    FOREIGN KEY (id_local) REFERENCES local_evento(id_local),
    FOREIGN KEY (id_site) REFERENCES site_vendas(id_site),
    FOREIGN KEY (id_produtor) REFERENCES usuario_produtor(id_usuario) ON DELETE CASCADE
);

CREATE INDEX idx_data_evento ON evento(data_evento);
CREATE INDEX idx_local_geo ON local_evento(latitude, longitude);
CREATE INDEX idx_evento_produtor ON evento(id_produtor);

CREATE TABLE IF NOT EXISTS categoria (
    id_categ SERIAL PRIMARY KEY,
    nome_categoria VARCHAR(100) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS evento_categoria (
    id_evento INTEGER NOT NULL,
    id_categ INTEGER NOT NULL,
    PRIMARY KEY (id_evento, id_categ),
    FOREIGN KEY (id_evento) REFERENCES evento(id_evento) ON DELETE CASCADE,
    FOREIGN KEY (id_categ) REFERENCES categoria(id_categ) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS checkin (
    id_checkin SERIAL PRIMARY KEY,
    data_checkin TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    latitude_checkin DECIMAL(10,8) NOT NULL,
    longitude_checkin DECIMAL(11,8) NOT NULL,
    CHECK (latitude_checkin BETWEEN -90 AND 90),
    CHECK (longitude_checkin BETWEEN -180 AND 180),
    id_usuario INTEGER NOT NULL,
    id_evento INTEGER NOT NULL,
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) ON DELETE CASCADE,
    FOREIGN KEY (id_evento) REFERENCES evento(id_evento) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS avaliacao (
    id_avaliacao SERIAL PRIMARY KEY,
    nota INTEGER CHECK (nota >= 1 AND nota <= 5),
    comentario VARCHAR(300),
    data_avaliacao TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    id_checkin INTEGER UNIQUE NOT NULL,
    FOREIGN KEY (id_checkin) REFERENCES checkin(id_checkin) ON DELETE CASCADE
);

-- CARGA INICIAL

INSERT INTO usuario (email, senha_hash, status_aprov) VALUES
('admin@abase.com','hash',TRUE),
('admin2@abase.com','hash',TRUE),
('dev1@abase.com','hash',TRUE),
('dev2@abase.com','hash',TRUE),
('suporte@abase.com','hash',TRUE),
('produtor@abase.com','hash',TRUE),
('produtor2@abase.com','hash',TRUE),
('produtor3@abase.com','hash',TRUE),
('produtor4@abase.com','hash',TRUE),
('produtor5@abase.com','hash',TRUE),
('jose@abase.com','hash',TRUE),
('heitor@abase.com','hash',TRUE),
('alice@abase.com','hash',TRUE),
('jessica@abase.com','hash',TRUE),
('adalberto@abase.com','hash',TRUE);

INSERT INTO usuario_produtor VALUES
(6,'12345678000190','Produtor 1 LTDA','Produtor 1'),
(7,'23456789000101','Produtor 2 LTDA','Produtor 2'),
(8,'34567890100112','Produtor 3 LTDA','Produtor 3'),
(9,'45678901200123','Produtor 4 LTDA','Produtor 4'),
(10,'56789012300134','Produtor 5 LTDA','Produtor 5');

INSERT INTO usuario_cliente VALUES
(11,'12345678900'),
(12,'23456789000'),
(13,'34567890100'),
(14,'45678901200'),
(15,'56789012300');

INSERT INTO usuario_admin VALUES
(1,'admin'),
(2,'moderador'),
(3,'dev'),
(4,'dev'),
(5,'suporte');

INSERT INTO site_vendas (nome,url_base) VALUES
('Sympla','https://www.sympla.com.br/'),
('Eventbrite','https://www.eventbrite.com/'),
('Ingresso Rápido','https://www.ingressorapido.com.br/'),
('Ticketmaster','https://www.ticketmaster.com/'),
('Eventim','https://www.eventim.com.br/');

INSERT INTO local_evento (nome,endereco,latitude,longitude) VALUES
('Embrazado','Vitória',-20.29341,-40.29051),
('WineBeer27','Vitória',-20.28114,-40.30070),
('Brava','Guarapari',-20.68932,-40.46265),
('A Selva','Vitória',-20.30849,-40.33622),
('El Libertador','Vitória',-20.26977,-40.29382);

INSERT INTO evento (nome,url_evento,data_evento,hora_evento,id_local,id_site,id_produtor) VALUES
('Equilibrium','url1','2026-07-01','22:00',1,1,6),
('Carnaval','url2','2026-07-15','20:00',2,2,7),
('Festa do Sol','url3','2026-08-01','18:00',3,3,8),
('Noite Latina','url4','2026-08-15','21:00',4,4,9),
('Cerveja','url5','2026-09-01','17:00',5,5,10);

INSERT INTO categoria (nome_categoria) VALUES
('Rave'),('Carnaval'),('Techno'),('Rock'),('Gastronomia');

INSERT INTO evento_categoria VALUES
(1,1),(2,2),(3,3),(4,4),(5,5);

INSERT INTO checkin (latitude_checkin,longitude_checkin,id_usuario,id_evento) VALUES
(-20.29,-40.29,11,1),
(-20.28,-40.30,12,2),
(-20.68,-40.46,13,3),
(-20.30,-40.33,14,4),
(-20.26,-40.29,15,5);

INSERT INTO avaliacao (nota,comentario,id_checkin) VALUES
(5,'Muito bom',1),
(4,'Bom',2),
(5,'Ótimo',3),
(4,'Legal',4),
(5,'Perfeito',5);

-- CONSULTAS

-- Eventos com produtor e local
SELECT e.nome, u.email, l.nome, e.data_evento
FROM evento e
JOIN usuario_produtor up ON e.id_produtor = up.id_usuario
JOIN usuario u ON up.id_usuario = u.id_usuario
JOIN local_evento l ON e.id_local = l.id_local
ORDER BY e.data_evento;

-- Média de avaliações
SELECT e.nome, ROUND(AVG(a.nota),2)
FROM evento e
JOIN checkin c ON e.id_evento = c.id_evento
JOIN avaliacao a ON c.id_checkin = a.id_checkin
GROUP BY e.nome
ORDER BY AVG(a.nota) DESC;

-- Engajamento
SELECT nome, visualizacoes, cliques_url
FROM evento
ORDER BY visualizacoes DESC;

-- Checkins por evento
SELECT e.nome, COUNT(c.id_checkin)
FROM evento e
LEFT JOIN checkin c ON e.id_evento = c.id_evento
GROUP BY e.nome;

-- Eventos por site
SELECT sv.nome, COUNT(e.id_evento)
FROM site_vendas sv
JOIN evento e ON sv.id_site = e.id_site
GROUP BY sv.nome;

-- Histórico de usuário
SELECT u.email,e.nome,c.data_checkin
FROM usuario u
JOIN checkin c ON u.id_usuario = c.id_usuario
JOIN evento e ON c.id_evento = e.id_evento;

-- Avaliação por local
SELECT l.nome, ROUND(AVG(a.nota),2)
FROM local_evento l
JOIN evento e ON l.id_local = e.id_local
JOIN checkin c ON e.id_evento = c.id_evento
JOIN avaliacao a ON c.id_checkin = a.id_checkin
GROUP BY l.nome;

-- Ranking de eventos
SELECT e.nome, COUNT(a.id_avaliacao)
FROM evento e
JOIN checkin c ON e.id_evento = c.id_evento
JOIN avaliacao a ON c.id_checkin = a.id_checkin
GROUP BY e.nome
ORDER BY COUNT(a.id_avaliacao) DESC;