create table if not exists Buildings
(
    building_id        TEXT not null
        constraint building_id
            primary key
);

create table if not exists Zones
(
    zone_id      TEXT not null
        constraint zone_id
            primary key,
    building_id  TEXT not null
        constraint building_id
            references Buildings
);

create table if not exists DoorStates
(
    door_id        TEXT    not null
        constraint door_id
            primary key,
    zone_id  TEXT not null
        constraint zone_id
            references Zones,
    is_locked      boolean not null,
    initial_locked boolean not null,
    is_open        boolean not null,
    initial_open   boolean not null
);


create table if not exists AgentRequests
(
    intervention_id TEXT not null
        constraint intervention_id
            primary key,
    agent_id TEXT,
    priority  TEXT  not null,
    zone_id         TEXT not null
        constraint zone_id
            references Zones,
    room_id TEXT
);

create table if not exists AgentsDeployed
(
    intervention_id TEXT not null
        constraint intervention_id
            primary key,
    agent_id TEXT,
    priority  TEXT  not null,
    zone_id         TEXT
        constraint zone_id
            references Zones,
    room_id TEXT
);

create table if not exists AgentsArrived
(
    intervention_id TEXT not null
        constraint intervention_id
            primary key,
    agent_id TEXT,
    priority  TEXT  not null,
    zone_id         TEXT
        constraint zone_id
            references Zones,
    room_id TEXT
);

create table if not exists Gatherings
(
    gathering_id TEXT not null
        constraint gathering_id
            primary key,
    expected_attendees integer default 0 not null,
    gathering_type     TEXT              not null,
    zone_id            TEXT
        constraint zone_id
            references Zones
);

create table if not exists VentilationSystems
(
    zone_id              TEXT not null
        constraint zone_id
            primary key
        constraint zone_id
            references Zones,
    vitesse_retour       integer,
    vitesse_distribution integer,
    is_open              integer
);

create table if not exists ZoneStates
(
    zone_id             TEXT                    not null
        constraint zone_id
            primary key
        constraint zone_id
            references Zones,
    urgency_state       TEXT    default 'AUCUN' not null,
    are_alarms_active   boolean default false   not null,
    smoke_concentration integer default 0       not null
);

create table if not exists RoomOccupation
(
    room_id             TEXT    not null
    constraint room_id
    primary key,
    current_occupation  integer default 0       not null,
    supports_counting   boolean default false   not null
);

create table if not exists RoomOccupants
(
    room_id              TEXT    not null,
    idul                 TEXT    not null,
    consecutive_accesses integer default 1      not null,
    constraint room_occupants_pk
    primary key (room_id, idul),
    constraint room_id_fk
    foreign key (room_id) references RoomOccupation(room_id)
    on delete cascade
    );

