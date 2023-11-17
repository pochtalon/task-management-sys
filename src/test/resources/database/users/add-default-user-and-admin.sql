INSERT INTO users (id, nickname, password, email, first_name, last_name)
VALUES (1, 'admin', '$2a$10$pVfjvP8petI6Q.locgLd4ua.JSf.evDRNT0FaeaWtW0eesn9Qk37W', 'admin@mail.com', 'Robert', 'Heinlein');
INSERT INTO users_roles (user_id, role_id) VALUES (1, 2);
INSERT INTO users (id, nickname, password, email, first_name, last_name)
VALUES (2, 'mnemonic', '$2a$10$pVfjvP8petI6Q.locgLd4ua.JSf.evDRNT0FaeaWtW0eesn9Qk37W', 'johnny@mail.com', 'John', 'Smith');
INSERT INTO users_roles (user_id, role_id) VALUES (1, 2);
