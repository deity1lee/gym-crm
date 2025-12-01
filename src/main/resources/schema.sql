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
                          id BIGINT PRIMARY KEY,
                          dateOfBirth DATE,
                          address VARCHAR(255),
                          CONSTRAINT fk_trainee_user
                              FOREIGN KEY (id) REFERENCES users(id)
                                  ON DELETE CASCADE
);

CREATE TABLE trainers (
                          id BIGINT PRIMARY KEY,
                          specialization BIGINT NOT NULL,
                          CONSTRAINT fk_trainer_user
                              FOREIGN KEY (id) REFERENCES users(id)
                                  ON DELETE CASCADE,
                          CONSTRAINT fk_trainer_specialization
                              FOREIGN KEY (specialization) REFERENCES trainingTypes(id)
);

CREATE TABLE trainings (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           traineeId BIGINT NOT NULL,
                           trainerId BIGINT NOT NULL,
                           trainingName VARCHAR(255) NOT NULL,
                           trainingTypeId BIGINT NOT NULL,
                           trainingDate DATE NOT NULL,
                           trainingDuration BIGINT NOT NULL,

                           CONSTRAINT fk_training_trainee
                               FOREIGN KEY (traineeId) REFERENCES trainees(id),

                           CONSTRAINT fk_training_trainer
                               FOREIGN KEY (trainerId) REFERENCES trainers(id),

                           CONSTRAINT fk_training_type
                               FOREIGN KEY (trainingTypeId) REFERENCES trainingTypes(id)
);

CREATE TABLE trainer_trainee (
                                 trainee_id BIGINT NOT NULL,
                                 trainer_id BIGINT NOT NULL,

                                 PRIMARY KEY (trainee_id, trainer_id),

                                 CONSTRAINT fk_tt_trainee
                                     FOREIGN KEY (trainee_id) REFERENCES trainees(id)
                                         ON DELETE CASCADE,

                                 CONSTRAINT fk_tt_trainer
                                     FOREIGN KEY (trainer_id) REFERENCES trainers(id)
                                         ON DELETE CASCADE
);