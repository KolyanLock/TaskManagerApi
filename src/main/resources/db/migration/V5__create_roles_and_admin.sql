INSERT INTO roles (name) VALUES ('ROLE_ADMIN'), ('ROLE_USER');

INSERT INTO users (username, password, enabled)
VALUES ('admin', '$2a$10$j3u5rd4CeYBYUwUuKfBG2.MGGKtSb9ZvByYbvQ8ao8m/zbZSE0lmy', true);

INSERT INTO user_roles (user_id, role_id)
VALUES (1, 1);