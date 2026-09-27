create table if not exists cartes_acces
(
id_carte TEXT
    constraint cartes_acces_pk
        PRIMARY KEY,
idul      TEXT NOT NULL,
role_securite TEXT
);

insert or ignore into cartes_acces (id_carte, idul, role_securite) values ('123456', 'jdoe', NULL),
                                                      ('789012', 'asmith', NULL),
                                                      ('345678', 'bjones', NULL),
                                                      ('384643', 'dgijr', 'AGENT-SECURITE'),
                                                      ('746291', 'emart', 'AGENT-SECURITE');