CREATE TABLE IF NOT EXISTS users
(
    id BIGINT NOT NULL,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    nik_name VARCHAR(100),
    chat_id BIGINT,
    CONSTRAINT pk_user PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS message
(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    user_id BIGINT REFERENCES users (id),
    request_text VARCHAR(500),
    response_text VARCHAR(500),
    date_message BIGINT,
    CONSTRAINT pk_message PRIMARY KEY (id)
);