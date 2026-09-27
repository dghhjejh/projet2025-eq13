CREATE TABLE IF NOT EXISTS Users
(
    idul       TEXT PRIMARY KEY,
    name       TEXT NOT NULL,
    contact_no TEXT NOT NULL,
    status     TEXT NOT NULL
);

INSERT OR IGNORE INTO Users (idul, name, contact_no, status)
VALUES ('jdoe', 'John Doe', '+15145551234', 'AVAILABLE'),
       ('asmith', 'Alice Smith', '+15145554321', 'IN_A_MEETING'),
       ('bjones', 'Bob Jones', '+15145559876', 'OUT_OF_OFFICE');