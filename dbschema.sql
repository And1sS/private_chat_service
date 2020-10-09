CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS private_chat
(
    id        UUID DEFAULT uuid_generate_v4(),
    user_1_id UUID,
    user_2_id UUID,

    PRIMARY KEY (id),
    CONSTRAINT unique_private_chat_constraint UNIQUE (user_1_id, user_2_id)
);

CREATE TABLE IF NOT EXISTS private_message
(
    id            UUID      DEFAULT uuid_generate_v4(),
    user_id       UUID,
    chat_id       UUID NOT NULL,
    contents      TEXT NOT NULL,
    creation_time TIMESTAMP DEFAULT NOW(),

    PRIMARY KEY (id),
    CONSTRAINT chat_id_constraint FOREIGN KEY (chat_id) REFERENCES private_chat (id) ON DELETE CASCADE
);