CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       firstName VARCHAR(255) NOT NULL,
                       lastName VARCHAR(255) NOT NULL,
                       username VARCHAR(255) NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       isActive BOOLEAN NOT NULL
);

CREATE TABLE trainingTypes (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               trainingTypeName VARCHAR(255) NOT NULL
);

CREATE TABLE trainees (
                          user_id BIGINT PRIMARY KEY,
                          dateOfBirth DATE,
                          address VARCHAR(255),
                          CONSTRAINT fk_trainee_user
                              FOREIGN KEY (user_id) REFERENCES users(id)
                                  ON DELETE CASCADE
);

CREATE TABLE trainers (
                          user_id BIGINT PRIMARY KEY,
                          training_type_id BIGINT NOT NULL,
                          CONSTRAINT fk_trainer_user
                              FOREIGN KEY (user_id) REFERENCES users(id)
                                  ON DELETE CASCADE,
                          CONSTRAINT fk_trainer_type
                              FOREIGN KEY (training_type_id) REFERENCES trainingTypes(id)
);

CREATE TABLE trainings (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           trainee_id BIGINT NOT NULL,
                           trainer_id BIGINT NOT NULL,
                           trainingName VARCHAR(255) NOT NULL,
                           training_type_id BIGINT NOT NULL,
                           trainingDate DATE NOT NULL,
                           trainingDuration BIGINT NOT NULL,

                           CONSTRAINT fk_training_trainee
                               FOREIGN KEY (trainee_id) REFERENCES trainees(user_id),

                           CONSTRAINT fk_training_trainer
                               FOREIGN KEY (trainer_id) REFERENCES trainers(user_id),

                           CONSTRAINT fk_training_type
                               FOREIGN KEY (training_type_id) REFERENCES trainingTypes(id)
);

CREATE TABLE trainer_trainee (
                                 trainee_id BIGINT NOT NULL,
                                 trainer_id BIGINT NOT NULL,

                                 PRIMARY KEY (trainee_id, trainer_id),

                                 CONSTRAINT fk_tt_trainee
                                     FOREIGN KEY (trainee_id) REFERENCES trainees(user_id)
                                         ON DELETE CASCADE,

                                 CONSTRAINT fk_tt_trainer
                                     FOREIGN KEY (trainer_id) REFERENCES trainers(user_id)
                                         ON DELETE CASCADE
);
