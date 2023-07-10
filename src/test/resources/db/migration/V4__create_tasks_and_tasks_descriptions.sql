CREATE TABLE tasks_description (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    text CLOB NOT NULL
);

CREATE TABLE tasks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description_id BIGINT UNIQUE,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    FOREIGN KEY (description_id) REFERENCES tasks_description(id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
