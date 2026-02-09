CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       first_name VARCHAR(255) NOT NULL,
                       last_name VARCHAR(255) NOT NULL,
                       username VARCHAR(255) NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       is_active BOOLEAN NOT NULL,
                       failed_attempts INT,
                       lock_time DATETIME,
                       account_non_locked BOOLEAN DEFAULT TRUE
);

CREATE TABLE training_types (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                training_type_name VARCHAR(255) NOT NULL
);

CREATE TABLE trainees (
                          user_id BIGINT PRIMARY KEY,
                          date_of_birth DATE,
                          address VARCHAR(255),
                          CONSTRAINT fk_trainee_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE trainers (
                          user_id BIGINT PRIMARY KEY,
                          training_type_id BIGINT NOT NULL,
                          CONSTRAINT fk_trainer_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                          CONSTRAINT fk_trainer_type FOREIGN KEY (training_type_id) REFERENCES training_types(id)
);

CREATE TABLE trainings (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           trainee_id BIGINT NOT NULL,
                           trainer_id BIGINT NOT NULL,
                           training_name VARCHAR(255) NOT NULL,
                           training_type_id BIGINT NOT NULL,
                           training_date DATE NOT NULL,
                           training_duration BIGINT NOT NULL,
                           CONSTRAINT fk_training_trainee FOREIGN KEY (trainee_id) REFERENCES trainees(user_id),
                           CONSTRAINT fk_training_trainer FOREIGN KEY (trainer_id) REFERENCES trainers(user_id),
                           CONSTRAINT fk_training_type FOREIGN KEY (training_type_id) REFERENCES training_types(id)
);

CREATE TABLE trainer_trainee (
                                 trainee_id BIGINT NOT NULL,
                                 trainer_id BIGINT NOT NULL,
                                 PRIMARY KEY (trainee_id, trainer_id),
                                 CONSTRAINT fk_tt_trainee FOREIGN KEY (trainee_id) REFERENCES trainees(user_id) ON DELETE CASCADE,
                                 CONSTRAINT fk_tt_trainer FOREIGN KEY (trainer_id) REFERENCES trainers(user_id) ON DELETE CASCADE
);

CREATE TABLE workload_outbox (
                                 id BIGINT NOT NULL AUTO_INCREMENT,
                                 transaction_id VARCHAR(255) NOT NULL,
                                 payload TEXT NOT NULL,
                                 status VARCHAR(50) NOT NULL,
                                 created_at DATETIME NOT NULL,
                                 PRIMARY KEY (id)
);
