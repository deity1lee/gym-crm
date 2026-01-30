INSERT INTO users (first_name, last_name, username, password, is_active, account_non_locked, failed_attempts, lock_time)
VALUES ('John', 'Do', 'John.Do', '$2a$12$bfOlJV7RcPgU3Ii/PjRZLuOCZdeANqRzd0AxsAcVZUGCVtN54FvBu', TRUE, TRUE, 0, NULL);

INSERT INTO users (first_name, last_name, username, password, is_active, account_non_locked, failed_attempts, lock_time)
VALUES ('John3', 'Doe3', 'John.Doe3', 'pass123453', TRUE, TRUE, 0, NULL);

INSERT INTO users (first_name, last_name, username, password, is_active, account_non_locked, failed_attempts, lock_time)
VALUES ('Lucy', 'Lee', 'Lucy.Lee', 'pass111222', TRUE, TRUE, 0, NULL);

INSERT INTO users (first_name, last_name, username, password, is_active, account_non_locked, failed_attempts, lock_time)
VALUES ('Emily', 'Brown', 'Emily.Brown', 'aStrong123', TRUE, TRUE, 0, NULL);

INSERT INTO users (first_name, last_name, username, password, is_active, account_non_locked, failed_attempts, lock_time)
VALUES ('Joe', 'Perry', 'Joe.Perry', 'Tass123453', TRUE, TRUE, 0, NULL);


INSERT INTO training_types (training_type_name) VALUES ('yoga');
INSERT INTO training_types (training_type_name) VALUES ('fitness');
INSERT INTO training_types (training_type_name) VALUES ('pilates');
INSERT INTO training_types (training_type_name) VALUES ('cardio');


INSERT INTO trainees (user_id, date_of_birth, address) VALUES (2, '1999-05-12', 'NY, USA');

INSERT INTO trainees (user_id, date_of_birth, address) VALUES (3, '2000-05-12', 'Berlin, Germany');


INSERT INTO trainers (user_id, training_type_id) VALUES (1, 1);
INSERT INTO trainers (user_id, training_type_id) VALUES (4, 2);
INSERT INTO trainers (user_id, training_type_id) VALUES (5, 3);


INSERT INTO trainings (trainee_id, trainer_id, training_name, training_type_id, training_date, training_duration)
VALUES (2, 1, 'aaa', 2, '2020-05-12', 10);

INSERT INTO trainings (trainee_id, trainer_id, training_name, training_type_id, training_date, training_duration)
VALUES (3, 1, 'bbb', 1, '2020-05-12', 20);


INSERT INTO trainer_trainee (trainee_id, trainer_id) VALUES (2, 1);
INSERT INTO trainer_trainee (trainee_id, trainer_id) VALUES (3, 5);
