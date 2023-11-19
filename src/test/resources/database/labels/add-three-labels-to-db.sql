INSERT INTO labels (id, name, color) VALUES (1, 'White label', 'WHITE');
INSERT INTO tasks_labels (task_id, label_id) VALUES (1, 1);
INSERT INTO tasks_labels (task_id, label_id) VALUES (2, 1);
INSERT INTO labels (id, name, color) VALUES (2, 'Black label', 'BLACK');
INSERT INTO tasks_labels (task_id, label_id) VALUES (1, 2);
INSERT INTO tasks_labels (task_id, label_id) VALUES (3, 2);
INSERT INTO labels (id, name, color) VALUES (3, 'Green label', 'GREEN');
INSERT INTO tasks_labels (task_id, label_id) VALUES (2, 3);
INSERT INTO tasks_labels (task_id, label_id) VALUES (3, 3);
