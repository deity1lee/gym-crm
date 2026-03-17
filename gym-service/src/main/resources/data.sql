-- Training types
INSERT INTO training_types (training_type_name)
SELECT 'yoga' WHERE NOT EXISTS (SELECT 1 FROM training_types WHERE training_type_name = 'yoga');

INSERT INTO training_types (training_type_name)
SELECT 'fitness' WHERE NOT EXISTS (SELECT 1 FROM training_types WHERE training_type_name = 'fitness');

INSERT INTO training_types (training_type_name)
SELECT 'pilates' WHERE NOT EXISTS (SELECT 1 FROM training_types WHERE training_type_name = 'pilates');

INSERT INTO training_types (training_type_name)
SELECT 'cardio' WHERE NOT EXISTS (SELECT 1 FROM training_types WHERE training_type_name = 'cardio');

-- Users
INSERT INTO users (first_name, last_name, username, password, is_active, account_non_locked, failed_attempts, lock_time)
SELECT 'John', 'Do', 'John.Do', '$2a$12$bfOlJV7RcPgU3Ii/PjRZLuOCZdeANqRzd0AxsAcVZUGCVtN54FvBu', TRUE, TRUE, 0, NULL
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'John.Do');

INSERT INTO users (first_name, last_name, username, password, is_active, account_non_locked, failed_attempts, lock_time)
SELECT 'John3', 'Doe3', 'John.Doe3', 'pass123453', TRUE, TRUE, 0, NULL
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'John.Doe3');

INSERT INTO users (first_name, last_name, username, password, is_active, account_non_locked, failed_attempts, lock_time)
SELECT 'Lucy', 'Lee', 'Lucy.Lee', 'pass111222', TRUE, TRUE, 0, NULL
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'Lucy.Lee');

INSERT INTO users (first_name, last_name, username, password, is_active, account_non_locked, failed_attempts, lock_time)
SELECT 'Emily', 'Brown', 'Emily.Brown', 'aStrong123', TRUE, TRUE, 0, NULL
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'Emily.Brown');

INSERT INTO users (first_name, last_name, username, password, is_active, account_non_locked, failed_attempts, lock_time)
SELECT 'Joe', 'Perry', 'Joe.Perry', 'Tass123453', TRUE, TRUE, 0, NULL
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'Joe.Perry');

-- Trainees
INSERT INTO trainees (user_id, date_of_birth, address)
SELECT u.id, '1999-05-12', 'NY, USA'
FROM users u
WHERE u.username = 'John.Doe3'
  AND NOT EXISTS (SELECT 1 FROM trainees t WHERE t.user_id = u.id);

INSERT INTO trainees (user_id, date_of_birth, address)
SELECT u.id, '2000-05-12', 'Berlin, Germany'
FROM users u
WHERE u.username = 'Lucy.Lee'
  AND NOT EXISTS (SELECT 1 FROM trainees t WHERE t.user_id = u.id);

-- Trainers
INSERT INTO trainers (user_id, training_type_id)
SELECT u.id, tt.id
FROM users u
         JOIN training_types tt ON tt.training_type_name = 'yoga'
WHERE u.username = 'John.Do'
  AND NOT EXISTS (SELECT 1 FROM trainers tr WHERE tr.user_id = u.id);

INSERT INTO trainers (user_id, training_type_id)
SELECT u.id, tt.id
FROM users u
         JOIN training_types tt ON tt.training_type_name = 'fitness'
WHERE u.username = 'Emily.Brown'
  AND NOT EXISTS (SELECT 1 FROM trainers tr WHERE tr.user_id = u.id);

INSERT INTO trainers (user_id, training_type_id)
SELECT u.id, tt.id
FROM users u
         JOIN training_types tt ON tt.training_type_name = 'pilates'
WHERE u.username = 'Joe.Perry'
  AND NOT EXISTS (SELECT 1 FROM trainers tr WHERE tr.user_id = u.id);

-- Trainer-Trainee relationships
INSERT INTO trainer_trainee (trainee_id, trainer_id)
SELECT tr.user_id, trr.user_id
FROM trainees tr
         JOIN trainers trr ON trr.user_id = (SELECT id FROM users WHERE username = 'John.Do')
WHERE tr.user_id = (SELECT id FROM users WHERE username = 'John.Doe3')
  AND NOT EXISTS (
    SELECT 1 FROM trainer_trainee tt
    WHERE tt.trainee_id = tr.user_id AND tt.trainer_id = trr.user_id
);

INSERT INTO trainer_trainee (trainee_id, trainer_id)
SELECT tr.user_id, trr.user_id
FROM trainees tr
         JOIN trainers trr ON trr.user_id = (SELECT id FROM users WHERE username = 'Joe.Perry')
WHERE tr.user_id = (SELECT id FROM users WHERE username = 'Lucy.Lee')
  AND NOT EXISTS (
    SELECT 1 FROM trainer_trainee tt
    WHERE tt.trainee_id = tr.user_id AND tt.trainer_id = trr.user_id
);

-- Trainings
INSERT INTO trainings (trainee_id, trainer_id, training_name, training_type_id, training_date, training_duration)
SELECT tr.user_id, trr.user_id, 'aaa', tt.id, '2026-05-12', 10
FROM trainees tr
         JOIN trainers trr ON trr.user_id = (SELECT id FROM users WHERE username = 'John.Do')
         JOIN training_types tt ON tt.training_type_name = 'fitness'
WHERE tr.user_id = (SELECT id FROM users WHERE username = 'John.Doe3')
  AND NOT EXISTS (
    SELECT 1 FROM trainings t
    WHERE t.trainee_id = tr.user_id AND t.trainer_id = trr.user_id AND t.training_date = '2026-05-12'
);

INSERT INTO trainings (trainee_id, trainer_id, training_name, training_type_id, training_date, training_duration)
SELECT tr.user_id, trr.user_id, 'bbb', tt.id, '2026-05-12', 20
FROM trainees tr
         JOIN trainers trr ON trr.user_id = (SELECT id FROM users WHERE username = 'John.Do')
         JOIN training_types tt ON tt.training_type_name = 'yoga'
WHERE tr.user_id = (SELECT id FROM users WHERE username = 'Lucy.Lee')
  AND NOT EXISTS (
    SELECT 1 FROM trainings t
    WHERE t.trainee_id = tr.user_id AND t.trainer_id = trr.user_id AND t.training_date = '2026-05-12'
);