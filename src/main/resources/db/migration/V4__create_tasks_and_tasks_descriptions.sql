CREATE TABLE tasks_description (
    id BIGSERIAL PRIMARY KEY,
    text TEXT NOT NULL
);

CREATE TABLE tasks (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description_id BIGINT UNIQUE,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    FOREIGN KEY (description_id) REFERENCES tasks_description(id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
